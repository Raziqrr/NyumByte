/**
 * @Author: Raziqrr rzqrdzn03@gmail.com
 * @Date: 2025-06-08 22:28:52
 * @LastEditors: Raziqrr rzqrdzn03@gmail.com
 * @LastEditTime: 2025-06-08 22:29:10
 * @FilePath: app/src/main/java/com/example/nyumbyte/data/network/firebase/AuthDao.kt
 * @Description: 这是默认设置,可以在设置》工具》File Description中进行配置
 */
package com.example.nyumbyte.data.network.firebase

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthDao {
    @Query("SELECT * FROM auth LIMIT 1")
    fun getAuth(): Flow<AuthEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuth(authEntity: AuthEntity)

    @Query("DELETE FROM auth")
    suspend fun clearAuth()
}