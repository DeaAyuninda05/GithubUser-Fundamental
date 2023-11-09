package com.dicoding.githubuser.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dicoding.githubuser.data.database.entity.favoriteUser
import com.dicoding.githubuser.data.database.room.FavoriteDao
import com.dicoding.githubuser.data.retrofit.ApiService
import com.dicoding.githubuser.data.Result
import com.dicoding.githubuser.data.response.UserResponse
import com.dicoding.githubuser.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserRepository(
    private val apiService: ApiService,
    private val mFavoriteDao: FavoriteDao,
    private val appExecutors: AppExecutors
){
    private val result = MediatorLiveData<Result<List<favoriteUser>>>()

    fun getUsers(): LiveData<Result<List<favoriteUser>>>{
        result.value = Result.Loading
        val query = "username"
        val client = apiService.getSearchUsers(query)
        client.enqueue(object : Callback<UserResponse>{
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    val userList = ArrayList<favoriteUser>()

                    userResponse?.items?.forEach { itemsItem ->
                        val user = favoriteUser(
                            username = itemsItem.login.toString(),
                            avatarUrl = itemsItem.avatarUrl.toString()
                        )
                        userList.add(user)
                    }
                    for (user in userList) {
                        Log.d("UserRepository", "Inserted user: ${user.username}, Avatar URL: ${user.avatarUrl}")
                    }
                    appExecutors.diskIO.execute() {
                        for (user in userList) {
                            mFavoriteDao.insertFavoriteUser(user)
                        }
                    }
                    result.value = Result.Success(userList)
                } else {
                    result.value = Result.Error("Gagal mengambil data pengguna")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }

        })

        val localData = mFavoriteDao.getAllFavoriteUsers()
        result.addSource(localData) {newData: List<favoriteUser> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    fun getAllFavoriteUsers(): LiveData<List<favoriteUser>> {
        return mFavoriteDao.getAllFavoriteUsers()
    }
    fun getFavoriteUser(username: String): LiveData<Boolean> {
        return mFavoriteDao.isFavoriteUser(username)
    }

    fun setFavoriteUser(user: favoriteUser, favoriteS: Boolean) {
        user.isFavorite = favoriteS

        appExecutors.diskIO.execute() {
            mFavoriteDao.updateUserFavorite(user)
        }
    }

    fun saveFavoriteUser(user: favoriteUser) {
        Log.d("UserRepository", "Avatar URL to save: ${user.avatarUrl}")

        appExecutors.diskIO.execute {
            val username = user.username
            val avatarUrl = user.avatarUrl

            val existingUser = mFavoriteDao.isFavoriteUser(username ?: "")
            if (existingUser.value == true) {
                mFavoriteDao.updateUserFavorite(user)
            } else {
                mFavoriteDao.insertFavoriteUser(user)
            }
        }
    }

    fun removeUserFromFavorite(user: favoriteUser) {
        appExecutors.diskIO.execute {
            mFavoriteDao.deleteFavoriteUser(user.username)
        }
    }

    fun isUserFavorite(username:String): LiveData<Boolean>{
        return mFavoriteDao.isFavoriteUser(username)
    }

    companion object{
        @Volatile
        private var INSTANCE : UserRepository? = null

        fun getInstance(
            apiService: ApiService,
            mFavoriteDao: FavoriteDao,
            appExecutors: AppExecutors
        ) : UserRepository = INSTANCE ?: synchronized(this){
            INSTANCE ?: UserRepository(apiService, mFavoriteDao, appExecutors)
        }.also { INSTANCE = it }
    }
}