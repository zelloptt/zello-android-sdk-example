package com.zello.sdk.example.app.ui.channels

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zello.sdk.ZelloChannel

class ChannelPreviewProvider : PreviewParameterProvider<ZelloChannel> {
	override val values = sequenceOf(
		ZelloChannel(
			name = "#1 channel",
			isMuted = false,
			status = ZelloChannel.ConnectionStatus.CONNECTED,
			usersOnline = 1,
			options = ZelloChannel.Options(
				noDisconnect = false,
				hidePowerButton = false,
				listenOnly = false,
				allowAlerts = false,
				allowTextMessages = false,
				allowLocations = false,
				emergencyOnly = false
			)
		)
	)
}
