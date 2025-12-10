package com.superhuman.livesustainably.auth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import com.superhuman.livesustainably.auth.data.AuthRepository
import com.superhuman.livesustainably.auth.data.FirebaseAuthRepository

@InstallIn(ViewModelComponent::class)
@Module
abstract class AuthEventModule {
    @Binds
    abstract fun bindAuthRepository(
        firebaseAuthRepository: FirebaseAuthRepository
    ): AuthRepository
}