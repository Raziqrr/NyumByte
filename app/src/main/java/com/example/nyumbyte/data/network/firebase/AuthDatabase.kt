/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-08 22:29:23
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-08 22:29:33
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/network/firebase/AuthDatabase.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.data.network.firebase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AuthEntity::class], version = 1)
abstract class AuthDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao

    companion object {
        @Volatile
        private var INSTANCE: AuthDatabase? = null

        fun getInstance(context: Context): AuthDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AuthDatabase::class.java,
                    "auth_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}