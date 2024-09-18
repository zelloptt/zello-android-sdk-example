package com.zello.sdk.example.app.ui.groupconversations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.zello.sdk.ZelloChannel
import com.zello.sdk.ZelloConsoleSettings
import com.zello.sdk.ZelloContact
import com.zello.sdk.ZelloGroupConversation
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
class GroupConversationsViewModel @Inject constructor(val zelloRepository: ZelloRepository) : ViewModel() {
	private val _groupConversations = zelloRepository.onGroupConversationsUpdated.asLiveData()
	val groupConversations: LiveData<List<ZelloGroupConversation>> = _groupConversations

	private val _users = zelloRepository.onUsersUpdated.asLiveData()
	val users: LiveData<List<ZelloUser>> = _users

	private val _isConnected = zelloRepository.isConnected.asLiveData()
	val isConnected: LiveData<Boolean> = _isConnected

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

	private val _settings = zelloRepository.settings.asLiveData()
	val settings: LiveData<ZelloConsoleSettings?> = _settings

	fun setSelectedContact(conversation: ZelloGroupConversation) {
		zelloRepository.zello.setSelectedContact(conversation)
	}

	fun startVoiceMessage(conversation: ZelloGroupConversation) {
		stopVoiceMessage()
		zelloRepository.zello.startVoiceMessage(conversation)
	}

	fun stopVoiceMessage() {
		zelloRepository.zello.stopVoiceMessage()
	}

	fun sendImage(image: ByteArray, conversation: ZelloGroupConversation) {
		zelloRepository.zello.sendImage(
			image,
			conversation
		)
	}

	fun imageDismissed() {
		zelloRepository.clearIncomingImage()
	}

	fun sendLocation(conversation: ZelloGroupConversation) {
		zelloRepository.zello.sendLocation(conversation)
	}

	fun locationDismissed() {
		zelloRepository.clearIncomingLocation()
	}

	fun sendText(conversation: ZelloGroupConversation, message: String) {
		zelloRepository.zello.sendText(
			message,
			conversation
		)
	}

	fun textDismissed() {
		zelloRepository.clearIncomingText()
	}

	fun alertDismissed() {
		zelloRepository.clearIncomingAlert()
	}

	fun onSendAlert(conversation: ZelloGroupConversation, text: String) {
		zelloRepository.zello.sendAlert(conversation, text)
	}

	fun toggleMute(conversation: ZelloGroupConversation) {
		if (conversation.isMuted) {
			zelloRepository.zello.unmuteContact(conversation)
		} else {
			zelloRepository.zello.muteContact(conversation)
		}
	}

	fun getHistory(conversation: ZelloGroupConversation) {
		zelloRepository.getHistory(conversation)
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

	fun createConversation(users: List<ZelloUser>) {
		zelloRepository.zello.createGroupConversation(users)
	}

	fun addUsersToConversation(conversation: ZelloGroupConversation, users: List<ZelloUser>) {
		zelloRepository.zello.addUsersToGroupConversation(conversation, users)
	}

	fun leaveConversation(conversation: ZelloGroupConversation) {
		zelloRepository.zello.leaveGroupConversation(conversation)
	}

	fun renameConversation(conversation: ZelloGroupConversation, name: String) {
		zelloRepository.zello.renameGroupConversation(conversation, name)
	}

	fun connectChannel(channel: ZelloChannel) {
		zelloRepository.zello.connectChannel(channel)
	}

	fun disconnectChannel(channel: ZelloChannel) {
		zelloRepository.zello.disconnectChannel(channel)
	}
}
