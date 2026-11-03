package com.qubix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

    var loggedIn by remember {
        mutableStateOf(auth.currentUser != null)
    }

    var selectedPage by remember {
        mutableStateOf("Chats")
    }

    if (!loggedIn) {
        AuthScreen(
            onLogin = { email, password ->
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        loggedIn = true
                    }
            },
            onSignup = { email, password ->
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        loggedIn = true
                    }
            }
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val pages = listOf(
                        "Chats",
                        "Groups",
                        "Calls",
                        "Notifications",
                        "Profile"
                    )

                    pages.forEach { page ->
                        NavigationBarItem(
                            selected = selectedPage == page,
                            onClick = { selectedPage = page },
                            icon = { Text(
                                when (page) {
                                    "Chats" -> "💬"
                                    "Groups" -> "👥"
                                    "Calls" -> "📞"
                                    "Notifications" -> "🔔"
                                    else -> "👤"
                                }
                            ) },
                            label = { Text(page) }
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (selectedPage) {
                    "Chats" -> ChatScreen {
                        auth.signOut()
                        loggedIn = false
                    }

                    "Groups" -> GroupChatScreen()

                    "Calls" -> CallScreen()

                    "Notifications" -> NotificationScreen()

                    "Profile" -> ProfileScreen {
                        auth.signOut()
                        loggedIn = false
                    }
                }
            }
        }
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
                    status = error.localizedMessage ?: "Loading failed"
                } else {
                    messages = snapshot?.documents?.mapNotNull {
                        it.toObject(ChatMessage::class.java)
                    } ?: emptyList()
                }
            }

        onDispose {
            registration.remove()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "QUBIX Chats",
                style = MaterialTheme.typography.headlineMedium
            )

            TextButton(onClick = onLogout) {
                Text("Logout")
            }
        }

        Text(user?.email ?: "")
        Text(status, color = MaterialTheme.colorScheme.error)

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(messages) { item ->
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
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
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Write a message...") }
            )

            Button(
                onClick = {
                    val text = message.trim()

                    if (user != null && text.isNotEmpty()) {
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

@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }
    val user = auth.currentUser

    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    LaunchedEffect(user?.uid) {
        if (user != null) {
            db.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener {
                    name = it.getString("name") ?: ""
                    bio = it.getString("bio") ?: ""
                }
                .addOnFailureListener {
                    status = it.localizedMessage ?: "Load failed"
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            "QUBIX Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(user?.email ?: "")

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your Name") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("About You") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (user != null) {
                val profile = hashMapOf(
                    "name" to name.trim(),
                    "bio" to bio.trim(),
                    "email" to (user.email ?: "")
                )

                db.collection("users")
                    .document(user.uid)
                    .set(profile)
                    .addOnSuccessListener {
                        status = "Profile saved!"
                    }
                    .addOnFailureListener {
                        status = it.localizedMessage ?: "Save failed"
                    }
            }
        }) {
            Text("Save Profile")
        }

        Text(status)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(onClick = onLogout) {
            Text("Logout")
        }
    }
}
