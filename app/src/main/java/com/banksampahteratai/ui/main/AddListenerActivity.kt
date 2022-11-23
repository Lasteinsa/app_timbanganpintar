package com.banksampahteratai.ui.main

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.banksampahteratai.R
import com.banksampahteratai.data.Const.Companion.KATEGORI_SAMPAH
import com.banksampahteratai.data.Const.Companion.LIST_HARGA_SAMPAH
import com.banksampahteratai.data.Const.Companion.SAMPAH
import com.banksampahteratai.data.Const.Companion.SAMPAH_SHOW
import com.banksampahteratai.data.Utility
import com.banksampahteratai.data.model.KategoriSampahModel
import com.banksampahteratai.data.model.SampahModel
import com.banksampahteratai.data.model.SampahShow
import com.banksampahteratai.data.model.TransaksiData
import com.banksampahteratai.databinding.ActivityAddListenerBinding
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddListenerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddListenerBinding
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private lateinit var utility: Utility
    private lateinit var bounceAnim: Animation
    private lateinit var mConnectedThread: ConnectedThread
    private lateinit var btSocket: BluetoothSocket
    private lateinit var executor: ExecutorService
    private val handler = Handler(Looper.getMainLooper())
    private var listHargaSampah: ArrayList<SampahModel> = ArrayList()
    private var kategoriSampah: ArrayList<KategoriSampahModel> = ArrayList()
    private var sampah: ArrayList<TransaksiData> = ArrayList()
    private var sampahShow: ArrayList<SampahShow> = ArrayList()
    private var namaJenis: String = ""
    private var beratSampah: Double = 5.050
    val listKategori = mutableListOf<String?>()
    val listJenis    = mutableListOf<String>()

    // Bluetooth shit
    private lateinit var myBluetooth: BluetoothAdapter

    private var myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddListenerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        utility = Utility()
        executor = Executors.newSingleThreadExecutor()

        setupAnimation()
        setupKategori()
        setupAction()
        binding.loadingBt.alpha = 1F
        binding.btListenText.alpha = 0F
        executor.execute {
            bluetoothListen()
        }
        binding.inputJumlah.setText(beratSampah.toString())
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                utility.showSnackbar(this@AddListenerActivity,binding.root, "Your Action will cause an damn error", true)
            }
        })
    }

    private fun setupAnimation() {
        bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce)
    }

    private fun setupKategori() {
        val getListHargaSampah = intent.extras?.getParcelableArrayList<SampahModel>(LIST_HARGA_SAMPAH)
        val getKategoriSampah = intent.extras?.getParcelableArrayList<KategoriSampahModel>(KATEGORI_SAMPAH)

        getListHargaSampah?.forEach {
            listHargaSampah.add(SampahModel(it?.id, it?.idKategori, it?.kategori, it?.jenis, it?.harga?.toInt(), it?.hargaPusat?.toInt(), it?.jumlah?.toDouble()))
        }

        getKategoriSampah?.forEach {
            kategoriSampah.add(KategoriSampahModel(it?.id, it?.name, it?.created_at))
            listKategori.add(it.name)
        }

        val listKategoriAdapter = ArrayAdapter(this,androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listKategori )
        listKategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.kategoriSampah.adapter = listKategoriAdapter

        arrayAdapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listJenis )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.jenisSampah.adapter = arrayAdapter

        binding.kategoriSampah.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedKategori = binding.kategoriSampah.selectedItem
                listJenis.clear()
                getListHargaSampah?.forEach {
                    if(it.kategori == selectedKategori) {
                        listJenis.add(it.jenis.toString())
                    }
                }
                arrayAdapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                utility.showSnackbar(this@AddListenerActivity, binding.root, getString(R.string.please_choose), true)
            }

        }
    }

    private fun bluetoothListen() {
        try {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();

            val hc: BluetoothDevice = myBluetooth.getRemoteDevice("00:19:08:35:D8:69")
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            btSocket = hc.createInsecureRfcommSocketToServiceRecord(myUUID)
            btSocket.connect();
            mConnectedThread = ConnectedThread(btSocket, binding.btListenText)
            mConnectedThread.start()
            handler.post {
                binding.loadingBt.alpha = 0F
                binding.btListenText.alpha = 1F
            }
        }
        catch (e: IOException) {
            utility.showSnackbar(this, binding.root, "Failed to init Bluetooth", true)
            closeTheBt()
            val intent = Intent(this@AddListenerActivity, ScaleActivity::class.java)
            setResult(Activity.RESULT_CANCELED,intent)
            finish()
        }
    }

    private fun setupAction() {
        binding.submitIt.setOnClickListener {
            it.startAnimation(bounceAnim)
            val intent = Intent(this@AddListenerActivity, ScaleActivity::class.java)

            namaJenis = binding.jenisSampah.selectedItem.toString()

            beratSampah = binding.inputJumlah.text.toString().toDouble()

            closeTheBt()

            listHargaSampah.forEach {
                if(it.jenis == namaJenis) {
                    val jenisSampahShow     = it.jenis
                    val jumlahSampahShow    = beratSampah
                    val hargaSampahShow     = it.harga!!
                    val totalHargaShow      = jumlahSampahShow * hargaSampahShow
                    val newDataSampah = TransaksiData(it.id, beratSampah.toString())
                    val newSampahShow = SampahShow(jenisSampahShow, jumlahSampahShow, hargaSampahShow, totalHargaShow)

                    sampah.add(newDataSampah)
                    sampahShow.add(newSampahShow)
                }
            }
            intent.putExtra(SAMPAH, sampah)
            intent.putExtra(SAMPAH_SHOW, sampahShow)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
        binding.btnEditBerat.setOnClickListener {
            it.startAnimation(bounceAnim)
            val isBeratEnabled = binding.inputJumlah.isEnabled
            binding.inputJumlah.isEnabled = !isBeratEnabled
        }
        binding.btListen.setOnClickListener {
            it.startAnimation(bounceAnim)
            binding.inputJumlah.setText(binding.btListenText.text)
        }

        binding.cancelIt.setOnClickListener {
            it.startAnimation(bounceAnim)
            closeTheBt()
            finish()
        }
    }

    private fun closeTheBt() {
        if(this::mConnectedThread.isInitialized && this::btSocket.isInitialized) {
            mConnectedThread.interrupt()
            btSocket.close()
        }
    }
}

private class ConnectedThread(socket: BluetoothSocket, view: TextView) : Thread() {
    private val mmInStream: InputStream?
    private var executor: ExecutorService
    private var textView: TextView
    private val handler = Handler(Looper.getMainLooper())
    init {
        var tmpIn: InputStream? = null
        executor = Executors.newSingleThreadExecutor()
        try {
            tmpIn = socket.inputStream
        } catch (e: IOException) {

        }
        mmInStream = tmpIn
        textView = view
    }
    override fun run() {
        executor.execute {
            val buffer = ByteArray(256)
            var bytes: Int

            // Keep the damn looping
            while (true) {
                try {
                    bytes = mmInStream!!.read(buffer)
                    val readMessage = String(buffer, 0, bytes)
                    Log.d("output", readMessage)
                    handler.post {
                        textView.text = readMessage
                    }

                } catch (e: IOException) {
                    Log.d("error", e.toString())
                    break
                }
            }
        }
    }
}