package com.zello.sdk.example.app.ui.shared.types

import com.zello.sdk.ZelloContact
import com.zello.sdk.ZelloOutgoingVoiceMessage

data class OutgoingVoiceMessageViewState(
	val contact: ZelloContact,
	val state: ZelloOutgoingVoiceMessage.State
)
