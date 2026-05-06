package com.example.transcribe.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideTranscriptionDB(@ApplicationContext context: Context): TranscriptionDB {
        return TranscriptionDB.getDatabase(context)
    }

    @Provides
    fun provideTranscriptionDao(database: TranscriptionDB): TranscriptionDAO {
        return database.transcriptionDAO()
    }

    @Provides
    @Singleton
    fun provideLocalRepo(transcriptionDAO: TranscriptionDAO): TranscriptionRepository {
        return LocalTranscriptionRepository(transcriptionDAO)
    }
}