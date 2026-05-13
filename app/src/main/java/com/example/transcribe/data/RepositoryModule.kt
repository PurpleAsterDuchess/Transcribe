package com.example.transcribe.data

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.example.transcribe.data.TranscriptionDAO

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTranscriptionRepository(
        impl: LocalTranscriptionRepository
    ): TranscriptionRepository

    companion object {
//        @Provides
//        @Singleton
//        fun provideFirestore(): FirebaseFirestore = Firebase.firestore

        @Provides
        @Singleton
        fun provideTranscriptionDao(firestore: FirebaseFirestore): TranscriptionDAO {
            return TranscriptionDAO(firestore)
        }

//        @Provides
//        @Singleton
//        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    }
}