package com.dicoding.githubuser.data.database.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dicoding.githubuser.data.database.entity.favoriteUser

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favoriteUser WHERE isFavorite = 1 ORDER BY username DESC")
    fun getAllFavoriteUsers(): LiveData<List<favoriteUser>>

    @Update
    fun updateUserFavorite(favoriteUser: favoriteUser)

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    fun insertFavoriteUser(user: favoriteUser)

    @Query("DELETE FROM favoriteUser WHERE username = :username")
    fun deleteFavoriteUser(username: String)


    @Query("SELECT EXISTS(SELECT 1 FROM favoriteUser WHERE username = :username AND isFavorite = 1)")
    fun isFavoriteUser(username: String): LiveData<Boolean>
}