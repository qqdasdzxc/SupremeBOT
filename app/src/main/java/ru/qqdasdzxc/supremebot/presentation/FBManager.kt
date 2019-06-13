package ru.qqdasdzxc.supremebot.presentation

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.Settings.Secure
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.utils.Constants.FB_ACTIVATED_VALUE
import ru.qqdasdzxc.supremebot.utils.Constants.FB_ACTIVATIONS_TABLE
import ru.qqdasdzxc.supremebot.utils.Constants.FB_DEVICE_ID_VALUE

class FBManager(private val contentResolver: ContentResolver) {

    private val loadingLiveData = MutableLiveData<Boolean>()
    private val systemMessagesLiveData = MutableLiveData<Int>()
    private val errorMessagesLiveData = MutableLiveData<String>()
    private val activationLiveData = MutableLiveData<String>()
    private val fbReference = FirebaseDatabase.getInstance().reference


    fun validateKeyAndInitActivation(key: String?) {
        if (key.isNullOrEmpty()) {
            systemMessagesLiveData.postValue(R.string.activation_key_empty_msg)
        } else {
            checkIsWorkingEnabled(key)
        }
    }

    private fun checkIsWorkingEnabled(key: String) {
        loadingLiveData.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            fbReference.child("enabled").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(dbError: DatabaseError) {
                    errorMessagesLiveData.postValue(dbError.message)
                    loadingLiveData.postValue(false)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.value.toString() == "1") {
                            initActivation(key)
                        } else {
                            systemMessagesLiveData.postValue(R.string.temporary_unavailable)
                            loadingLiveData.postValue(false)
                        }
                    } else {
                        systemMessagesLiveData.postValue(R.string.activation_key_is_invalid)
                        loadingLiveData.postValue(false)
                    }
                }
            })
        }
    }

    private fun initActivation(key: String) {
        CoroutineScope(Dispatchers.IO).launch {
            fbReference.child("activations")
                .orderByKey()
                .equalTo(key)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(dbError: DatabaseError) {
                        errorMessagesLiveData.postValue(dbError.message)
                        loadingLiveData.postValue(false)
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                updateData(dataSnapshot, key)
                            }
                        } else {
                            systemMessagesLiveData.postValue(R.string.activation_key_is_invalid)
                            loadingLiveData.postValue(false)
                        }
                    }
                })
        }
    }

    @SuppressLint("HardwareIds")
    private fun updateData(dataSnapshot: DataSnapshot, key: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val androidID = Secure.getString(contentResolver, Secure.ANDROID_ID)
            if (androidID == null) {
                systemMessagesLiveData.postValue(R.string.device_trouble)
                loadingLiveData.postValue(false)

                return@launch
            }

            when {
                dataSnapshot.child(key).child(FB_DEVICE_ID_VALUE).value.toString() == androidID -> {
                    //loadingLiveData.postValue(false)
                    activationLiveData.postValue(key)
                }
                dataSnapshot.child(key).child(FB_ACTIVATED_VALUE).value.toString() == "0" -> {
                    fbReference.child(FB_ACTIVATIONS_TABLE).child(key).child(FB_ACTIVATED_VALUE).setValue(1)
                    fbReference.child(FB_ACTIVATIONS_TABLE).child(key).child(FB_DEVICE_ID_VALUE).setValue(androidID)
                    //loadingLiveData.postValue(false)
                    activationLiveData.postValue(key)
                }
                else -> {
                    systemMessagesLiveData.postValue(R.string.activation_key_is_used)
                    loadingLiveData.postValue(false)
                }
            }
        }
    }

    fun getLoadingLiveData(): LiveData<Boolean> = loadingLiveData

    fun getSystemMessagesLiveData(): LiveData<Int> = systemMessagesLiveData

    fun getErrorMessagesLiveData(): LiveData<String> = errorMessagesLiveData

    fun getActivationLiveData(): LiveData<String> = activationLiveData
}