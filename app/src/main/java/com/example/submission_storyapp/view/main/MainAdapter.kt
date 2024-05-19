package com.example.submission_storyapp.view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submission_storyapp.data.api.responses.ListStoryItem
import com.example.submission_storyapp.data.preference.UserModel
import com.example.submission_storyapp.databinding.ItemStoriesBinding
import com.example.submission_storyapp.view.detail.DetailActivity

class MainAdapter : ListAdapter<ListStoryItem, MainAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = getItem(position)
        holder.bind(result)
    }

    class ViewHolder(private var binding: ItemStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listStoryItem: ListStoryItem) {
            with(binding) {
                tvItemName.text = listStoryItem.name
                tvItemDate.text = listStoryItem.createdAt
                tvItemDescription.text = listStoryItem.description
                Glide
                    .with(itemView.context)
                    .load(listStoryItem.photoUrl)
                    .into(ivItemPhoto)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.EXTRA_ITEM, listStoryItem)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}