package eu.mcomputing.mobv.zadanie.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.mcomputing.mobv.zadanie.data.DataRepository
import eu.mcomputing.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputing.mobv.zadanie.data.model.User
import kotlinx.coroutines.launch

class OtherProfileViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _otherProfileResult = MutableLiveData<UserEntity?>()
    val otherProfileResult: LiveData<UserEntity?> get() = _otherProfileResult

    var uid = MutableLiveData<String>()

    fun loadUser() {
        viewModelScope.launch {
            val result = dataRepository.getUser(uid.value ?: "")
            _otherProfileResult.postValue(result)
        }
    }
}