package com.banksampahteratai.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Nasabah::class, DataSampah::class, KategoriSampah::class], version = 1)
abstract class SmartRoomDatabase : RoomDatabase() {
    abstract fun nasabahDao(): NasabahDao
    abstract fun dataSampahDao(): DataSampahDao
    abstract fun kategoriSampahDao(): KategoriSampahDao

    companion object {
        @Volatile
        private var INSTANCE: SmartRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): SmartRoomDatabase {
            if (INSTANCE == null) {
                synchronized(SmartRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        SmartRoomDatabase::class.java, "database")
                        .build()
                }
            }
            return INSTANCE as SmartRoomDatabase
        }
    }
}