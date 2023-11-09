package com.dicoding.githubuser.data.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.githubuser.data.response.DetailUserResponse
import com.dicoding.githubuser.data.response.ItemsItem
import com.dicoding.githubuser.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Response

class DetailViewModel : ViewModel() {
    private val user = MutableLiveData<DetailUserResponse>()
    private val followers = MutableLiveData<List<ItemsItem>> ()
    private val following = MutableLiveData<List<ItemsItem>>()
    private val followersCount = MutableLiveData<Int>()
    private val followingCount = MutableLiveData<Int>()

    fun setUserDetail(username:String) {
        ApiConfig.getApiService()
            .getDetailUser(username)
            .enqueue(object : retrofit2.Callback<DetailUserResponse> {
                override fun onResponse(
                    call: Call<DetailUserResponse>,
                    response: Response<DetailUserResponse>
                ) {
                    if (response.isSuccessful) {
                        user.postValue(response.body())
                        val detailUserResponse = response.body()
                        detailUserResponse?.let {
                            val avatarUrl = it.avatarUrl
                            Log.d("DetailViewModel", "Avatar URL: $avatarUrl")
                        }
                    }
                }

                override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                    t.message?.let { Log.d("Failure", it) }
                }

            })
    }
    fun getUserDetail(): LiveData<DetailUserResponse> {
            return user
        }

    fun setFollowers(username: String) {
        ApiConfig.getApiService()
            .getFollowers(username)
            .enqueue(object : retrofit2.Callback<List<ItemsItem>> {
                override fun onResponse(
                    call: Call<List<ItemsItem>>,
                    response: Response<List<ItemsItem>>
                ) {
                    if (response.isSuccessful) {
                        val followersList = response.body() ?: emptyList()
                        followers.postValue(followersList)

                        // Log jumlah pengikut (followers)
                        val followersCount = followersList.size
                        Log.d("DetailViewModel", "Followers Count: $followersCount")
                    }
                }
                override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                    t.message?.let { Log.d("Failure", it)}
                }

            })
    }
    fun getFollowers() : LiveData<List<ItemsItem>>{
        return followers
    }

    fun setFollowing(username: String) {
        ApiConfig.getApiService()
            .getFollowing(username)
            .enqueue(object : retrofit2.Callback<List<ItemsItem>> {
                override fun onResponse(
                    call: Call<List<ItemsItem>>,
                    response: Response<List<ItemsItem>>
                ) {
                    if (response.isSuccessful) {
                        val followingList = response.body() ?: emptyList()
                        following.postValue(followingList)

                        // Log jumlah yang diikuti (following)
                        val followingCount = followingList.size
                        Log.d("DetailViewModel", "Following Count: $followingCount")


                    }
                }

                override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
                    t.message?.let { Log.d("Failure", it)}
                }

            })
    }
    fun getFollowing(): LiveData<List<ItemsItem>>{
        return following
    }

}