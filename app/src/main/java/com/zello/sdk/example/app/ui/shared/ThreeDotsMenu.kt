package com.zello.sdk.example.app.ui.shared

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.zello.sdk.ZelloContact
import com.zello.sdk.example.app.R
import java.io.ByteArrayOutputStream

@Composable
fun ThreeDotsMenu(
	contact: ZelloContact,
	showEmergencyOption: Boolean = false,
	isInOutgoingEmergency: Boolean = false,
	showAlertOption: Boolean = true,
	showLocationOption: Boolean = true,
	showTextOption: Boolean = true,
	showEndCallOption: Boolean = false,
	sendImage: (ByteArray) -> Unit,
	sendText: () -> Unit,
	sendLocation: () -> Unit,
	sendAlert: () -> Unit,
	toggleMute: () -> Unit,
	startEmergency: (() -> Unit)? = null,
	stopEmergency: (() -> Unit)? = null,
	showHistory: () -> Unit,
	endCall: (() -> Unit)? = null,
) {
	Box {
		var dropDownExpanded by remember { mutableStateOf(false) }
		val context = LocalContext.current // Used to obtain an image from resources to send
		IconButton(onClick = {
			dropDownExpanded = !dropDownExpanded
		}) {
			Icon(
				painter = painterResource(id = R.drawable.more_vert_24),
				contentDescription = "more"
			)
		}
		DropdownMenu(
			expanded = dropDownExpanded,
			onDismissRequest = { dropDownExpanded = false }
		) {
			DropdownMenuItem(
				text = { Text("Send Image") },
				onClick = {
					// Sends a static image from resources purely as an example. An image
					// could come from a file, the camera, etc.
					val bitmap = ContextCompat.getDrawable(
						context,
						R.drawable.ic_launcher_background
					)?.toBitmap()
					val stream = ByteArrayOutputStream()
					bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
					val bitmapData = stream.toByteArray()
					sendImage(bitmapData)
					dropDownExpanded = false
				}
			)
			if (showTextOption) {
				DropdownMenuItem(
					text = { Text("Send Text") },
					onClick = {
						sendText()
						dropDownExpanded = false
					}
				)
			}
			if (showLocationOption) {
				DropdownMenuItem(
					text = { Text("Send Location") },
					onClick = {
						sendLocation()
						dropDownExpanded = false
					}
				)
			}
			if (showAlertOption) {
				DropdownMenuItem(
					text = { Text("Send Alert") },
					onClick = {
						sendAlert()
						dropDownExpanded = false
					}
				)
			}
			DropdownMenuItem(
				text = { Text(if (contact.isMuted) "Unmute" else "Mute") },
				onClick = {
					toggleMute()
					dropDownExpanded = false
				}
			)
			if (showEmergencyOption) {
				DropdownMenuItem(
					text = { Text("${if (isInOutgoingEmergency) "Stop" else "Start"} Emergency") },
					onClick = {
						if (isInOutgoingEmergency) {
							stopEmergency?.invoke()
						} else {
							startEmergency?.invoke()
						}
						dropDownExpanded = false
					}
				)
			}
			if (showEndCallOption) {
				DropdownMenuItem(
					text = { Text("End call") },
					onClick = {
						endCall?.invoke()
						dropDownExpanded = false
					}
				)
			}
			DropdownMenuItem(
				text = { Text("Show History") },
				onClick = {
					showHistory()
					dropDownExpanded = false
				}
			)
		}
	}
}
