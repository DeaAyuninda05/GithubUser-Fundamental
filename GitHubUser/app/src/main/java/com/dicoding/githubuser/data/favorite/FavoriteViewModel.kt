package com.dicoding.githubuser.data.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubuser.data.database.entity.favoriteUser
import com.dicoding.githubuser.data.repository.UserRepository


class FavoriteViewModel(  private val userRepository: UserRepository
    ) : ViewModel() {

    val favoriteUsers: LiveData<List<favoriteUser>> = userRepository.getAllFavoriteUsers()

    fun getAllFavoriteUsers(): LiveData<List<favoriteUser>> {
        return userRepository.getAllFavoriteUsers()
    }

//    fun saveUserFavorite(username: favoriteUser) {
//        username.isFavorite = true
//        userRepository.saveFavoriteUser(username)
//    }
    fun saveUserFavorite(user: favoriteUser) {
        user.isFavorite = true
        userRepository.saveFavoriteUser(user)
    }

    fun removeUserFromFavorite(username: String?) {
        username?.let {
            val user = favoriteUser(username = it, isFavorite = false)
            userRepository.removeUserFromFavorite(user)
        }
    }

    fun isUserFavorite(username: String): LiveData<Boolean> {
        return userRepository.isUserFavorite(username)
    }


    fun getfavoriteUsersLive(): LiveData<List<favoriteUser>>{
        return favoriteUsers
    }

}