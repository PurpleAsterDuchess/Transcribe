package com.example.transcribe.data

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.google.firebase.firestore.FirebaseFirestore

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTranscriptionRepository(
        impl: LocalTranscriptionRepository
    ): TranscriptionRepository

    companion object {
        @Provides
        @Singleton
        fun provideTranscriptionDao(firestore: FirebaseFirestore): TranscriptionDAO {
            return TranscriptionDAO(firestore)
        }
    }
}