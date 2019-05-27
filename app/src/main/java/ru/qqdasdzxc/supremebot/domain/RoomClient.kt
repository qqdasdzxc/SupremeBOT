package ru.qqdasdzxc.supremebot.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.qqdasdzxc.supremebot.data.dto.UserProfile

class RoomClient {

    private val userProfileLiveData = MutableLiveData<UserProfile>()

    fun getUserProfile(): LiveData<UserProfile> {
        CoroutineScope(Dispatchers.IO).launch {
            userProfileLiveData.postValue(App.getDatabase().getUserProfileDao().getById(1))
        }

        return userProfileLiveData
    }

    fun saveUserProfile(userProfile: UserProfile) {
        CoroutineScope(Dispatchers.IO).launch {
            App.getDatabase().getUserProfileDao().save(userProfile)
        }
    }
}