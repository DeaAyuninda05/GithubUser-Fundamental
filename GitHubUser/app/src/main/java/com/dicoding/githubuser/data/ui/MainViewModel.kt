package com.dicoding.githubuser.data.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubuser.data.database.entity.favoriteUser
import com.dicoding.githubuser.data.repository.UserRepository
import com.dicoding.githubuser.data.response.ItemsItem
import com.dicoding.githubuser.data.response.UserResponse
import com.dicoding.githubuser.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(
    private val userRepository: UserRepository,
) : ViewModel(){

    private val _users =  MutableLiveData<UserResponse>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _followers = MutableLiveData<List<ItemsItem>>()
    private val _following = MutableLiveData<List<ItemsItem>>()
    private val _favoriteUsers = MutableLiveData<List<favoriteUser>>()

    val users: MutableLiveData<UserResponse> = _users
    val isLoading: LiveData<Boolean> = _isLoading
    val followers: LiveData<List<ItemsItem>> = _followers
    val following: LiveData<List<ItemsItem>> = _following
    val favoriteUser: LiveData<List<favoriteUser>> = _favoriteUsers

    private fun findUser(){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getSearchUsers("sidiqpermana")
        client.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>, response: Response<UserResponse>
            ) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse != null) {
                        if (userResponse.items.isNotEmpty()) {
                            val userItem = userResponse.items[0]
                            val userDetailResponse = UserResponse(
                                totalCount = userResponse.totalCount,
                                incompleteResults = userResponse.incompleteResults,
                                items = listOf(userItem)
                            )
                            _users.value = (userDetailResponse)
                            loadFollowers(userItem.login)
                        }
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e(TAG, "Error: ${t.message.toString()}")
                val userResponse = UserResponse(
                    totalCount = 0,
                    incompleteResults = false,
                    items = emptyList()
                )
                _users.value = userResponse
            }
        })
    }

    fun loadFollowers(username:String){
        _isLoading.value = true

        val client = ApiConfig.getApiService().getFollowers(username)
        client.enqueue(object : Callback<List<ItemsItem>> {
            override fun onResponse(
                call: Call<List<ItemsItem>>, response: Response<List<ItemsItem>>
            ) {
                _isLoading.value = false

                if (response.isSuccessful){
                    _followers.value = response.body()
                }else{
                    _followers.value = emptyList()
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "Eror loading followers : ${t.message}")
            }

        })
    }

    fun loadFollowing(username: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getFollowing(username)
        client.enqueue(object : Callback<List<ItemsItem>>{
            override fun onResponse(
                call: Call<List<ItemsItem>>, response: Response<List<ItemsItem>>
            ) {
                _isLoading.value = false

                if (response.isSuccessful){
                    _following.value = response.body()
                } else{
                    _followers.value = emptyList()
                }
            }

            override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "Eror loading following : ${t.message}")

            }

        })
    }

    init {
        findUser()
    }

    companion object {
        private const val TAG = "MainViewModel"
    }

}

