package com.dicoding.githubuser.data.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.githubuser.data.response.ItemsItem
import com.dicoding.githubuser.data.response.UserDiffCallback
import com.dicoding.githubuser.databinding.ItemUserBinding

class RecyclerViewFollowAdapter (
    private var userList : List<ItemsItem>
): RecyclerView.Adapter<RecyclerViewFollowAdapter.UserViewHolder>(){

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind (user: ItemsItem){
                binding.tvItemName.text = user.login.toString()
                Glide.with(binding.root)
                    .load(user.avatarUrl.toString())
                    .into(binding.imgItemPhoto)
            }
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int
    ): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: UserViewHolder,
        position: Int
    ) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return userList.size

    }

    fun setList(updatedList: List<ItemsItem>) {
//        userList = updatedList
//        notifyDataSetChanged()
        val diffResult = DiffUtil.calculateDiff(UserDiffCallback(userList, updatedList))
        userList = updatedList
        diffResult.dispatchUpdatesTo(this)
    }
}