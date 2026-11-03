
package com.qubix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF080D1B)
private val Blue = Color(0xFF2589FF)
private val CardColor = Color(0xFF142139)
private val White = Color.White
private val Gray = Color(0xFF9EACC1)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QubixApp()
        }
    }
}

@Composable
fun QubixApp() {
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Chats", "Groups", "Calls", "Profile")
    val icons = listOf(
        Icons.Default.Chat,
        Icons.Default.Groups,
        Icons.Default.Call,
        Icons.Default.Person
    )

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Blue,
            background = Navy,
            surface = CardColor
        )
    ) {
        Scaffold(
            containerColor = Navy,
            topBar = {
                TopAppBarQubix()
            },
            bottomBar = {
                NavigationBar(
                    containerColor = CardColor
                ) {
                    tabs.forEachIndexed { index, title ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(icons[index], contentDescription = title)
                            },
                            label = { Text(title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Blue,
                                selectedTextColor = Blue,
                                unselectedIconColor = Gray,
                                unselectedTextColor = Gray,
                                indicatorColor = Navy
                            )
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
                when (selectedTab) {
                    0 -> ChatScreen()
                    1 -> GroupsScreen()
                    2 -> CallsScreen()
                    3 -> ProfileScreen()
                }
            }
        }
    }
}

@Composable
fun TopAppBarQubix() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Navy)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                "QUBIX",
                color = Blue,
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "Connect. Communicate. Share.",
                color = Gray,
                fontSize = 11.sp
            )
        }

        IconButton(onClick = {}) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = White
            )
        }
    }
}

@Composable
fun ChatScreen() {
    val names = listOf(
        "Alex Morgan",
        "Sarah Johnson",
        "David Smith",
        "Emma Wilson",
        "Michael Brown"
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Messages",
            color = White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search conversations...") },
            leadingIcon = {
                Icon(Icons.Default.Search, null)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(names) { name ->
                ChatItem(name)
            }
        }
    }
}

@Composable
fun ChatItem(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(Blue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                name.first().toString(),
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.width(13.dp))

        Column {
            Text(
                name,
                color = White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                "Start a conversation",
                color = Gray,
                fontSize = 13.sp
            )
        }
    }

    HorizontalDivider(color = CardColor)
}

@Composable
fun GroupsScreen() {
    SimpleScreen(
        icon = Icons.Default.Groups,
        title = "Your Groups",
        description = "Your group conversations will appear here.",
        button = "Create Group"
    )
}

@Composable
fun CallsScreen() {
    SimpleScreen(
        icon = Icons.Default.Call,
        title = "Calls",
        description = "Your voice call history will appear here.",
        button = "Start a Call"
    )
}

@Composable
fun ProfileScreen() {
    SimpleScreen(
        icon = Icons.Default.Person,
        title = "Your Profile",
        description = "Manage your QUBIX account and profile.",
        button = "Edit Profile"
    )
}

@Composable
fun SimpleScreen(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    button: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Blue,
            modifier = Modifier.size(65.dp)
        )

        Spacer(Modifier.height(18.dp))

        Text(
            title,
            color = White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(10.dp))

        Text(
            description,
            color = Gray,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(22.dp))

        Button(onClick = {}) {
            Text(button)
        }
    }
}
