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
import com.example.transcribe.data.Transcription
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class PlayViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null
    private val collection = firestore.collection("transcriptions")

    var isPlayingSong by mutableStateOf(false)
        private set

    var currentTranscription by mutableStateOf<Transcription?>(null)
        private set

    fun getTranscriptionById(songId: String?) {
        if (songId == null) return

        viewModelScope.launch(errorHandler) {
            val snapshot = collection.document(songId).get().await()
            currentTranscription = snapshot.toObject<Transcription>()
        }
    }

    fun playAudio(context: Context, fileUriString: String?) {
        if (fileUriString == null) return
        val uri = Uri.parse(fileUriString)

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
            prepareAsync()
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
        viewModelScope.launch(errorHandler) {
            collection.document(transcriptionId).delete().await()
            onDeleted()
        }
    }

    fun updateTranscription(transcription: Transcription) {
        viewModelScope.launch(errorHandler) {
            collection.document(transcription.id).set(transcription).await()
        }
    }

    private val errorHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("PlayViewModel", "Error: ${exception.message}")
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
    }
}