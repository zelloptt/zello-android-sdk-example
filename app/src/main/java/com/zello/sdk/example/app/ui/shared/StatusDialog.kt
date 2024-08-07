package com.zello.sdk.example.app.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zello.sdk.ZelloAccountStatus

@Composable
fun StatusDialog(
	selectedStatus: ZelloAccountStatus,
	onDismiss: () -> Unit,
	onSelectStatus: (ZelloAccountStatus) -> Unit
) {
	Dialog(
		onDismissRequest = { onDismiss() },
		properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
	) {
		Surface(
			color = Color.White
		) {
			Column(
				modifier = Modifier.padding(16.dp)
			) {
				val statuses = ZelloAccountStatus.entries.toTypedArray()
				statuses.forEach { status ->
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.Start,
						verticalAlignment = Alignment.CenterVertically
					) {
						RadioButton(
							selected = (status == selectedStatus),
							onClick = { onSelectStatus(status) }
						)
						Text(text = status.name, modifier = Modifier.padding(start = 8.dp))
					}
					Spacer(modifier = Modifier.height(8.dp))
				}

				Spacer(modifier = Modifier.height(16.dp))
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {
					Button(onClick = { onDismiss() }) {
						Text("Cancel")
					}
				}
			}
		}
	}
}
