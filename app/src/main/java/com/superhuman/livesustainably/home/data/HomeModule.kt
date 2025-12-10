package com.superhuman.livesustainably.home.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    @Singleton
    fun provideHomeRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): HomeRepository {
        return HomeRepository(firestore, auth)
    }
}
