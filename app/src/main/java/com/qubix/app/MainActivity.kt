
package com.qubix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class ChatMessage(
    val senderId: String = "",
    val senderEmail: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                QubixApp()
            }
        }
    }
}

@Composable
fun QubixApp() {
    val auth = remember { FirebaseAuth.getInstance() }
    var loggedIn by remember { mutableStateOf(auth.currentUser != null) }

    if (loggedIn) {
        ChatScreen(onLogout = {
            auth.signOut()
            loggedIn = false
        })
    } else {
        AuthScreen(
            onLogin = { email, password ->
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { loggedIn = true }
            },
            onSignup = { email, password ->
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { loggedIn = true }
            }
        )
    }
}

@Composable
fun ChatScreen(onLogout: () -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }
    val user = auth.currentUser

    var message by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var status by remember { mutableStateOf("") }

    DisposableEffect(Unit) {
        val registration = db.collection("chats")
            .document("public")
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    status = error.localizedMessage ?: "Could not load messages"
                } else {
                    messages = snapshot?.documents?.mapNotNull {
                        it.toObject(ChatMessage::class.java)
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("QUBIX Chat", style = MaterialTheme.typography.headlineMedium)
            TextButton(onClick = onLogout) {
                Text("Logout")
            }
        }

        Text(user?.email ?: "", style = MaterialTheme.typography.bodySmall)
        Text(status, color = MaterialTheme.colorScheme.error)

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = false
        ) {
            items(messages) { item ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        item.senderEmail,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(item.message)
                }
                HorizontalDivider()
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Write a message...") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    val text = message.trim()
                    if (text.isNotEmpty() && user != null) {
                        val data = ChatMessage(
                            senderId = user.uid,
                            senderEmail = user.email ?: "User",
                            message = text,
                            timestamp = System.currentTimeMillis()
                        )

                        db.collection("chats")
                            .document("public")
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
                },
                enabled = message.isNotBlank()
            ) {
                Text("Send")
            }
        }
    }
}
