package com.example.transcribe

import android.content.Context
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalTranscriptionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var interpreter: Interpreter? = null
    private val mutex = Mutex()

    private suspend fun ensureModelLoaded() {
        if (interpreter == null) {
            mutex.withLock {
                if (interpreter == null) {
                    withContext(Dispatchers.IO) {
                        try {
                            val modelBuffer = loadModelFile()
                            interpreter = Interpreter(modelBuffer)
                            Log.d("LocalTranscription", "Model loaded successfully.")
                        } catch (e: Exception) {
                            Log.e("LocalTranscription", "Failed to load TFLite model: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd("nmp.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    suspend fun transcribeLocal(fileUri: Uri): TranscriptionResponse = withContext(Dispatchers.Default) {
        ensureModelLoaded()
        val model = interpreter ?: return@withContext TranscriptionResponse(
            success = false,
            pdf_url = null,
            voice_url = null,
            others_url = null,
            score_image_url = null,
            notes = null,
            msg = "Model failed to initialize"
        )

        try {
            Log.d("LocalTranscription", "Running local inference...")
            val inputBuffer = ByteBuffer.allocateDirect(1 * 43844 * 1 * 4).order(ByteOrder.nativeOrder())
            
            val outputs = mutableMapOf<Int, Any>()
            for (i in 0 until model.outputTensorCount) {
                val shape = model.getOutputTensor(i).shape()
                outputs[i] = Array(shape[0]) { Array(shape[1]) { FloatArray(shape[2]) } }
            }

            model.runForMultipleInputsOutputs(arrayOf(inputBuffer), outputs)

            // Simulate parsing notes from the TFLite output for visualization
            // In a real app, this would involve 'Basic Pitch' post-processing
            val dummyNotes = listOf(
                NoteEvent(60, 0.5f, 1.0f, 0.5f, 80),
                NoteEvent(64, 1.2f, 1.8f, 0.6f, 85),
                NoteEvent(67, 2.0f, 3.0f, 0.8f, 90)
            )

            val midiFile = File(context.filesDir, "local_${System.currentTimeMillis()}.mid")
            midiFile.createNewFile()

            TranscriptionResponse(
                success = true,
                pdf_url = null,
                voice_url = null,
                others_url = Uri.fromFile(midiFile).toString(),
                score_image_url = null,
                notes = dummyNotes,
                msg = "Processed locally"
            )
        } catch (e: Exception) {
            Log.e("LocalTranscription", "Inference error: ${e.message}")
            TranscriptionResponse(
                success = false,
                pdf_url = null,
                voice_url = null,
                others_url = null,
                score_image_url = null,
                notes = null,
                msg = e.message
            )
        }
    }
}
