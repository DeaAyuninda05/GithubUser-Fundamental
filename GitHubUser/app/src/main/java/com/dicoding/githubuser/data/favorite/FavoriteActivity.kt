package com.dicoding.githubuser.data.favorite

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.detail.DetailActivity

class FavoriteActivity : AppCompatActivity() {

    private lateinit var favoriteAdapter: RecyclerViewFavoriteAdapter
    val favoriteViewModel: FavoriteViewModel by viewModels {
        FavoriteViewModelFactory.getInstance(this)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        val recyclerView: RecyclerView = findViewById(R.id.rvUserFavorite)
        favoriteAdapter = RecyclerViewFavoriteAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = favoriteAdapter

        favoriteViewModel.getAllFavoriteUsers().observe(this, { users ->
            favoriteAdapter.submitList(users)
            if (users.isEmpty()) {
                Toast.makeText(this, "Tidak ada pengguna favorit.", Toast.LENGTH_SHORT).show()
            }
        })

        favoriteAdapter.setOnItemClickCallback { users ->
            val intent = Intent(this@FavoriteActivity, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_USERNAME, users.username)
            intent.putExtra(DetailActivity.EXTRA_AVATAR_URL, users.avatarUrl)
            startActivity(intent)
        }

    }
}