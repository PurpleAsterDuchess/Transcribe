package com.example.transcribe.screens.play

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.transcribe.data.Transcription
import com.example.transcribe.data.TranscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

@HiltViewModel
class PlayViewModel @Inject constructor(
    private val repo: TranscriptionRepository
) : ViewModel() {

    private var mediaPlayer: MediaPlayer? = null

    var isPlayingSong by mutableStateOf(false)
        private set

    fun getTranscriptionById(songId: String?): Flow<Transcription?> {
        val id = songId?.toIntOrNull() ?: return emptyFlow()
        return repo.findById(id)
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

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
