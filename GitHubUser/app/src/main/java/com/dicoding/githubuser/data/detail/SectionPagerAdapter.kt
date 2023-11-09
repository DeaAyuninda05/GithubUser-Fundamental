package com.dicoding.githubuser.data.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SectionPagerAdapter(activity: FragmentActivity) :FragmentStateAdapter(activity) {

    internal var username: String = ""

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FollowsFragment.newInstance(username, FollowsFragment.FOLLOWERS_VIEW)
            1 -> FollowsFragment.newInstance(username, FollowsFragment.FOLLOWING_VIEW)
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
    override fun getItemCount(): Int {
        return 2
    }
}