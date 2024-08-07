package com.zello.sdk.example.app.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ConnectDialog(
	onDismiss: () -> Unit,
	onConnect: (String, String, String) -> Unit
) {
	Dialog(
		onDismissRequest = { onDismiss() },
		properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
	) {
		Surface(
			color = Color.White
		) {
			var username by remember { mutableStateOf("") }
			var password by remember { mutableStateOf("") }
			var network by remember { mutableStateOf("") }

			Column(
				modifier = Modifier.padding(16.dp),
			) {
				TextField(
					placeholder = { Text("Username") },
					value = username,
					onValueChange = { username = it },
					modifier = Modifier.fillMaxWidth(),
					singleLine = true
				)
				Spacer(modifier = Modifier.height(8.dp))

				TextField(
					placeholder = { Text("Password") },
					value = password,
					onValueChange = { password = it },
					modifier = Modifier.fillMaxWidth(),
					singleLine = true,
					visualTransformation = PasswordVisualTransformation(),
					keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
				)
				Spacer(modifier = Modifier.height(8.dp))

				TextField(
					placeholder = { Text("Network") },
					value = network,
					onValueChange = { network = it },
					modifier = Modifier.fillMaxWidth(),
					singleLine = true
				)
				Spacer(modifier = Modifier.height(16.dp))

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {
					Button(onClick = { onDismiss() }) {
						Text("Cancel")
					}
					Spacer(modifier = Modifier.width(8.dp))
					Button(onClick = {
						onConnect(username, password, network)
						onDismiss()
					}) {
						Text("Connect")
					}
				}
			}
		}
	}
}
