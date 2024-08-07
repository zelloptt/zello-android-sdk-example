package com.zello.sdk.example.app.ui.shared.types

import com.zello.sdk.ZelloIncomingEmergency

data class IncomingEmergenciesViewState(
	val emergencies: List<ZelloIncomingEmergency>
)
