package com.dicoding.mystory.view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.mystory.data.api.respone.ListStoryItem
import com.dicoding.mystory.databinding.ItemStoryBinding

class StoryAdapter(private val onClick: (ListStoryItem, Int) -> Unit) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    private val stories = mutableListOf<ListStoryItem>()

    fun setStories(newStories: List<ListStoryItem>) {
        stories.clear()
        stories.addAll(newStories)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stories[position], position, onClick)
    }

    override fun getItemCount(): Int = stories.size

    class ViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem, position: Int, onClick: (ListStoryItem, Int) -> Unit) {
            binding.apply {
                tvItemName.text = story.name
                tvDetailDescription.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(ivItemPhoto)

                root.setOnClickListener {
                    onClick(story, position)
                }
            }
        }
    }
}