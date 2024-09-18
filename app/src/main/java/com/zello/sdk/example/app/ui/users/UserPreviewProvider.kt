package com.zello.sdk.example.app.ui.users

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.zello.sdk.ZelloUser

class UserPreviewProvider : PreviewParameterProvider<ZelloUser> {
	override val values = sequenceOf(
		ZelloUser(
			name = "adam",
			isMuted = false,
			displayName = "adam display name",
			status = ZelloUser.Status.AVAILABLE,
			customStatusText = "custom status text",
			profilePictureUrl = null,
			profilePictureThumbnailUrl = null,
			supportedFeatures = ZelloUser.SupportedFeatures(groupConversations = true)
		)
	)
}
