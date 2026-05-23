package com.example.transcribe

import com.example.transcribe.data.AuthRepo
import com.example.transcribe.data.RepositoryModule
import com.example.transcribe.data.TranscriptionDAO
import com.example.transcribe.data.TranscriptionRepository
import com.example.transcribe.data.UserRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton
import kotlinx.coroutines.flow.flowOf
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AuthModule::class, RepositoryModule::class]
)
object TestAuthModule {
    @Provides
    @Singleton
    fun provideAuthRepo(): AuthRepo {
        val mock = Mockito.mock(AuthRepo::class.java)
        whenever(mock.authStateFlow).thenReturn(flowOf(null))
        whenever(mock.getUserId()).thenReturn(null)
        whenever(mock.currentUser).thenReturn(null)
        return mock
    }

    @Provides
    @Singleton
    fun provideTranscriptionRepo(): TranscriptionRepository {
        val mock = Mockito.mock(TranscriptionRepository::class.java)
        whenever(mock.getAll()).thenReturn(flowOf(emptyList()))
        return mock
    }

    @Provides
    @Singleton
    fun provideUserRepo(): UserRepo {
        val mock = Mockito.mock(UserRepo::class.java)
        whenever(mock.getUserFlow(any())).thenReturn(flowOf(null))
        whenever(mock.getAll()).thenReturn(flowOf(emptyList()))
        return mock
    }

    @Provides
    @Singleton
    fun provideTranscriptionDao(): TranscriptionDAO = Mockito.mock(TranscriptionDAO::class.java)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Mockito.mock(FirebaseAuth::class.java)

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Mockito.mock(FirebaseFirestore::class.java)
}
