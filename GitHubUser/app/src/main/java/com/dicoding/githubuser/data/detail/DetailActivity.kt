package com.dicoding.githubuser.data.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.database.entity.favoriteUser
import com.dicoding.githubuser.data.repository.UserRepository
import com.dicoding.githubuser.data.favorite.FavoriteViewModel
import com.dicoding.githubuser.data.favorite.FavoriteViewModelFactory
import com.dicoding.githubuser.databinding.ActivityDetailBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    private lateinit var followsFragment: FollowsFragment
    private lateinit var userRepository: UserRepository

    private var userFavorite: favoriteUser? = null
    private val favoriteViewModel by viewModels<FavoriteViewModel>() {
        FavoriteViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting up the ViewPager and TabLayout for follower/following tabs
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabs: TabLayout = findViewById(R.id.tabs)
        val usernames = "user"
        val followersFragment = FollowsFragment.newInstance(usernames, FollowsFragment.FOLLOWERS_VIEW)
        val followingFragment = FollowsFragment.newInstance(usernames, FollowsFragment.FOLLOWING_VIEW)
        val fragmentList = listOf(followersFragment, followingFragment)
        val sectionPagerAdapter = SectionPagerAdapter(this)

        viewPager.adapter = sectionPagerAdapter
        TabLayoutMediator(tabs, viewPager){ tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        val username = intent.getStringExtra(EXTRA_USERNAME)
        val avatarUrl = intent.getStringExtra(EXTRA_AVATAR_URL)

        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        username?.let {
            viewModel.setUserDetail(it)
        }
        username?.let {
            tabConfiguration(it, sectionPagerAdapter)
        }
        showLoading(true)

        val userImage = viewModel.getUserDetail().value?.avatarUrl

        viewModel.getUserDetail().observe(this, { detailUserResponse ->
            if (detailUserResponse != null) {
                binding.tvName.text = detailUserResponse.name ?: "-"
                binding.tvUsername.text =detailUserResponse.login ?: "-"
                binding.tvFollowersLabel.text = "${detailUserResponse.followers} followers"
                binding.tvFollowing.text = "${detailUserResponse.following} following"
                Glide.with(this)
                    .load(detailUserResponse.avatarUrl)
                    .into(binding.userImage)
                showLoading(false)
            }
        })

        favoriteViewModel.isUserFavorite(username ?: "").observe(this) { isFavorite ->
            val fab = binding.fabFavorite

            if (isFavorite) {
                fab.setImageDrawable(
                    ContextCompat.getDrawable(
                        fab.context,
                        R.drawable.ic_favorite_true
                    )
                )
                fab.setOnClickListener {
                    showToast("Removing $username from Favorite")
                    favoriteViewModel.removeUserFromFavorite(username ?: "")
                }
            } else {
                fab.setImageDrawable(
                    ContextCompat.getDrawable(
                        fab.context,
                        R.drawable.ic_favorite_false
                    )
                )
                fab.setOnClickListener {
                    showToast("Adding $username to favorite")
                    favoriteViewModel.saveUserFavorite(favoriteUser(username ?: "", avatarUrl ?: ""))
                }
            }

        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun tabConfiguration(dataUsername: String, sectionPagerAdapter: SectionPagerAdapter){
        val viewPager : ViewPager2 = binding.viewPager
        viewPager.adapter = sectionPagerAdapter
        sectionPagerAdapter.username = dataUsername
        val tabs: TabLayout =binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    private fun showLoading(state: Boolean) { binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"
        const val EXTRA_AVATAR_URL = "extra_avatar_url"
        const val ARG_USERNAME = "arg_username"

        fun newInstance(username: String): FollowsFragment{
            val fragment = FollowsFragment()
            val args = Bundle().apply {
                putString(ARG_USERNAME, username)
            }
            fragment.arguments = args
            return fragment
        }
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }

}