package com.zello.sdk.example.app.ui.users

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import com.zello.sdk.ZelloHistoryVoiceMessage
import com.zello.sdk.ZelloOutgoingVoiceMessage
import com.zello.sdk.ZelloUser
import com.zello.sdk.example.app.databinding.FragmentUsersBinding
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
class UsersFragment : Fragment() {

	private val viewModel: UsersViewModel by viewModels()

	private var _binding: FragmentUsersBinding? = null

	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {

		_binding = FragmentUsersBinding.inflate(inflater, container, false)
		val root: View = binding.root

		val composeView: ComposeView = binding.composeView

		composeView.setContent {
			Users()
		}

		return root
	}

	@Composable
	private fun Users() {
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
		var sendingAlertUser by remember { mutableStateOf<ZelloUser?>(null) }
		sendingAlertUser?.let {
			SendAlertDialog(
				canSelectLevel = false,
				onDismiss = { sendingAlertUser = null },
				onSend = { text, _ ->
					viewModel.onSendAlert(it, text)
				}
			)
		}
		var sendingTextUser by remember { mutableStateOf<ZelloUser?>(null) }
		sendingTextUser?.let {
			SendTextDialog(
				onDismiss = { sendingTextUser = null },
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
			modifier = Modifier.fillMaxSize()
		) {
			items(users.size) { index ->
				if (index != 0) {
					Spacer(modifier = Modifier.height(8.dp))
				}
				User(
					user = users[index],
					showSendAlert = {
						sendingAlertUser = users[index]
					},
					showSendText = {
						sendingTextUser = users[index]
					}
				)
			}
		}
	}

	@Composable
	@Preview
	private fun User(
		@PreviewParameter(UserPreviewProvider::class) user: ZelloUser,
		showSendAlert: () -> Unit = {},
		showSendText: () -> Unit = {}
	) {
		val selectedContact = viewModel.selectedContact.observeAsState().value
		Box {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(8.dp)
					.clickable {
						viewModel.setSelectedContact(user)
					}
					.background(if (selectedContact?.isSameAs(user) == true) Color.LightGray else Color.Unspecified),
				verticalAlignment = Alignment.CenterVertically
			) {
				user.profilePictureThumbnailUrl?.let { url ->
					AsyncImage(
						model = url,
						contentDescription = "",
						contentScale = ContentScale.Crop,
						modifier = Modifier
							.size(48.dp)
							.clip(CircleShape),
						placeholder = ColorPainter(Color.LightGray),
						error = ColorPainter(Color.Red)
					)
				}
				Column(
					modifier = Modifier
						.weight(1f)
						.padding(horizontal = 8.dp)
						.fillMaxHeight(),
					verticalArrangement = Arrangement.Center) {
					Text(text = "${user.displayName} (${user.name})")
					Text(text = user.status.name)
					user.customStatusText?.let { status ->
						Text(text = status)
					}
				}
				ThreeDotsMenu(
					contact = user,
					sendImage = {
						viewModel.sendImage(it, user)
					},
					sendText = showSendText,
					sendLocation = {
						viewModel.sendLocation(user)
					},
					sendAlert = showSendAlert,
					toggleMute = {
						viewModel.toggleMute(user)
					},
					showHistory = {
						viewModel.getHistory(user)
					}
				)
				TalkButton(user = user)
			}
		}
	}

	@Composable
	private fun TalkButton(@PreviewParameter(UserPreviewProvider::class) user: ZelloUser) {
		val outgoingVoiceMessageViewState = viewModel.outgoingVoiceMessageViewState.observeAsState().value
		val incomingVoiceMessageViewState = viewModel.incomingVoiceMessageViewState.observeAsState().value
		val isSameContact = outgoingVoiceMessageViewState?.contact?.isSameAs(user) == true
		val isConnecting = isSameContact && outgoingVoiceMessageViewState?.state == ZelloOutgoingVoiceMessage.State.CONNECTING
		val isTalking = isSameContact && outgoingVoiceMessageViewState?.state == ZelloOutgoingVoiceMessage.State.SENDING
		val isReceiving = incomingVoiceMessageViewState?.contact?.isSameAs(user) == true
		ListItemTalkButton(
			isEnabled = true,
			isConnecting = isConnecting,
			isTalking = isTalking,
			isReceiving = isReceiving,
			onDown = { viewModel.startVoiceMessage(user) },
			onUp = { viewModel.stopVoiceMessage() }
		)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}
