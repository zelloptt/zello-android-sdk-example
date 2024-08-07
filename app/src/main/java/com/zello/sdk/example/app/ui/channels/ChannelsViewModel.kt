package com.zello.sdk.example.app.ui.channels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.zello.sdk.ZelloAlertMessage
import com.zello.sdk.ZelloChannel
import com.zello.sdk.ZelloContact
import com.zello.sdk.ZelloHistoryMessage
import com.zello.sdk.ZelloHistoryVoiceMessage
import com.zello.sdk.example.app.repositories.ZelloRepository
import com.zello.sdk.example.app.ui.shared.types.IncomingAlertViewState
import com.zello.sdk.example.app.ui.shared.types.IncomingEmergenciesViewState
import com.zello.sdk.example.app.ui.shared.types.IncomingImageViewState
import com.zello.sdk.example.app.ui.shared.types.IncomingLocationViewState
import com.zello.sdk.example.app.ui.shared.types.IncomingTextViewState
import com.zello.sdk.example.app.ui.shared.types.IncomingVoiceMessageViewState
import com.zello.sdk.example.app.ui.shared.types.OutgoingEmergencyViewState
import com.zello.sdk.example.app.ui.shared.types.OutgoingVoiceMessageViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChannelsViewModel @Inject constructor(val zelloRepository: ZelloRepository) : ViewModel() {
	private val _channels = zelloRepository.onChannelsUpdated.asLiveData()
	val channels: LiveData<List<ZelloChannel>> = _channels

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

	private val _emergencyChannel = zelloRepository.emergencyChannel.asLiveData()
	val emergencyChannel: LiveData<ZelloChannel?> = _emergencyChannel

	private val _incomingEmergenciesViewState: MediatorLiveData<IncomingEmergenciesViewState> = MediatorLiveData<IncomingEmergenciesViewState>().apply {
		addSource(zelloRepository.incomingEmergencies.asLiveData()) { incomingEmergencies ->
			println("Incoming emergencies: $incomingEmergencies")
			value = IncomingEmergenciesViewState(emergencies = incomingEmergencies)
		}
	}
	val incomingEmergenciesViewState: LiveData<IncomingEmergenciesViewState> = _incomingEmergenciesViewState

	private val _outgoingEmergencyViewState: MediatorLiveData<OutgoingEmergencyViewState> = MediatorLiveData<OutgoingEmergencyViewState>().apply {
		addSource(zelloRepository.outgoingEmergency.asLiveData()) { outgoingEmergency ->
			value = if (outgoingEmergency != null) OutgoingEmergencyViewState(emergency = outgoingEmergency)
			else null
		}
	}
	val outgoingEmergencyViewState: LiveData<OutgoingEmergencyViewState> = _outgoingEmergencyViewState

	private val _history = zelloRepository.onHistoryUpdated.asLiveData()
	val history: LiveData<Pair<ZelloContact, List<ZelloHistoryMessage>>?> = _history

	private val _historyVoiceMessage = zelloRepository.historyVoiceMessage.asLiveData()
	val historyVoiceMessage: LiveData<ZelloHistoryVoiceMessage?> = _historyVoiceMessage

	fun setSelectedContact(channel: ZelloChannel) {
		zelloRepository.zello.setSelectedContact(channel)
	}

	fun startVoiceMessage(channel: ZelloChannel) {
		stopVoiceMessage()
		zelloRepository.zello.startVoiceMessage(channel)
	}

	fun stopVoiceMessage() {
		zelloRepository.zello.stopVoiceMessage()
	}

	fun connectChannel(channel: ZelloChannel) {
		zelloRepository.zello.connectChannel(channel)
	}

	fun disconnectChannel(channel: ZelloChannel) {
		zelloRepository.zello.disconnectChannel(channel)
	}

	fun sendImage(image: ByteArray, channel: ZelloChannel) {
		zelloRepository.zello.sendImage(
			image,
			channel
		)
	}

	fun startEmergency() {
		zelloRepository.zello.startEmergency()
	}

	fun stopEmergency() {
		zelloRepository.zello.stopEmergency()
	}

	fun imageDismissed() {
		zelloRepository.clearIncomingImage()
	}

	fun sendLocation(channel: ZelloChannel) {
		zelloRepository.zello.sendLocation(channel)
	}

	fun locationDismissed() {
		zelloRepository.clearIncomingLocation()
	}

	fun sendText(channel: ZelloChannel, message: String) {
		zelloRepository.zello.sendText(
			message,
			channel
		)
	}

	fun textDismissed() {
		zelloRepository.clearIncomingText()
	}

	fun alertDismissed() {
		zelloRepository.clearIncomingAlert()
	}

	fun onSendAlert(channel: ZelloChannel, text: String, level: ZelloAlertMessage.ChannelLevel?) {
		zelloRepository.zello.sendAlert(channel, text, level)
	}

	fun toggleMute(channel: ZelloChannel) {
		if (channel.isMuted) {
			zelloRepository.zello.unmuteContact(channel)
		} else {
			zelloRepository.zello.muteContact(channel)
		}
	}

	fun getHistory(channel: ZelloChannel) {
		zelloRepository.getHistory(channel)
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
