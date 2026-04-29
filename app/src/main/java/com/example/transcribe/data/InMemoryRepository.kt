package com.example.transcribe.data

import javax.inject.Inject

interface TranscriptionRepository <T>{
    fun findAll(): List<T>
    fun findById(index: Int): T
    fun insert(newTranscription: T)
    fun delete(transcriptionToDelete: T)
    fun edit(selectedTranscriptionToEdit: T)
}

class InMemoryRepository @Inject constructor () : TranscriptionRepository<Transcription> {
    private val transcriptionsRepository: MutableList<Transcription> = ArrayList()

    override fun findAll(): MutableList<Transcription>{
        return transcriptionsRepository
    }

    override fun findById(index: Int): Transcription{
        //Needs a check for valid index
        return transcriptionsRepository[index]
    }

    override fun insert(newTranscription: Transcription){
        transcriptionsRepository.add(newTranscription)
    }

    override fun delete(transcriptionToDelete: Transcription){
        transcriptionsRepository.remove(transcriptionToDelete)
    }

    override fun edit(selectedTranscriptionToEdit: Transcription){
        //Find the transcription that we want to edit and then amend its details
        for (transcription in transcriptionsRepository.iterator()){
            if (transcription.id==selectedTranscriptionToEdit.id){
                transcription.title = selectedTranscriptionToEdit.title
                transcription.author = selectedTranscriptionToEdit.author
            }
        }
    }
}
