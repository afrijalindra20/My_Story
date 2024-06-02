package com.dicoding.mystory.view.main

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mystory.databinding.ActivityMainBinding
import com.dicoding.mystory.view.ViewModelFactory
import com.dicoding.mystory.view.detail.DetailStoryActivity
import com.dicoding.mystory.view.upload.UploadActivity

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter

    private val uploadStoryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Refresh daftar cerita
            viewModel.getStories()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            uploadStoryResultLauncher.launch(intent)
        }

        setupView()
        setupRecyclerView()
        playAnimation()
        viewModel.getStories()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupRecyclerView() {
        adapter = StoryAdapter { story, position ->
            val intent = Intent(this, DetailStoryActivity::class.java)
            intent.putExtra(DetailStoryActivity.EXTRA_STORY, story)
            startActivity(intent)
        }
        binding.storyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.storyRecyclerView.adapter = adapter

        viewModel.stories.observe(this) { stories ->
            adapter.setStories(stories)
        }
        viewModel.isLoading.observe(this) { isLoading ->
            // Tampilkan indikator loading jika sedang memuat data
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            if (error.isNotBlank()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun playAnimation() {

    }
}