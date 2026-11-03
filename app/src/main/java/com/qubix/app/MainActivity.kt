package com.qubix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

    var selectedPage by remember { mutableStateOf("Chat") }

    if (!loggedIn) {
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
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = { selectedPage = "Chat" }) {
                    Text("Chats")
                }
                TextButton(onClick = { selectedPage = "Profile" }) {
                    Text("Profile")
                }
            }

            HorizontalDivider()

            when (selectedPage) {
                "Chat" -> ChatScreen(
                    onLogout = {
                        auth.signOut()
                        loggedIn = false
                    }
                )
                "Profile" -> ProfileScreen(
                    onLogout = {
                        auth.signOut()
                        loggedIn = false
                    }
                )
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
    var status by remember { mutableStateOf("Loading profile...") }

    LaunchedEffect(user?.uid) {
        if (user != null) {
            db.collection("users")
                .document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    name = document.getString("name") ?: ""
                    bio = document.getString("bio") ?: ""
                    status = ""
                }
                .addOnFailureListener {
                    status = it.localizedMessage ?: "Could not load profile"
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("QUBIX PROFILE", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        Text(user?.email ?: "No email")

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your Name") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("About You") },
            minLines = 3
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
                        status = "Profile saved successfully!"
                    }
                    .addOnFailureListener {
                        status = it.localizedMessage ?: "Save failed"
                    }
            }
        }) {
            Text("Save Profile")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(status)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(onClick = onLogout) {
            Text("Logout")
        }
    }
}
