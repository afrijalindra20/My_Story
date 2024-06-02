package com.dicoding.mystory.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystory.data.database.UserRepository
import com.dicoding.mystory.data.database.LoginRepository
import com.dicoding.mystory.data.database.RegisterRepository
import com.dicoding.mystory.data.database.StoryRepository
import com.dicoding.mystory.data.database.UploadStoryRepository
import com.dicoding.mystory.di.Injection
import com.dicoding.mystory.view.detail.DetailViewModel
import com.dicoding.mystory.view.login.LoginViewModel
import com.dicoding.mystory.view.main.MainViewModel
import com.dicoding.mystory.view.signup.SignupViewModel
import com.dicoding.mystory.view.upload.UploadStoryViewModel

class ViewModelFactory(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository,
    private val registerRepository: RegisterRepository,
    private val loginRepository: LoginRepository,
    private val uploadStoryRepository: UploadStoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(storyRepository, userRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(loginRepository, userRepository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(registerRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(UploadStoryViewModel::class.java) -> {
                UploadStoryViewModel(uploadStoryRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

        companion object {
            @Volatile
            private var INSTANCE: ViewModelFactory? = null

            fun getInstance(context: Context): ViewModelFactory {
                return INSTANCE ?: synchronized(this) {
                    val instance = ViewModelFactory(
                        Injection.provideRepository(context),
                        Injection.provideUserRepository(context),
                        Injection.provideRegisterRepository(context),
                        Injection.provideLoginRepository(context),
                        Injection.provideUploadStoryRepository(context)
                    )
                    INSTANCE = instance
                    instance
                }
            }
        }
    }