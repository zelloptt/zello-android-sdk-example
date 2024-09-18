package com.zello.sdk.example.app.ui.groupconversations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zello.sdk.ZelloChannel
import com.zello.sdk.ZelloGroupConversation
import com.zello.sdk.ZelloHistoryVoiceMessage
import com.zello.sdk.ZelloOutgoingVoiceMessage
import com.zello.sdk.example.app.R
import com.zello.sdk.example.app.databinding.FragmentGroupConversationsBinding
import com.zello.sdk.example.app.ui.groupconversations.modals.AddUsersToGroupConversationModal
import com.zello.sdk.example.app.ui.groupconversations.modals.CreateGroupConversationModal
import com.zello.sdk.example.app.ui.groupconversations.modals.RenameGroupConversationModal
import com.zello.sdk.example.app.ui.shared.CallAlertDialog
import com.zello.sdk.example.app.ui.shared.ChannelConnectionSwitch
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
class GroupConversationsFragment : Fragment() {

	private val viewModel: GroupConversationsViewModel by viewModels()

	private var _binding: FragmentGroupConversationsBinding? = null

	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentGroupConversationsBinding.inflate(inflater, container, false)
		val root: View = binding.root

		val composeView: ComposeView = binding.composeView

		composeView.setContent {
			GroupConversations()
		}

		return root
	}

	@Composable
	private fun GroupConversations() {
		val groupConversations = viewModel.groupConversations.observeAsState().value ?: emptyList()
		val users = viewModel.users.observeAsState().value ?: emptyList()
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
		var sendingAlertConversation by remember { mutableStateOf<ZelloGroupConversation?>(null) }
		sendingAlertConversation?.let {
			SendAlertDialog(
				canSelectLevel = true,
				onDismiss = { sendingAlertConversation = null },
				onSend = { text, _ ->
					viewModel.onSendAlert(it, text)
				}
			)
		}
		var sendingTextConversation by remember { mutableStateOf<ZelloGroupConversation?>(null) }
		sendingTextConversation?.let {
			SendTextDialog(
				onDismiss = { sendingTextConversation = null },
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
		var creatingConversation by remember { mutableStateOf(false) }
		if (creatingConversation) {
			CreateGroupConversationModal(
				users = users,
				onDismiss = { creatingConversation = false },
				onCreate = { selectedUsers ->
					viewModel.createConversation(selectedUsers)
				}
			)
		}
		var addingToConversation by remember { mutableStateOf<ZelloGroupConversation?>(null) }
		addingToConversation?.let { conversation ->
			AddUsersToGroupConversationModal(
				groupConversation = conversation,
				allUsers = users,
				onDismiss = { addingToConversation = null },
				onAdd = { selectedUsers ->
					viewModel.addUsersToConversation(conversation, selectedUsers)
				}
			)
		}
		var renamingConversation by remember { mutableStateOf<ZelloGroupConversation?>(null) }
		renamingConversation?.let { conversation ->
			RenameGroupConversationModal(
				groupConversation = conversation,
				onDismiss = { renamingConversation = null },
				onRename = { newName ->
					viewModel.renameConversation(conversation, newName)
				}
			)
		}

		Box(modifier = Modifier.fillMaxSize()) {
			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
			) {
				items(groupConversations.size) { index ->
					if (index != 0) {
						Spacer(modifier = Modifier.height(8.dp))
					}
					GroupConversation(
						conversation = groupConversations[index],
						showSendAlert = {
							sendingAlertConversation = groupConversations[index]
						},
						showSendText = {
							sendingTextConversation = groupConversations[index]
						},
						showAddUsersToConversation = {
							addingToConversation = groupConversations[index]
						},
						showRenameConversation = {
							renamingConversation = groupConversations[index]
						}
					)
				}
			}

			val isConnected = viewModel.isConnected.observeAsState().value ?: false
			if (isConnected) {
				FloatingActionButton(
					onClick = {
						creatingConversation = true
					},
					modifier = Modifier
						.align(Alignment.BottomEnd)
						.padding(16.dp)
				) {
					Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Conversation")
				}
			}
		}
	}

	@Composable
	private fun GroupConversation(
		conversation: ZelloGroupConversation,
		showSendAlert: () -> Unit = {},
		showSendText: () -> Unit = {},
		showAddUsersToConversation: () -> Unit = {},
		showRenameConversation: () -> Unit = {}
	) {
		val selectedContact = viewModel.selectedContact.observeAsState().value
		val settings = viewModel.settings.observeAsState().value
		Row(modifier = Modifier
			.fillMaxWidth()
			.clickable { viewModel.setSelectedContact(conversation) }
			.background(if (selectedContact?.isSameAs(conversation) == true) Color.LightGray else Color.Unspecified),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically) {
			Column(
				modifier = Modifier.weight(1f)
			) {
				Text(text = conversation.displayName)
				Text(text = stringResource(id = if (conversation.status == ZelloChannel.ConnectionStatus.CONNECTED) R.string.connected else R.string.disconnected))
				Text(text = "${conversation.usersOnline} users connected")
			}
			ChannelConnectionSwitch(channel = conversation, onCheckChanged = { if (it) viewModel.connectChannel(conversation) else viewModel.disconnectChannel(conversation) })
			val isConnected = conversation.status == ZelloChannel.ConnectionStatus.CONNECTED
			ThreeDotsMenu(
				contact = conversation,
				showAlertOption = isConnected && settings?.allowAlertMessages == true,
				showTextOption = isConnected && settings?.allowTextMessages == true,
				showLocationOption = isConnected && settings?.allowLocationMessages == true,
				showImageOption = isConnected && settings?.allowImageMessages == true,
				sendImage = {
					viewModel.sendImage(it, conversation)
				},
				sendText = showSendText,
				sendLocation = {
					viewModel.sendLocation(conversation)
				},
				sendAlert = showSendAlert,
				toggleMute = {
					viewModel.toggleMute(conversation)
				},
				showHistory = {
					viewModel.getHistory(conversation)
				},
				showLeaveConversationOption = true,
				leaveConversation = {
					viewModel.leaveConversation(conversation)
				},
				showAddUsersToConversationOption = true,
				addUsersToConversation = showAddUsersToConversation,
				showRenameConversationOption = true,
				renameConversation = showRenameConversation
			)
			TalkButton(conversation = conversation)
		}
	}

	@Composable
	private fun TalkButton(conversation: ZelloGroupConversation) {
		val outgoingVoiceMessageViewState = viewModel.outgoingVoiceMessageViewState.observeAsState().value
		val incomingVoiceMessageViewState = viewModel.incomingVoiceMessageViewState.observeAsState().value
		val isSameContact = outgoingVoiceMessageViewState?.contact?.isSameAs(conversation) == true
		val isConnecting = isSameContact && outgoingVoiceMessageViewState?.state == ZelloOutgoingVoiceMessage.State.CONNECTING
		val isTalking = isSameContact && outgoingVoiceMessageViewState?.state == ZelloOutgoingVoiceMessage.State.SENDING
		val isReceiving = incomingVoiceMessageViewState?.contact?.isSameAs(conversation) == true
		ListItemTalkButton(
			isEnabled = true,
			isConnecting = isConnecting,
			isTalking = isTalking,
			isReceiving = isReceiving,
			onDown = { viewModel.startVoiceMessage(conversation) },
			onUp = { viewModel.stopVoiceMessage() }
		)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
