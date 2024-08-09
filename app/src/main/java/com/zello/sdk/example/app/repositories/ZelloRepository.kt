package com.zello.sdk.example.app.repositories

import android.content.Context
import android.widget.Toast
import com.zello.sdk.Zello
import com.zello.sdk.ZelloAccountStatus
import com.zello.sdk.ZelloAlertMessage
import com.zello.sdk.ZelloChannel
import com.zello.sdk.ZelloConnectionError
import com.zello.sdk.ZelloContact
import com.zello.sdk.ZelloHistoryMessage
import com.zello.sdk.ZelloHistoryVoiceMessage
import com.zello.sdk.ZelloImageMessage
import com.zello.sdk.ZelloIncomingEmergency
import com.zello.sdk.ZelloIncomingVoiceMessage
import com.zello.sdk.ZelloLocationMessage
import com.zello.sdk.ZelloOutgoingEmergency
import com.zello.sdk.ZelloOutgoingVoiceMessage
import com.zello.sdk.ZelloRecentEntry
import com.zello.sdk.ZelloState
import com.zello.sdk.ZelloTextMessage
import com.zello.sdk.ZelloUser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ZelloRepository @Inject constructor(
	@ApplicationContext val context: Context,
	val zello: Zello
) : Zello.Listener {

	private val _state = MutableStateFlow(zello.state)
	val state = _state.asStateFlow()

	private val _isConnected = MutableStateFlow(false)
	val isConnected = _isConnected.asStateFlow()
	private val _isConnecting = MutableStateFlow(false)
	val isConnecting = _isConnecting.asStateFlow()

	private val _selectedContact = MutableStateFlow<ZelloContact?>(null)
	val selectedContact = _selectedContact.asStateFlow()

	private val _onUsersUpdated = MutableStateFlow<List<ZelloUser>>(emptyList())
	val onUsersUpdated = _onUsersUpdated.asStateFlow()
	private val _onChannelsUpdated = MutableStateFlow<List<ZelloChannel>>(emptyList())
	val onChannelsUpdated = _onChannelsUpdated.asStateFlow()

	private val _outgoingVoiceMessage = MutableStateFlow<ZelloOutgoingVoiceMessage?>(null)
	val outgoingVoiceMessage = _outgoingVoiceMessage.asStateFlow()
	private val _incomingVoiceMessage = MutableStateFlow<ZelloIncomingVoiceMessage?>(null)
	val incomingVoiceMessage = _incomingVoiceMessage.asStateFlow()

	private val _incomingImage = MutableStateFlow<ZelloImageMessage?>(null)
	val incomingImage = _incomingImage.asStateFlow()

	private val _accountStatus = MutableStateFlow<ZelloAccountStatus?>(null)
	val accountStatus = _accountStatus.asStateFlow()

	private val _incomingLocation = MutableStateFlow<ZelloLocationMessage?>(null)
	val incomingLocation = _incomingLocation.asStateFlow()

	private val _incomingText = MutableStateFlow<ZelloTextMessage?>(null)
	val incomingText = _incomingText.asStateFlow()

	private val _incomingAlert = MutableStateFlow<ZelloAlertMessage?>(null)
	val incomingAlert = _incomingAlert.asStateFlow()

	private val _emergencyChannel = MutableStateFlow<ZelloChannel?>(null)
	val emergencyChannel = _emergencyChannel.asStateFlow()

	private val _incomingEmergencies = MutableStateFlow<List<ZelloIncomingEmergency>>(listOf())
	val incomingEmergencies = _incomingEmergencies.asStateFlow()

	private val _outgoingEmergency = MutableStateFlow<ZelloOutgoingEmergency?>(null)
	val outgoingEmergency = _outgoingEmergency.asStateFlow()

	private val _onRecentsUpdated = MutableStateFlow<List<ZelloRecentEntry>>(emptyList())
	val onRecentsUpdated = _onRecentsUpdated.asStateFlow()

	private val _onHistoryUpdated = MutableStateFlow<Pair<ZelloContact, List<ZelloHistoryMessage>>?>(null)
	val onHistoryUpdated = _onHistoryUpdated.asStateFlow()

	private val _historyVoiceMessage = MutableStateFlow<ZelloHistoryVoiceMessage?>(null)
	val historyVoiceMessage = _historyVoiceMessage.asStateFlow()

	init {
		zello.listener = this
	}

	override fun onStateChanged(sdk: Zello, state: ZelloState) {
		_state.value = state
	}

	override fun onConnectStarted(zello: Zello) {
		_isConnecting.value = true
	}

	override fun onConnectFailed(zello: Zello, error: ZelloConnectionError) {
		Toast.makeText(context, "Failed to connect ${error.name}", Toast.LENGTH_SHORT).show()
		_isConnecting.value = false
	}

	override fun onConnectSucceeded(zello: Zello) {
		_isConnected.value = true
		_isConnecting.value = false
	}

	override fun onDisconnected(zello: Zello) {
		_isConnected.value = false
		_isConnecting.value = false
		_onUsersUpdated.value = zello.users
		_onChannelsUpdated.value = zello.channels
	}

	override fun onWillReconnect(zello: Zello) {
	}

	override fun onAccountStatusChanged(zello: Zello, status: ZelloAccountStatus?) {
		_accountStatus.value = status
	}

	override fun onIncomingAlertMessage(zello: Zello, message: ZelloAlertMessage) {
		_incomingAlert.value = message
	}

	override fun onOutgoingAlertMessageSent(zello: Zello, message: ZelloAlertMessage) {
	}

	override fun onOutgoingAlertMessageSendFailed(zello: Zello, message: ZelloAlertMessage) {
		Toast.makeText(context, "Alert message failed to send", Toast.LENGTH_SHORT).show()
	}

	override fun onIncomingImageMessage(message: ZelloImageMessage) {
		_incomingImage.value = message
	}

	override fun onOutgoingImageMessageSent(zello: Zello, message: ZelloImageMessage) {
	}

	override fun onOutgoingImageMessageSendFailed(zello: Zello, message: ZelloImageMessage) {
		Toast.makeText(context, "Image message failed to send", Toast.LENGTH_SHORT).show()
	}

	override fun onIncomingLocationMessage(zello: Zello, message: ZelloLocationMessage) {
		_incomingLocation.value = message
	}

	override fun onIncomingTextMessage(message: ZelloTextMessage) {
		_incomingText.value = message
	}

	override fun onOutgoingTextMessageSent(zello: Zello, message: ZelloTextMessage) {
	}

	override fun onOutgoingTextMessageSendFailed(zello: Zello, message: ZelloTextMessage) {
		Toast.makeText(context, "Text message failed to send", Toast.LENGTH_SHORT).show()
	}

	override fun onOutgoingLocationMessageSent(zello: Zello, message: ZelloLocationMessage) {
	}

	override fun onOutgoingLocationMessageSendFailed(zello: Zello, message: ZelloLocationMessage, isPermissionIssue: Boolean) {
		Toast.makeText(context, "Location message failed to send", Toast.LENGTH_SHORT).show()
	}

	override fun onContactListUpdated(zello: Zello) {
		_onUsersUpdated.value = zello.users
		_onChannelsUpdated.value = zello.channels
		_emergencyChannel.value = zello.emergencyChannel
	}

	override fun onIncomingVoiceMessageStarted(zello: Zello, message: ZelloIncomingVoiceMessage) {
		_incomingVoiceMessage.value = zello.incomingVoiceMessage
	}

	override fun onIncomingVoiceMessageStopped(zello: Zello, message: ZelloIncomingVoiceMessage) {
		_incomingVoiceMessage.value = zello.incomingVoiceMessage
	}

	override fun onOutgoingVoiceMessageConnecting(zello: Zello, message: ZelloOutgoingVoiceMessage) {
		_outgoingVoiceMessage.value = zello.outgoingVoiceMessage
	}

	override fun onOutgoingVoiceMessageStarted(zello: Zello, message: ZelloOutgoingVoiceMessage) {
		_outgoingVoiceMessage.value = zello.outgoingVoiceMessage
	}

	override fun onOutgoingVoiceMessageStopped(
		zello: Zello,
		message: ZelloOutgoingVoiceMessage,
		error: ZelloOutgoingVoiceMessage.Error?
	) {
		error?.let {
			Toast.makeText(context, "Failed to send message ${error.name}", Toast.LENGTH_SHORT).show()
		}
		_outgoingVoiceMessage.value = zello.outgoingVoiceMessage
	}

	override fun onSelectedContactChanged(zello: Zello, contact: ZelloContact?) {
		_selectedContact.value = contact
	}

	override fun onIncomingEmergencyStarted(zello: Zello, incomingEmergency: ZelloIncomingEmergency) {
		_incomingEmergencies.value = zello.incomingEmergencies.toMutableList()
	}

	override fun onIncomingEmergencyStopped(zello: Zello, incomingEmergency: ZelloIncomingEmergency) {
		_incomingEmergencies.value = zello.incomingEmergencies.toMutableList()
	}

	override fun onOutgoingEmergencyStarted(zello: Zello, outgoingEmergency: ZelloOutgoingEmergency) {
		_outgoingEmergency.value = zello.outgoingEmergency
	}

	override fun onOutgoingEmergencyStopped(zello: Zello, outgoingEmergency: ZelloOutgoingEmergency) {
		_outgoingEmergency.value = zello.outgoingEmergency
	}

	override fun onRecentsUpdated(zello: Zello, recents: List<ZelloRecentEntry>) {
		_onRecentsUpdated.value = recents
	}

	override fun onHistoryUpdated(zello: Zello) {
		onHistoryUpdated.value?.first?.let {
			_onHistoryUpdated.value = Pair(first = it, second = zello.getHistory(it))
		}
	}

	override fun onHistoryPlaybackStarted(zello: Zello, message: ZelloHistoryVoiceMessage) {
		_historyVoiceMessage.value = message
	}

	override fun onHistoryPlaybackStopped(zello: Zello, message: ZelloHistoryVoiceMessage) {
		_historyVoiceMessage.value = null
	}

	fun clearIncomingImage() {
		_incomingImage.value = null
	}

	fun clearIncomingLocation() {
		_incomingLocation.value = null
	}

	fun clearIncomingText() {
		_incomingText.value = null
	}

	fun clearIncomingAlert() {
		_incomingAlert.value = null
	}

	fun getHistory(contact: ZelloContact) {
		_onHistoryUpdated.value = Pair(first = contact, second = zello.getHistory(contact))
	}

	fun clearHistory() {
		_onHistoryUpdated.value = null
	}
}
