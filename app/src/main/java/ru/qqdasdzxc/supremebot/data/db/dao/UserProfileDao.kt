package ru.qqdasdzxc.supremebot.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.qqdasdzxc.supremebot.data.dto.UserProfile

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM UserProfile WHERE idUser = :id")
    fun getById(id: Long): UserProfile

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(userProfile: UserProfile)
}