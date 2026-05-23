package com.example.transcribe.data

import com.example.transcribe.UserRole
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserRepo : Repository<User>{
    suspend fun createUserProfile(newUserDetails: User): Response
    suspend fun getUserRole(uid: String): UserRole
    fun getUserFlow(uid: String): Flow<User?>
    suspend fun addRecentTranscription(uid: String, transcription: Transcription)
    suspend fun removeRecentTranscription(uid: String, transcriptionId: String)
}

class UserRepository @Inject constructor(
    private val dao: UserDao
) : UserRepo {

    override suspend fun createUserProfile(newUserDetails: User) = dao.create(newUserDetails)

    override suspend fun insert(item: User) = dao.add(item)

    override suspend fun delete(id: String) = dao.delete(id)

    override suspend fun edit(item: User) = dao.update(item)

    override fun getAll(): Flow<List<User>> = dao.getAll()

    override suspend fun getById(id: String): User? = dao.getById(id)

    override suspend fun getUserRole(uid: String): UserRole {
        val user = dao.getById(uid)
        return user?.role ?: UserRole.UNKNOWN
    }

    override fun getUserFlow(uid: String): Flow<User?> = dao.getUserFlow(uid)

    override suspend fun addRecentTranscription(uid: String, transcription: Transcription) = 
        dao.addRecentTranscription(uid, transcription)

    override suspend fun removeRecentTranscription(uid: String, transcriptionId: String) =
        dao.removeRecentTranscription(uid, transcriptionId)
}
