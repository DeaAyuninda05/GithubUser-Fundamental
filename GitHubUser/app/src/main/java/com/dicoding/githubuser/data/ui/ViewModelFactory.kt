package com.dicoding.githubuser.data.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.githubuser.data.setting.SettingPreferences
import com.dicoding.githubuser.data.setting.SettingViewModel

class ViewModelFactory(
    private val pref: SettingPreferences
) : ViewModelProvider.Factory {

    companion object{
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        fun getInstance(pref: SettingPreferences?, context: Context?): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = pref?.let { ViewModelFactory(it) }
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }

    @Suppress("UNCHEKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)

    }

}