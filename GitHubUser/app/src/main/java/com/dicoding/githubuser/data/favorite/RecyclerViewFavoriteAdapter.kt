package com.dicoding.githubuser.data.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.githubuser.R
import com.dicoding.githubuser.data.database.entity.favoriteUser
import com.dicoding.githubuser.databinding.ItemUserBinding

class RecyclerViewFavoriteAdapter:
    ListAdapter<favoriteUser, RecyclerViewFavoriteAdapter.ViewHolder>(RecyclerViewFavoriteAdapter.DIFF_CALLBACK) {
    private var onItemClickCallback: ((favoriteUser) -> Unit)? = null

    fun setOnItemClickCallback(callback: (favoriteUser) -> Unit ) {
        onItemClickCallback = callback
    }

    inner class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: favoriteUser) {
            binding.tvItemName.text = user.username.toString()
            Glide.with(binding.root)
                .load(user.avatarUrl)
                .placeholder(R.drawable.placeholder)
                .into(binding.imgItemPhoto)

            binding.cardView.setOnClickListener{
                onItemClickCallback?.invoke(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<favoriteUser>() {
            override fun areItemsTheSame(oldItem: favoriteUser, newItem: favoriteUser): Boolean {
                return oldItem.username == newItem.username
            }

            override fun areContentsTheSame(oldItem: favoriteUser, newItem: favoriteUser): Boolean {
                return oldItem == newItem
            }

        }
    }

}