package com.example.transcribe.screens.play

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribe.data.AuthRepo
import com.example.transcribe.data.Transcription
import com.example.transcribe.data.UserRepo
import com.example.transcribe.data.TranscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.io.File

@HiltViewModel
class PlayViewModel @Inject constructor(
    private val repo: TranscriptionRepository,
    private val userRepo: UserRepo,
    private val authRepo: AuthRepo
) : ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("PlayViewModel", "Error: ${exception.message}")
    }

    private var mediaPlayer: MediaPlayer? = null

    var isPlayingSong by mutableStateOf(false)
        private set

    var currentTranscription by mutableStateOf<Transcription?>(null)
        private set
    fun getTranscriptionById(songId: String?) {
        if (songId.isNullOrBlank()) return

        viewModelScope.launch(errorHandler) {
            val transcription = repo.getById(songId)
            if (transcription != null) {
                val userId = authRepo.getUserId()
                if (!userId.isNullOrBlank()) {
                    val user = userRepo.getById(userId)
                    if (user != null) {
                        transcription.author = "${user.firstName} ${user.surname}".trim()
                        repo.edit(transcription)
                        
                        userRepo.addRecentTranscription(userId, transcription)
                    }
                }
                currentTranscription = transcription
            }
        }
    }

    fun playAudio(context: Context, fileUriString: String?) {
        if (fileUriString.isNullOrBlank()) {
            Log.e("PlayViewModel", "No file URI provided")
            return
        }
        
        val uri = Uri.parse(fileUriString)

        if (uri.scheme == "file") {
            val file = File(uri.path ?: "")
            if (!file.exists()) {
                Log.e("PlayViewModel", "Audio file missing from storage: ${file.absolutePath}")
                return
            }
        }

        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(context, uri)
                setOnPreparedListener { mp ->
                    mp.start()
                    isPlayingSong = true
                }
                setOnCompletionListener {
                    isPlayingSong = false
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("PlayViewModel", "MediaPlayer error: $what, $extra")
                    isPlayingSong = false
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e("PlayViewModel", "Playback failed: ${e.message}")
            isPlayingSong = false
            mediaPlayer = null
        }
    }

    fun stopAudio() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        isPlayingSong = false
    }

    fun deleteTranscription(transcriptionId: String, onDeleted: () -> Unit) {
        if (transcriptionId.isBlank()) return
        viewModelScope.launch(errorHandler) {
            val userId = authRepo.getUserId()
            if (!userId.isNullOrBlank()) {
                userRepo.removeRecentTranscription(userId, transcriptionId)
            }
            repo.delete(transcriptionId)
            onDeleted()
        }
    }

    fun updateTranscription(transcription: Transcription) {
        if (transcription.id.isBlank()) return
        viewModelScope.launch(errorHandler) {
            repo.edit(transcription)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }
}
