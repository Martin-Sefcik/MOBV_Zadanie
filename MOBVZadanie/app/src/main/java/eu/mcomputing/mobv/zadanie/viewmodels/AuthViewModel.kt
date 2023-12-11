package eu.mcomputing.mobv.zadanie.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.data.model.User
import kotlinx.coroutines.launch

class AuthViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _registrationResult = MutableLiveData<String>()
    val registrationResult: LiveData<String> get() = _registrationResult

    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String> get() = _loginResult

    private val _userResult = MutableLiveData<User?>()
    val userResult: LiveData<User?> get() = _userResult

    private val _userResetPasswordResult = MutableLiveData<String>()
    val userResetPasswordResult: LiveData<String> get() = _userResetPasswordResult

    private val _userChangePasswordResult = MutableLiveData<String>()
    val userChangePasswordResult: LiveData<String> get() = _userChangePasswordResult

    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val repeat_password = MutableLiveData<String>()
    val old_password = MutableLiveData<String>()
    val new_password = MutableLiveData<String>()
    val repeat_new_password = MutableLiveData<String>()

    fun registerUser() {
        viewModelScope.launch {
            val result = dataRepository.apiRegisterUser(
                username.value ?: "",
                email.value ?: "",
                password.value ?: "",
                repeat_password.value ?: ""
            )
            _registrationResult.postValue(result.first ?: "")
            _userResult.postValue(result.second)
        }
    }

    fun loginUser() {
        viewModelScope.launch {
            val result = dataRepository.apiLoginUser(
                username.value ?: "",
                password.value ?: ""
            )
            _loginResult.postValue(result.first ?: "")
            _userResult.postValue(result.second)
        }
    }

    fun resetUserPassword() {
        viewModelScope.launch {
//            Log.d("Email",email.value ?: "")
            val result = dataRepository.apiResetUserPassword(email.value ?: "")
            _userResetPasswordResult.postValue(result.first ?: "")
        }
    }

    fun changeUserPassword() {
        viewModelScope.launch {
            val result = dataRepository.apiChangeUserPassword(
                old_password.value ?: "",
                new_password.value ?: "",
                repeat_new_password.value ?: ""
            )
            _userChangePasswordResult.postValue(result.first ?: "")
        }
    }

    fun logoutUser() {
        username.value = ""
        password.value = ""
        email.value = ""
        repeat_password.value = ""

        _userResult.value = null
    }
}