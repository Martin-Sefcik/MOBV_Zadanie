package eu.mcomputing.mobv.zadanie.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OtherProfileViewModel: ViewModel() {
    val userName = MutableLiveData<String>()
}