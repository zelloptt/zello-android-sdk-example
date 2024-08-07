package com.zello.sdk.example.app.ui.shared

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zello.sdk.Zello
import com.zello.sdk.ZelloHistoryAlertMessage
import com.zello.sdk.ZelloHistoryImageMessage
import com.zello.sdk.ZelloHistoryLocationMessage
import com.zello.sdk.ZelloHistoryMessage
import com.zello.sdk.ZelloHistoryTextMessage
import com.zello.sdk.ZelloHistoryVoiceMessage
import com.zello.sdk.example.app.R
import com.zello.sdk.example.app.utils.TimeUtils
import kotlinx.coroutines.launch

@Composable
fun HistoryDialog(
	messages: List<ZelloHistoryMessage>,
	zello: Zello,
	onDismiss: () -> Unit,
	onMessageClick: (ZelloHistoryMessage) -> Unit,
) {
	Dialog(
		onDismissRequest = { onDismiss() },
		properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
	) {
		Surface(
			color = Color.White,
		) {
			Box(modifier = Modifier.padding(16.dp)) {
				if (messages.isEmpty()) {
					Text(text = "No messages")
				}
				LazyColumn {
					items(messages) { message ->
						Column {
							HistoryMessage(message = message, zello = zello, onMessageClick = onMessageClick)
							Spacer(modifier = Modifier.height(16.dp))
						}
					}
				}
			}
		}
	}
}

@Composable
private fun HistoryMessage(message: ZelloHistoryMessage, zello: Zello, onMessageClick: (ZelloHistoryMessage) -> Unit) {
	val title = if (message.channelUser != null) "${message.channelUser?.name} : ${message.contact.name}" else message.contact.name
	Row(modifier = Modifier
		.fillMaxSize()
		.clickable { onMessageClick(message) }, verticalAlignment = Alignment.CenterVertically
	) {
		Image(
			painter = painterResource(id = if (message.incoming) R.drawable.ic_incoming_24dp else R.drawable.ic_outgoing_24dp),
			contentDescription = "Incoming or outgoing message"
		)
		Spacer(modifier = Modifier.width(8.dp))
		Column {
			Text(text = title)
			Text(text = message.javaClass.simpleName)
			Text(text = TimeUtils.timestampToString(message.timestamp))
			when (message) {
				is ZelloHistoryVoiceMessage -> {
					Text(text = "Duration: ${message.durationMs} ms")
				}

				is ZelloHistoryImageMessage -> {
					AsyncImage(message = message, zello = zello)
				}

				is ZelloHistoryLocationMessage -> {
					Text(text = "Location: ${message.latitude}, ${message.longitude}, ${message.accuracy} ${message.address}")
				}

				is ZelloHistoryTextMessage -> {
					Text(text = message.text)
				}

				is ZelloHistoryAlertMessage -> {
					Text(text = message.text)
				}
			}
		}
	}
}

@Composable
private fun AsyncImage(message: ZelloHistoryImageMessage, zello: Zello) {
	var bitmap by remember { mutableStateOf<Bitmap?>(null) }
	val scope = rememberCoroutineScope()

	LaunchedEffect(message) {
		scope.launch {
			zello.loadBitmapForHistoryImageMessage(message) {
				bitmap = it
			}
		}
	}

	bitmap?.let {
		Image(
			bitmap = it.asImageBitmap(),
			contentDescription = "Image",
			modifier = Modifier.size(200.dp) // Set appropriate size
		)
	}
}
