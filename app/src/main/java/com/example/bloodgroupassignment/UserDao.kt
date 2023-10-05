package com.example.bloodgroupassignment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insert(item: User)

    @Query("SELECT * FROM user_list WHERE bloodGroup = :search_term")
    suspend fun get(search_term: String): List<User>

    @Query("DELETE FROM user_list WHERE name = :name")
    suspend fun delete(name: String)
}
