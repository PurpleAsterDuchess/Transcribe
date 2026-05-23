package com.example.transcribe

import com.example.transcribe.data.AuthRepo
import com.example.transcribe.data.AuthRepository
import com.example.transcribe.data.UserRepo
import com.example.transcribe.data.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepository: AuthRepository
    ): AuthRepo

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepository: UserRepository
    ): UserRepo

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

        @Provides
        @Singleton
        fun provideFirestore(): FirebaseFirestore = Firebase.firestore
    }
}
