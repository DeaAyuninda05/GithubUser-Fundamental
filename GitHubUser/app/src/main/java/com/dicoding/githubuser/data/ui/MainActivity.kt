package com.dicoding.githubuser.data.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.database.room.FavoriteDatabase
import com.dicoding.githubuser.data.detail.DetailActivity
import com.dicoding.githubuser.data.favorite.FavoriteActivity
import com.dicoding.githubuser.data.response.ItemsItem
import com.dicoding.githubuser.data.retrofit.ApiConfig
import com.dicoding.githubuser.data.setting.SettingActivity
import com.dicoding.githubuser.data.setting.SettingPreferences
import com.dicoding.githubuser.data.setting.SettingViewModel
import com.dicoding.githubuser.data.setting.dataStore
import com.dicoding.githubuser.databinding.ActivityMainBinding
import com.google.android.material.search.SearchView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userAdapter: UserAdapter
    lateinit var searchView: SearchView
    private lateinit var pref: SettingPreferences

    private lateinit var favoriteDatabase: FavoriteDatabase
    val viewModel: SettingViewModel by viewModels {
        ViewModelFactory(pref)
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val GITHUB_USERNAME = "user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setting
        val pref = SettingPreferences.getInstance(application.dataStore)
        val themeSettingLiveData = pref.getThemeSetting().asLiveData()

        themeSettingLiveData.observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        userAdapter = UserAdapter()
        userAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ItemsItem) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_USERNAME, data.login)
                intent.putExtra(DetailActivity.EXTRA_AVATAR_URL, data.avatarUrl)
                startActivity(intent)
            }
        })

        val layoutManager = LinearLayoutManager(this)
        binding.rvUser.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvUser.addItemDecoration(itemDecoration)
        binding.rvUser.adapter = userAdapter

        searchView = findViewById(R.id.searchView)
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = searchView.text.toString()
                    performSearch(query)
                    searchView.clearFocus()
                    true
                } else {
                    false
                }
            }
            findUser()
        }

        binding.topAppBar.setOnMenuItemClickListener{ menuItem ->
            when (menuItem.itemId) {
                R.id.menu_favorite -> {
                    startActivity(Intent(this, FavoriteActivity::class.java))
                    true
                }
                R.id.menu_setting -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    private fun findUser() {
        showLoading(true)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response =
                    ApiConfig.getApiService().getSearchUsers(GITHUB_USERNAME).execute()

                runOnUiThread {
                    showLoading(false)
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        if (userResponse != null) {
                            val userList = userResponse.items
                            userAdapter.setList(userList)
                        }
                    } else {
                        showError("Gagal mengambil data")
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showLoading(false)
                    showError("Kesalahan jaringan")
                }
            }
        }
    }

    private fun performSearch(query: String) {
        showLoading(true)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = ApiConfig.getApiService().getSearchUsers(query).execute()

                runOnUiThread {
                    showLoading(false)
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        if (userResponse != null) {
                            val userList = userResponse.items
                            userAdapter.setList(userList)
                            userAdapter.notifyDataSetChanged()
                        }
                    } else {
                        showError("Gagal mengambil data")
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showLoading(false)
                    showError("Kesalahan jaringan")
                }
            }
        }
    }

    private fun showLoading(state: Boolean) { binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        searchView.visibility = View.VISIBLE
    }

}