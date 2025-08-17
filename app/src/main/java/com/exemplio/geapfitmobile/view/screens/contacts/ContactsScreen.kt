import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exemplio.geapfitmobile.view.core.components.ModalDialogError
import com.exemplio.geapfitmobile.view.core.components.ModalDialogSession
import com.exemplio.geapfitmobile.view.core.components.TopBar
import com.exemplio.geapfitmobile.view.core.navigation.Login
import com.exemplio.geapfitmobile.view.core.navigation.TabScreens
import com.exemplio.geapfitmobile.view.core.navigation.TabScreens.TabChats
import com.exemplio.geapfitmobile.view.screens.contacts.ContactsViewModel

@Composable
fun ContactsScreen(
    contactsViewModel: ContactsViewModel = hiltViewModel()
) {

    val uiState by contactsViewModel.uiState.collectAsStateWithLifecycle()
    val mockClients by contactsViewModel.contacts.collectAsState()

    val showModalErr = remember { mutableStateOf(false) }
    val showModalSession = remember { mutableStateOf(false) }
    val modalMessage = remember { mutableStateOf("") }

    LaunchedEffect(uiState.initialState) {
        if (uiState.initialState) {
            contactsViewModel.getClients()
        }
    }

    LaunchedEffect(uiState.errorCode, uiState.errorMessage) {
        if (uiState.errorCode != null && uiState.errorCode != 200) {
            modalMessage.value = uiState.errorMessage ?: "Error para conectar con el servidor"
            showModalErr.value = true
        }
    }

    Scaffold(
        topBar = {
            TopBar("Contactos", onCloseSession = {
                showModalSession.value = true
            },
            goBack = {
                println(GlobalNav.bottom)
                GlobalNav.navigateUpRoot()
            })
        },
    ) { padding ->
        AnimatedContent(targetState = uiState, label = "") { state ->
            if (state.loaded) {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    ContactsListSection(mockClients)
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
                    contactsViewModel.closeSession()
                }
            )
        }
    }
}

@Composable
fun ContactsListSection(clients: List<Client>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
    ) {
        items(clients) { client ->
            ContactsListItem(client)
        }
    }
}

@Composable
fun ContactsListItem(client: Client) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp)
            .clickable {
                GlobalNav.root?.navigate("TabSingleChat/${client.userId}") {
                    popUpTo(Login) { inclusive = true }
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF703EFE)),
            contentAlignment = Alignment.Center
        ) {
            Text("PF", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            client.displayName?.let { Text(it, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
        }
        Text(
            client.email,
            fontSize = 13.sp,
            color = Color.Black
        )
    }
}