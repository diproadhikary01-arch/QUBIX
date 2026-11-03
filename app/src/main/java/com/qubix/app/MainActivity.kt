package com.qubix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            QubixAuthApp()
        }
    }
}

@Composable
fun QubixAuthApp() {
    val auth = remember { FirebaseAuth.getInstance() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignup by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var loggedIn by remember {
        mutableStateOf(auth.currentUser != null)
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "QUBIX",
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (loggedIn) {
                    Text("Welcome to QUBIX!")
                    Text(auth.currentUser?.email ?: "")
                    Button(onClick = {
                        auth.signOut()
                        loggedIn = false
                        message = "Logged out"
                    }) {
                        Text("Logout")
                    }
                } else {
                    Text(
                        if (isSignup) "Create Account" else "Login",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {
                        if (email.isBlank() || password.length < 6) {
                            message = "Enter email and password (minimum 6 characters)"
                        } else {
                            val task = if (isSignup) {
                                auth.createUserWithEmailAndPassword(email, password)
                            } else {
                                auth.signInWithEmailAndPassword(email, password)
                            }

                            task.addOnCompleteListener { result ->
                                if (result.isSuccessful) {
                                    loggedIn = true
                                    message = "Success!"
                                } else {
                                    message = result.exception?.localizedMessage
                                        ?: "Authentication failed"
                                }
                            }
                        }
                    }) {
                        Text(if (isSignup) "Sign Up" else "Login")
                    }

                    TextButton(onClick = {
                        isSignup = !isSignup
                        message = ""
                    }) {
                        Text(
                            if (isSignup) "Already have an account? Login"
                            else "Create new account"
                        )
                    }

                    Text(message)
                }
            }
        }
    }
}
