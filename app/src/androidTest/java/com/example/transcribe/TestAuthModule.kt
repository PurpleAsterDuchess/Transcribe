package com.example.transcribe

import com.example.transcribe.data.AuthRepo
import com.example.transcribe.data.TranscriptionRepository
import com.example.transcribe.data.UserRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import org.mockito.Mockito

@Module
@InstallIn(SingletonComponent::class)
object TestAuthModule {
    @Provides
    @Singleton
    fun provideAuthRepo(): AuthRepo = Mockito.mock(AuthRepo::class.java)
    @Provides
    @Singleton
    fun provideTranscriptionRepo(): TranscriptionRepository = Mockito.mock(TranscriptionRepository::class. java)
    @Provides
    @Singleton
    fun provideUserRepo(): UserRepo = Mockito.mock(UserRepo::class.java)
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Mockito.mock(FirebaseAuth::class.java)
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Mockito.mock(FirebaseFirestore::class. java)
}