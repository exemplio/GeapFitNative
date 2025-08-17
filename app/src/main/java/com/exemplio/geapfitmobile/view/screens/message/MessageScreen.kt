package com.exemplio.geapfitmobile.view.screens.message

import GlobalNav
import MessageReceive
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exemplio.geapfitmobile.ui.theme.PurpleGrey80
import com.exemplio.geapfitmobile.view.core.components.TopBar

@Composable
fun MessageScreen(
    thirdUserId: String? = null,
    receiveChatId: String? = null,
    messageViewModel: MessageViewModel = hiltViewModel(),
) {
    val uiState by messageViewModel.uiState.collectAsStateWithLifecycle()
    val mockSMS by messageViewModel.messages.collectAsState()
    val showModalErr = remember { mutableStateOf(false) }
    val showModalSession = remember { mutableStateOf(false) }
    val modalMessage = remember { mutableStateOf("") }

    LaunchedEffect(uiState.connected) {
        if (uiState.connected) {
            messageViewModel.getMessages()
        }
    }

    LaunchedEffect(uiState.disconnected) {
        if (uiState.disconnected) {
            messageViewModel.connect()
        }
    }

    LaunchedEffect(thirdUserId) {
        messageViewModel.setUserId(thirdUserId)
    }

    LaunchedEffect(receiveChatId) {
        messageViewModel.setReceiveChatId(receiveChatId)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> messageViewModel.connect()
                Lifecycle.Event.ON_STOP -> {/* Optionally disconnect */}
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopBar("Messages", onCloseSession = {
                showModalSession.value = true
            }, goBack = {
                GlobalNav.navigateUpRoot()
            })
        },
    ){ padding ->
        ConstraintLayout(
            Modifier
                .fillMaxSize()
                .padding(padding)) {
            val (messages, chatBox) = createRefs()
            val listState = rememberLazyListState()
            LaunchedEffect(mockSMS.size) {
                println("Esto son los mensajes $mockSMS")
                listState.animateScrollToItem(mockSMS.size)
            }
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(messages) {
                        top.linkTo(parent.top)
                        bottom.linkTo(chatBox.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    },
                contentPadding = PaddingValues(16.dp)
            ) {
                items(mockSMS) { item ->
                    MessageItem(item)
                }
            }
            MessageBox(
                messageViewModel=messageViewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(chatBox) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )
        }
    }
}

@Composable
fun MessageItem(message: MessageReceive?) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)) {
        Box(
            modifier = Modifier
//                .align(if (message.isFromMe) Alignment.End else Alignment.Start)
                .align(Alignment.Start)
//                .clip(
//                    RoundedCornerShape(
//                        topStart = 48f,
//                        topEnd = 48f,
//                        bottomStart = if (message.isFromMe) 48f else 0f,
//                        bottomEnd = if (message.isFromMe) 0f else 48f
//                    )
//                )
                .background(PurpleGrey80)
                .padding(16.dp)
        ) {
//            print("Message content: ${message.content}")
            Text(text = message?.content ?: "")
        }
    }
}

@Composable
fun MessageBox(
    modifier: Modifier,
    messageViewModel: MessageViewModel
) {
    var chatBoxValue by remember { mutableStateOf(TextFieldValue("")) }
    Row(modifier = modifier.padding(16.dp)) {
        TextField(
            value = chatBoxValue,
            onValueChange = { newText ->
                chatBoxValue = newText
            },
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            shape = RoundedCornerShape(24.dp),
//            colors = TextFieldDefaults.textFieldColors(
//                focusedIndicatorColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent,
//                disabledIndicatorColor = Color.Transparent
//            ),
            placeholder = {
                Text(text = "Type something")
            }
        )
        IconButton(
            onClick = {
                val msg = chatBoxValue.text
                if (msg.isBlank()) return@IconButton
                onSendMessageClickListener(chatBoxValue.text,messageViewModel)
                chatBoxValue = TextFieldValue("")
            },
            modifier = Modifier
                .clip(CircleShape)
                .background(color = PurpleGrey80)
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = "Send",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )
        }
    }
}

fun onSendMessageClickListener(content: String? = null, messageViewModel: MessageViewModel) {
    messageViewModel.send(content)
}