package com.example.transcribe.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.transcribe.UserRole
import com.example.transcribe.data.Transcription
import com.example.transcribe.data.TranscriptionRepository
import com.example.transcribe.data.User
import com.example.transcribe.data.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val userRepository: UserRepo,
    private val transcriptionRepository: TranscriptionRepository
) : ViewModel() {

    val users: StateFlow<List<User>> = userRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedUserId = MutableStateFlow<String?>(null)
    val selectedUserId = _selectedUserId.asStateFlow()

    val selectedUser: StateFlow<User?> = combine(users, _selectedUserId) { userList, id ->
        userList.find { it.uid == id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val userTranscriptions: StateFlow<List<Transcription>> = combine(
        transcriptionRepository.getAll(),
        _selectedUserId
    ) { allTranscriptions, userId ->
        if (userId == null) emptyList()
        else allTranscriptions.filter { it.userId == userId }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectUser(userId: String?) {
        _selectedUserId.value = userId
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            userRepository.delete(userId)
            if (_selectedUserId.value == userId) {
                _selectedUserId.value = null
            }
        }
    }

    fun updateUserRole(userId: String, newRole: UserRole) {
        viewModelScope.launch {
            val user = users.value.find { it.uid == userId }
            if (user != null) {
                user.role = newRole
                userRepository.edit(user)
            }
        }
    }
}
