
package com.qubix.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotificationScreen() {
    var notifications by remember {
        mutableStateOf(
            listOf(
                "Welcome to QUBIX!",
                "Your account is ready."
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            "Notifications",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (notifications.isEmpty()) {
            Text("No notifications yet.")
        } else {
            notifications.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Text(
                        item,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
