package com.zello.sdk.example.app.ui.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zello.sdk.ZelloChannel
import com.zello.sdk.ZelloHistoryVoiceMessage
import com.zello.sdk.ZelloOutgoingVoiceMessage
import com.zello.sdk.example.app.R
import com.zello.sdk.example.app.databinding.FragmentChannelsBinding
import com.zello.sdk.example.app.ui.shared.CallAlertDialog
import com.zello.sdk.example.app.ui.shared.HistoryDialog
import com.zello.sdk.example.app.ui.shared.ImageDialog
import com.zello.sdk.example.app.ui.shared.ListItemTalkButton
import com.zello.sdk.example.app.ui.shared.LocationDialog
import com.zello.sdk.example.app.ui.shared.SendAlertDialog
import com.zello.sdk.example.app.ui.shared.SendTextDialog
import com.zello.sdk.example.app.ui.shared.TextDialog
import com.zello.sdk.example.app.ui.shared.ThreeDotsMenu
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChannelsFragment : Fragment() {

	private val viewModel: ChannelsViewModel by viewModels()

	private var _binding: FragmentChannelsBinding? = null

	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {

		_binding = FragmentChannelsBinding.inflate(inflater, container, false)
		val root: View = binding.root

		val composeView: ComposeView = binding.composeView

		composeView.setContent {
			Channels()
		}

		return root
	}

	@Composable
	private fun Channels() {
		val channels = viewModel.channels.observeAsState().value ?: emptyList()
		val image = viewModel.incomingImageViewState.observeAsState().value
		ImageDialog(
			state = image,
			onDismiss = { viewModel.imageDismissed() }
		)
		val location = viewModel.incomingLocationViewState.observeAsState().value
		LocationDialog(
			state = location,
			onDismiss = { viewModel.locationDismissed() }
		)
		val text = viewModel.incomingTextViewState.observeAsState().value
		TextDialog(
			state = text,
			onDismiss = { viewModel.textDismissed() }
		)
		val alert = viewModel.incomingAlertViewState.observeAsState().value
		CallAlertDialog(
			state = alert,
			onDismiss = { viewModel.alertDismissed() }
		)
		var sendingAlertChannel by remember { mutableStateOf<ZelloChannel?>(null) }
		sendingAlertChannel?.let {
			SendAlertDialog(
				canSelectLevel = true,
				onDismiss = { sendingAlertChannel = null },
				onSend = { text, level ->
					viewModel.onSendAlert(it, text, level)
				}
			)
		}
		var sendingTextChannel by remember { mutableStateOf<ZelloChannel?>(null) }
		sendingTextChannel?.let {
			SendTextDialog(
				onDismiss = { sendingTextChannel = null },
				onSend = { text ->
					viewModel.sendText(it, text)
				}
			)
		}
		val showHistory = viewModel.history.observeAsState().value
		val historyVoiceMessage = viewModel.historyVoiceMessage.observeAsState().value
		showHistory?.let {
			HistoryDialog(messages = it.second, zello = viewModel.zelloRepository.zello, onDismiss = { viewModel.clearHistory() }) { message ->
				if (message is ZelloHistoryVoiceMessage) {
					if (historyVoiceMessage == null) {
						viewModel.playHistory(message)
					} else {
						viewModel.stopHistoryPlayback()
					}
				}
			}
		}
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
		) {
			items(channels.size) { index ->
				if (index != 0) {
					Spacer(modifier = Modifier.height(8.dp))
				}
				Channel(
					channel = channels[index],
					showSendAlert = {
						sendingAlertChannel = channels[index]
					},
					showSendText = {
						sendingTextChannel = channels[index]
					}
				)
			}
		}
	}

	@Composable
	@Preview
	private fun Channel(
		@PreviewParameter(ChannelPreviewProvider::class) channel: ZelloChannel,
		showSendAlert: () -> Unit = {},
		showSendText: () -> Unit = {}
	) {
		val selectedContact = viewModel.selectedContact.observeAsState().value
		Row(modifier = Modifier
			.fillMaxWidth()
			.clickable { viewModel.setSelectedContact(channel) }
			.background(if (selectedContact?.isSameAs(channel) == true) Color.LightGray else Color.Unspecified),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically) {
			val incomingEmergenciesViewState = viewModel.incomingEmergenciesViewState.observeAsState().value
			val outgoingEmergencyViewState = viewModel.outgoingEmergencyViewState.observeAsState().value
			val isInOutgoingEmergency = outgoingEmergencyViewState?.emergency?.channel?.isSameAs(channel) == true
			val emergencyChannel = viewModel.emergencyChannel.observeAsState().value
			val activeIncomingEmergency = incomingEmergenciesViewState?.emergencies?.find { it.channel.isSameAs(channel) }
			Column(
				modifier = Modifier.weight(1f)
			) {
				Text(text = channel.name)
				Text(text = stringResource(id = if (channel.status == ZelloChannel.ConnectionStatus.CONNECTED) R.string.connected else R.string.disconnected))
				if (activeIncomingEmergency != null) {
					Text(text = "ACTIVE INCOMING EMERGENCY : ${activeIncomingEmergency.channelUser.name}")
				}
				if (isInOutgoingEmergency) {
					Text(text = "ACTIVE OUTGOING EMERGENCY")
				}
				Text(text = "${channel.usersOnline} users connected")
			}
			val isConnected = channel.status == ZelloChannel.ConnectionStatus.CONNECTED
			if (!channel.options.hidePowerButton && !(isConnected && channel.options.noDisconnect)) {
				ChannelConnectionSwitch(channel = channel)
			}
			ThreeDotsMenu(
				contact = channel,
				showEmergencyOption = emergencyChannel?.isSameAs(channel) == true,
				isInOutgoingEmergency = isInOutgoingEmergency,
				showAlertOption = channel.options.allowAlerts,
				showTextOption = channel.options.allowTextMessages,
				showLocationOption = channel.options.allowLocations,
				sendImage = {
					viewModel.sendImage(it, channel)
				},
				sendText = showSendText,
				sendLocation = {
					viewModel.sendLocation(channel)
				},
				sendAlert = showSendAlert,
				toggleMute = {
					viewModel.toggleMute(channel)
				},
				startEmergency = {
					viewModel.startEmergency()
				},
				stopEmergency = {
					viewModel.stopEmergency()
				},
				showHistory = {
					viewModel.getHistory(channel)
				}
			)
			TalkButton(channel = channel)
		}
	}

	@Composable
	private fun TalkButton(@PreviewParameter(ChannelPreviewProvider::class) channel: ZelloChannel) {
		val outgoingVoiceMessageViewState = viewModel.outgoingVoiceMessageViewState.observeAsState().value
		val incomingVoiceMessageViewState = viewModel.incomingVoiceMessageViewState.observeAsState().value
		val isSameContact = outgoingVoiceMessageViewState?.contact?.isSameAs(channel) == true
		val isConnecting = isSameContact && outgoingVoiceMessageViewState?.state == ZelloOutgoingVoiceMessage.State.CONNECTING
		val isTalking = isSameContact && outgoingVoiceMessageViewState?.state == ZelloOutgoingVoiceMessage.State.SENDING
		val isReceiving = incomingVoiceMessageViewState?.contact?.isSameAs(channel) == true
		ListItemTalkButton(
			isEnabled = channel.status == ZelloChannel.ConnectionStatus.CONNECTED,
			isConnecting = isConnecting,
			isTalking = isTalking,
			isReceiving = isReceiving,
			onDown = { viewModel.startVoiceMessage(channel) },
			onUp = { viewModel.stopVoiceMessage() }
		)
	}

	@Composable
	private fun ChannelConnectionSwitch(channel: ZelloChannel) {
		val isConnected = channel.status == ZelloChannel.ConnectionStatus.CONNECTED
		Switch(
			checked = isConnected,
			enabled = !(channel.options.noDisconnect && isConnected) && channel.status != ZelloChannel.ConnectionStatus.CONNECTING,
			onCheckedChange = { if (it) viewModel.connectChannel(channel) else viewModel.disconnectChannel(channel) }
		)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
