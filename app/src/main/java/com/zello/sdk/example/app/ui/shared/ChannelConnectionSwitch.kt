package com.zello.sdk.example.app.ui.shared

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import com.zello.sdk.ZelloChannel

@Composable
fun ChannelConnectionSwitch(channel: ZelloChannel, onCheckChanged: (Boolean) -> Unit) {
	val isConnected = channel.status == ZelloChannel.ConnectionStatus.CONNECTED
	Switch(
		checked = isConnected,
		enabled = !(channel.options.noDisconnect && isConnected) && channel.status != ZelloChannel.ConnectionStatus.CONNECTING,
		onCheckedChange = { onCheckChanged(it) }
	)
}
