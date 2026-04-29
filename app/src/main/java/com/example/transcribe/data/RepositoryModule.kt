package com.example.transcribe.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.UUID
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideTranscriptionRepository(): TranscriptionRepository<Transcription> {
        val repository = InMemoryRepository()

        // Add your sample records here during the initial creation
        repository.insert(Transcription(UUID.randomUUID(), "Title1", "Author1", "fileUri1"))
        repository.insert(Transcription(UUID.randomUUID(), "Title2", "Author2", "fileUri2"))
        repository.insert(Transcription(UUID.randomUUID(), "Title3", "Author3", "fileUri3"))

        return repository
    }
}