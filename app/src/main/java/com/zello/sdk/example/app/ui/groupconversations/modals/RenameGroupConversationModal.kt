package com.zello.sdk.example.app.ui.groupconversations.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.zello.sdk.ZelloGroupConversation

@Composable
fun RenameGroupConversationModal(
	groupConversation: ZelloGroupConversation,
	onDismiss: () -> Unit,
	onRename: (String) -> Unit
) {
	var newName by remember { mutableStateOf(groupConversation.displayName) }

	Dialog(onDismissRequest = {
		onDismiss()
	}) {
		Column(
			modifier = Modifier
				.fillMaxWidth(0.75f)
				.background(color = Color.White)
				.padding(16.dp)
		) {
			Text(text = "Rename Conversation")
			Spacer(modifier = Modifier.height(8.dp))
			BasicTextField(
				value = newName,
				onValueChange = { newName = it },
				modifier = Modifier
					.fillMaxWidth()
					.background(color = Color.LightGray)
					.padding(8.dp)
			)
			Spacer(modifier = Modifier.height(16.dp))
			Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
				TextButton(
					modifier = Modifier.align(Alignment.CenterVertically),
					onClick = {
						onDismiss()
					}
				) {
					Text(text = "Cancel")
				}
				Button(
					modifier = Modifier.align(Alignment.CenterVertically),
					onClick = {
						onRename(newName)
						onDismiss()
					}
				) {
					Text(text = "Rename")
				}
			}
		}
	}
}
