package com.dicoding.githubuser.data.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: SettingPreferences?) : ViewModel(){

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText
    fun getThemeSettings(): LiveData<Boolean>? {
        return pref?.getThemeSetting()?.asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        val theme = if (isDarkModeActive) "Dark" else "Light"
        _snackbarText.value = "Theme is changed to $theme"

        viewModelScope.launch {
            pref?.saveThemeSetting(isDarkModeActive)
        }
    }

}

