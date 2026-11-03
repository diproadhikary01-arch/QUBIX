
package com.qubix.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class GroupMessage(
    val senderId: String = "",
    val senderEmail: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)

@Composable
fun GroupChatScreen() {
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }

    var groupId by remember { mutableStateOf("public") }
    var message by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<GroupMessage>()) }
    var status by remember { mutableStateOf("") }

    DisposableEffect(groupId) {
        val registration = db.collection("groups")
            .document(groupId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    status = error.localizedMessage ?: "Could not load messages"
                } else {
                    messages = snapshot?.documents?.mapNotNull {
                        it.toObject(GroupMessage::class.java)
                    } ?: emptyList()
                }
            }

        onDispose { registration.remove() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("QUBIX Group Chat", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = groupId,
            onValueChange = { groupId = it.trim() },
            label = { Text("Group ID") },
            singleLine = true
        )

        Text(status, color = MaterialTheme.colorScheme.error)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { item ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(item.senderEmail, style = MaterialTheme.typography.labelSmall)
                    Text(item.message)
                }
                HorizontalDivider()
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                label = { Text("Message") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                val user = auth.currentUser
                val text = message.trim()

                if (user != null && text.isNotEmpty() && groupId.isNotBlank()) {
                    val data = GroupMessage(
                        senderId = user.uid,
                        senderEmail = user.email ?: "User",
                        message = text,
                        timestamp = System.currentTimeMillis()
                    )

                    db.collection("groups")
                        .document(groupId)
                        .collection("messages")
                        .add(data)
                        .addOnSuccessListener {
                            message = ""
                            status = ""
                        }
                        .addOnFailureListener {
                            status = it.localizedMessage ?: "Send failed"
                        }
                }
            }, enabled = message.isNotBlank() && groupId.isNotBlank()) {
                Text("Send")
            }
        }
    }
}
