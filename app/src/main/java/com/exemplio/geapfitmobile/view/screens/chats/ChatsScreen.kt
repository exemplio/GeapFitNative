import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.exemplio.geapfitmobile.view.core.components.HeaderSection
import com.exemplio.geapfitmobile.view.core.components.ModalDialogError
import com.exemplio.geapfitmobile.view.core.components.ModalDialogSession
import com.exemplio.geapfitmobile.view.screens.chats.ChatsViewModel
import com.exemplio.geapfitmobile.view.core.navigation.Login
import com.exemplio.geapfitmobile.view.core.navigation.TabScreens
import com.exemplio.geapfitmobile.view.core.navigation.TabScreens.TabContacts

data class ChatItem(
    val initials: String,
    val name: String,
    val date: String,
    val lastMessage: String
)

val chats = listOf(
    ChatItem("MO", "MATEO OPPEN", "01-03-2025", "Hola, bienvenido a GEAP ACADEMY! Es..."),
    ChatItem("LL", "LAUTARO LOPEZ FERNANDEZ", "17-02-2025", "entrenamiento elite grupal"),
    ChatItem("UH", "URIEL HENDLER", "12-02-2025", "Tenkiu"),
    ChatItem("AB", "ADRIAN BLUM", "12-02-2025", "Welcome to the new app"),
    ChatItem("SL", "SEBASTIÁN LIBERMAN", "11-02-2025", "Tipo medidas físicas, fotos bla bla"),
    ChatItem("AG", "ALEJANDRO GONZÁLEZ ...", "10-02-2025", "entrenamiento elite grupal"),
)

@Composable
fun ChatsScreen(
    chatsViewModel: ChatsViewModel = hiltViewModel()
) {
    val uiState by chatsViewModel.uiState.collectAsStateWithLifecycle()
    val mockClients by chatsViewModel.chats.collectAsState()

    val showModalErr = remember { mutableStateOf(false) }
    val showModalSession = remember { mutableStateOf(false) }
    val modalMessage = remember { mutableStateOf("") }

    LaunchedEffect(uiState.initialState) {
        if (uiState.initialState) {
            chatsViewModel.getClients()
        }
    }

    LaunchedEffect(uiState.errorCode, uiState.errorMessage) {
        if (uiState.errorCode != null && uiState.errorCode != 200) {
            modalMessage.value = uiState.errorMessage ?: "Error para conectar con el servidor"
            showModalErr.value = true
        }
    }

    var selectedTab by remember { mutableStateOf(0) }
    var showOnlyAssigned by remember { mutableStateOf(true) }
    Scaffold(
        topBar = {
            HeaderSection("Chats", onCloseSession = {
                showModalSession.value = true
            })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    GlobalNav.root?.navigate(TabContacts) {
                    popUpTo(TabContacts) { inclusive = true }
                }},
                containerColor = Color.Black,
                shape = CircleShape
            ) { Icon(Icons.Default.Add, contentDescription = "Add") }
        }
    ) { padding ->
        AnimatedContent(targetState = uiState) { state ->
            if (!state.isLoading && state.loaded) {
                Column(
                    Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Tabs(selectedTab) { selectedTab = it }
                    CheckboxRow(showOnlyAssigned) { showOnlyAssigned = it }
                    MassMessageButton()
                    SearchField()
                    UnreadBanner(unreadCount = 0)
                    ChatListSection(chats)
                }
            }
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        if (showModalErr.value) {
            ModalDialogError(
                message = modalMessage.value,
                onDismiss = {
                    showModalErr.value = false;
                    modalMessage.value = ""
                    uiState.errorCode = null
                    uiState.errorMessage = null
                }
            )
        }
        if (showModalSession.value) {
            ModalDialogSession(
                message = "¿Desea cerrar sesión?",
                onDismiss = {
                    showModalSession.value = false;
                    modalMessage.value = ""
                    uiState.errorCode = null
                    uiState.errorMessage = null
                },
                onLogout = {
                    showModalSession.value = false;
                    chatsViewModel.closeSession()
                }
            )
        }
    }
}

@Composable
fun Tabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.background)
            .height(44.dp)
    ) {
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(if (selectedTab == 0) Color.Black else MaterialTheme.colorScheme.background)
                .clickable { onTabSelected(0) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Clientes",
                color = if (selectedTab == 0) Color.White else Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(if (selectedTab == 1) Color.Black else MaterialTheme.colorScheme.background)
                .clickable { onTabSelected(1) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Colaboradores",
                color = if (selectedTab == 1) MaterialTheme.colorScheme.background else Color.Black,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun CheckboxRow(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = Color.Black)
        )
        Text(
            "Ver solo mis clientes asignados",
            fontSize = 15.sp,
            color = Color.Black
        )
    }
}

@Composable
fun MassMessageButton() {
    Button(
        onClick = { /* Mass message */ },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(48.dp)
    ) {
        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.Black)
        Text(
            "Mensaje masivo",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}

@Composable
fun SearchField() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = { Text("Buscar por nombre") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(40.dp),
        singleLine = true
    )
}

@Composable
fun UnreadBanner(unreadCount: Int) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8F0FE))
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🎂", fontSize = 17.sp)
            Spacer(Modifier.width(10.dp))
            Text(
                "Tienes $unreadCount chats sin leer",
                fontSize = 15.sp,
                color = Color(0xFF505050)
            )
        }
    }
}

@Composable
fun ChatListSection(chats: List<ChatItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
    ) {
        items(chats) { chat ->
            ChatListItem(chat)
        }
    }
}

@Composable
fun ChatListItem(chat: ChatItem) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
            .clickable { GlobalNav.root?.navigate(TabScreens.TabSingleChat) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF703EFE)),
            contentAlignment = Alignment.Center
        ) {
            Text(chat.initials, color = MaterialTheme.colorScheme.background, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(chat.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(chat.lastMessage, fontSize = 14.sp, color = Color(0xFF757575), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(
            chat.date,
            fontSize = 13.sp,
            color = Color(0xFFB0BEC5),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}