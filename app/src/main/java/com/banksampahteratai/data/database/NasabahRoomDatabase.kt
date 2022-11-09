package com.banksampahteratai.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Nasabah::class], version = 1)
abstract class NasabahRoomDatabase : RoomDatabase() {
    abstract fun nasabahDao(): NasabahDao

    companion object {
        @Volatile
        private var INSTANCE: NasabahRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): NasabahRoomDatabase {
            if (INSTANCE == null) {
                synchronized(NasabahRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        NasabahRoomDatabase::class.java, "nasabah_database")
                        .build()
                }
            }
            return INSTANCE as NasabahRoomDatabase
        }
    }
}