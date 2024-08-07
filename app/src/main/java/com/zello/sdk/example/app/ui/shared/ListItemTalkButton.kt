package com.zello.sdk.example.app.ui.shared

import android.view.MotionEvent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zello.sdk.example.app.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ListItemTalkButton(isEnabled: Boolean, isConnecting: Boolean, isTalking: Boolean, isReceiving: Boolean, onDown: () -> Unit, onUp: () -> Unit) {
	var isPressed by remember { mutableStateOf(false) }
	Button(onClick = {},
		enabled = isEnabled,
		colors = ButtonDefaults.buttonColors(containerColor = if (isTalking) Color.Red else if (isReceiving) Color.Green else Color.Gray),
		modifier = Modifier
			.padding(8.dp)
			.pointerInteropFilter {
				if (!isEnabled) {
					return@pointerInteropFilter false
				}
				when (it.action) {
					MotionEvent.ACTION_DOWN -> {
						isPressed = true
						onDown()
						true
					}
					MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
						isPressed = false
						onUp()
						true
					}
					else -> false
				}
			}) {
		Text(text = stringResource(id = if (isConnecting) R.string.connecting else if (isTalking) R.string.talking else if (isReceiving) R.string.receiving else R.string.ptt))
	}
}
