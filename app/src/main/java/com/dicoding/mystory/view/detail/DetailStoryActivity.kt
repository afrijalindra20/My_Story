package com.dicoding.mystory.view.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.mystory.R
import com.dicoding.mystory.data.api.respone.ListStoryItem
import com.dicoding.mystory.databinding.ActivityDetailStoryBinding
import com.dicoding.mystory.di.Injection
import com.dicoding.mystory.view.ViewModelFactory

class DetailStoryActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
        const val EXTRA_STORY = "extra_story"
    }

    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        val storyId = intent.getStringExtra(EXTRA_STORY_ID)
        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY)

        if (story != null) {
            populateStoryDetails(story)
        } else if (storyId != null) {
            viewModel.getStoryDetail(storyId)
            setupObservers()
        } else {
            finish()
        }
    }

    private fun populateStoryDetails(story: ListStoryItem) {
        binding.apply {
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            Glide.with(this@DetailStoryActivity)
                .load(story.photoUrl)
                .into(ivDetailPhoto)

            if (story.lat != 0.0 && story.lon != 0.0) {
                tvDetailLocation.visibility = View.VISIBLE
                tvDetailLocation.text = getString(R.string.location_format, story.lat, story.lon)
            } else {
                tvDetailLocation.visibility = View.GONE
            }
        }
    }

    private fun setupObservers() {
        viewModel.story.observe(this) { story ->
            populateStoryDetails(story)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            if (error.isNotBlank()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}