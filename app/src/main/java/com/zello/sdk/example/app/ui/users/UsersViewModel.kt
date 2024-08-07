package com.zello.sdk.example.app.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.zello.sdk.ZelloContact
import com.zello.sdk.ZelloHistoryMessage
import com.zello.sdk.ZelloHistoryVoiceMessage
import com.zello.sdk.ZelloUser
import com.zello.sdk.example.app.repositories.ZelloRepository
import com.zello.sdk.example.app.ui.shared.types.IncomingAlertViewState
import com.zello.sdk.example.app.ui.shared.types.IncomingImageViewState
import com.zello.sdk.example.app.ui.shared.types.IncomingLocationViewState
import com.zello.sdk.example.app.ui.shared.types.IncomingTextViewState
import com.zello.sdk.example.app.ui.shared.types.IncomingVoiceMessageViewState
import com.zello.sdk.example.app.ui.shared.types.OutgoingVoiceMessageViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(val zelloRepository: ZelloRepository) : ViewModel() {
	private val _users = zelloRepository.onUsersUpdated.asLiveData()
	val users: LiveData<List<ZelloUser>> = _users

	private val _outgoingVoiceMessageViewState = MediatorLiveData<OutgoingVoiceMessageViewState>().apply {
		addSource(zelloRepository.outgoingVoiceMessage.asLiveData()) { outgoingMessage ->
			value = if (outgoingMessage != null) OutgoingVoiceMessageViewState(contact = outgoingMessage.contact, state = outgoingMessage.state)
			else null
		}
	}
	val outgoingVoiceMessageViewState: LiveData<OutgoingVoiceMessageViewState> = _outgoingVoiceMessageViewState

	private val _incomingVoiceMessageViewState = MediatorLiveData<IncomingVoiceMessageViewState>().apply {
		addSource(zelloRepository.incomingVoiceMessage.asLiveData()) { incomingMessage ->
			value = if (incomingMessage != null) IncomingVoiceMessageViewState(contact = incomingMessage.contact)
			else null
		}
	}
	val incomingVoiceMessageViewState: LiveData<IncomingVoiceMessageViewState> = _incomingVoiceMessageViewState

	private val _selectedContact = zelloRepository.selectedContact.asLiveData()
	val selectedContact: LiveData<ZelloContact?> = _selectedContact

	private val _incomingImageViewState: MediatorLiveData<IncomingImageViewState> = MediatorLiveData<IncomingImageViewState>().apply {
		addSource(zelloRepository.incomingImage.asLiveData()) { incomingImage ->
			value = if (incomingImage != null) IncomingImageViewState(image = incomingImage.image)
			else null
		}
	}
	val incomingImageViewState: LiveData<IncomingImageViewState> = _incomingImageViewState

	private val _incomingLocationViewState: MediatorLiveData<IncomingLocationViewState> = MediatorLiveData<IncomingLocationViewState>().apply {
		addSource(zelloRepository.incomingLocation.asLiveData()) { incomingLocation ->
			value = if (incomingLocation != null) IncomingLocationViewState(locationText = incomingLocation.address)
			else null
		}
	}
	val incomingLocationViewState: LiveData<IncomingLocationViewState> = _incomingLocationViewState

	private val _incomingTextViewState: MediatorLiveData<IncomingTextViewState> = MediatorLiveData<IncomingTextViewState>().apply {
		addSource(zelloRepository.incomingText.asLiveData()) { incomingText ->
			value = if (incomingText != null) IncomingTextViewState(text = incomingText.text)
			else null
		}
	}
	val incomingTextViewState: LiveData<IncomingTextViewState> = _incomingTextViewState

	private val _incomingAlertViewState: MediatorLiveData<IncomingAlertViewState> = MediatorLiveData<IncomingAlertViewState>().apply {
		addSource(zelloRepository.incomingAlert.asLiveData()) { incomingAlert ->
			value = if (incomingAlert != null) IncomingAlertViewState(text = incomingAlert.text)
			else null
		}
	}
	val incomingAlertViewState: LiveData<IncomingAlertViewState> = _incomingAlertViewState

	private val _history = zelloRepository.onHistoryUpdated.asLiveData()
	val history: LiveData<Pair<ZelloContact, List<ZelloHistoryMessage>>?> = _history

	private val _historyVoiceMessage = zelloRepository.historyVoiceMessage.asLiveData()
	val historyVoiceMessage: LiveData<ZelloHistoryVoiceMessage?> = _historyVoiceMessage

	fun setSelectedContact(user: ZelloUser) {
		zelloRepository.zello.setSelectedContact(user)
	}

	fun startVoiceMessage(user: ZelloUser) {
		stopVoiceMessage()
		zelloRepository.zello.startVoiceMessage(user)
	}

	fun stopVoiceMessage() {
		zelloRepository.zello.stopVoiceMessage()
	}

	fun sendImage(image: ByteArray, user: ZelloUser) {
		zelloRepository.zello.sendImage(
			image,
			user
		)
	}

	fun imageDismissed() {
		zelloRepository.clearIncomingImage()
	}

	fun sendLocation(user: ZelloUser) {
		zelloRepository.zello.sendLocation(user)
	}

	fun locationDismissed() {
		zelloRepository.clearIncomingLocation()
	}

	fun sendText(user: ZelloUser, message: String) {
		zelloRepository.zello.sendText(
			message,
			user
		)
	}

	fun textDismissed() {
		zelloRepository.clearIncomingText()
	}

	fun alertDismissed() {
		zelloRepository.clearIncomingAlert()
	}

	fun onSendAlert(user: ZelloUser, text: String) {
		zelloRepository.zello.sendAlert(user, text, null)
	}

	fun toggleMute(user: ZelloUser) {
		if (user.isMuted) {
			zelloRepository.zello.unmuteContact(user)
		} else {
			zelloRepository.zello.muteContact(user)
		}
	}

	fun getHistory(user: ZelloUser) {
		zelloRepository.getHistory(user)
	}

	fun clearHistory() {
		zelloRepository.clearHistory()
	}

	fun playHistory(message: ZelloHistoryVoiceMessage) {
		zelloRepository.zello.playHistoryMessage(message)
	}

	fun stopHistoryPlayback() {
		zelloRepository.zello.stopHistoryMessagePlayback()
	}
}
