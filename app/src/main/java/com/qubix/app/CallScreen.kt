
package com.qubix.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CallScreen() {
    var callStatus by remember { mutableStateOf("Ready to call") }
    var isCalling by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "QUBIX Voice Call",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(callStatus)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isCalling = !isCalling
                callStatus = if (isCalling) {
                    "Call interface opened"
                } else {
                    "Call ended"
                }
            }
        ) {
            Text(if (isCalling) "End Call" else "Start Call")
        }
    }
}
