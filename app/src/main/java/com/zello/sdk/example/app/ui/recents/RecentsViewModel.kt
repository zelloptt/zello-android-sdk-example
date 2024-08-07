package com.zello.sdk.example.app.ui.recents

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.zello.sdk.ZelloRecentEntry
import com.zello.sdk.example.app.repositories.ZelloRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecentsViewModel @Inject constructor(private val zelloRepository: ZelloRepository) : ViewModel() {
	private val _recents = zelloRepository.onRecentsUpdated.asLiveData()
	val recents: LiveData<List<ZelloRecentEntry>> = _recents
}
