package com.dicoding.githubuser.di

import android.content.Context
import com.dicoding.githubuser.data.database.room.FavoriteDao
import com.dicoding.githubuser.data.database.room.FavoriteDatabase
import com.dicoding.githubuser.data.repository.UserRepository
import com.dicoding.githubuser.data.retrofit.ApiConfig
import com.dicoding.githubuser.data.retrofit.ApiService
import com.dicoding.githubuser.utils.AppExecutors
import kotlin.coroutines.CoroutineContext


object Injection {

    fun providerRepository(
        context: Context
    ): UserRepository {
        val apiService:ApiService = ApiConfig.getApiService()
        val database: FavoriteDatabase = FavoriteDatabase.getDatabase(context!!)
        val dao: FavoriteDao = database.favoriteDao()
        val appExecutors = AppExecutors()
//        val settingPreferences = SettingPreferences(dataStore)

        return  UserRepository.getInstance(apiService, dao, appExecutors)
    }
}

private fun FavoriteDatabase.Companion.getDatabase(context: CoroutineContext): FavoriteDatabase {
    TODO("Not yet implemented")
}
