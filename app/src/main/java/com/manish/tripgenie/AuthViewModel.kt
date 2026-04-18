package com.manish.tripgenie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _user = MutableLiveData<FirebaseUser?>(repository.getCurrentUser())
    val user: LiveData<FirebaseUser?> = _user

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _loginSuccess = MutableLiveData<Boolean>(false)
    val loginSuccess: LiveData<Boolean> = _loginSuccess

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.login(email, pass)
            _isLoading.value = false
            
            result.onSuccess {
                _user.value = it
                _loginSuccess.value = true
            }.onFailure {
                _error.value = it.localizedMessage ?: "Login failed"
            }
        }
    }

    fun signup(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.signup(email, pass)
            _isLoading.value = false
            
            result.onSuccess {
                _user.value = it
                _loginSuccess.value = true
            }.onFailure {
                _error.value = it.localizedMessage ?: "Signup failed"
            }
        }
    }

    fun logout() {
        repository.logout()
        _user.value = null
        _loginSuccess.value = false
    }
}
