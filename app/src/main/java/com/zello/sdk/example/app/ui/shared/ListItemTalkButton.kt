package com.zello.sdk.example.app.ui.shared

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zello.sdk.example.app.R

@Composable
fun ListItemTalkButton(
	isEnabled: Boolean,
	isConnecting: Boolean,
	isTalking: Boolean,
	isReceiving: Boolean,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
	onDown: () -> Unit,
	onUp: () -> Unit
) {

	LaunchedEffect(interactionSource) {
		interactionSource.interactions.collect { interaction ->
			when (interaction) {
				is PressInteraction.Press -> {
					onDown()
				}
				is PressInteraction.Release -> {
					onUp()
				}
				is PressInteraction.Cancel -> {
					onUp()
				}
				else -> {}
			}
		}
	}

	Button(
		onClick = {},
		enabled = isEnabled,
		colors = ButtonDefaults.buttonColors(containerColor = if (isTalking) Color.Red else if (isReceiving) Color.Green else Color.Gray),
		interactionSource = interactionSource,
		modifier = Modifier
			.padding(8.dp)

	) {
		Text(text = stringResource(id = if (isConnecting) R.string.connecting else if (isTalking) R.string.talking else if (isReceiving) R.string.receiving else R.string.ptt))
	}
}
