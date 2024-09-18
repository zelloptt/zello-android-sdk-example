package com.zello.sdk.example.app.ui.groupconversations.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import com.zello.sdk.ZelloUser

@Composable
fun CreateGroupConversationModal(
	users: List<ZelloUser>,
	onDismiss: () -> Unit,
	onCreate: (List<ZelloUser>) -> Unit
) {
	val filteredUsers = users.filter { it.supportedFeatures.groupConversations }
	var selectedUsers by remember { mutableStateOf(listOf<ZelloUser>()) }

	Dialog(onDismissRequest = {
		onDismiss()
	}) {
		Column(
			modifier = Modifier
				.fillMaxWidth(0.75f)
				.background(color = Color.White)
		) {
			if (filteredUsers.isEmpty()) {
				Text(
					text = "No available users",
					modifier = Modifier.padding(16.dp)
				)
			} else {
				LazyColumn(
					modifier = Modifier
						.fillMaxWidth()
						.weight(1f)
				) {
					items(filteredUsers) { user ->
						Row(
							verticalAlignment = Alignment.CenterVertically,
							modifier = Modifier
								.fillMaxWidth()
								.padding(vertical = 8.dp, horizontal = 16.dp)
						) {
							Checkbox(
								checked = selectedUsers.contains(user),
								onCheckedChange = { isChecked ->
									selectedUsers = if (isChecked) {
										selectedUsers + user
									} else {
										selectedUsers - user
									}
								}
							)
							Spacer(modifier = Modifier.width(8.dp))
							BasicText(text = user.name)
						}
					}
				}
			}
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
						onCreate(selectedUsers)
						onDismiss()
					}
				) {
					Text(text = "Create")
				}
			}
		}
	}
}
