import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.navigation.NavHostController
import com.exemplio.geapfitmobile.view.core.components.ErrorBanner
import com.exemplio.geapfitmobile.view.core.components.HeaderSection
import com.exemplio.geapfitmobile.view.core.components.ModalDialogError
import com.exemplio.geapfitmobile.view.core.components.ModalDialogSession
import com.exemplio.geapfitmobile.view.core.navigation.Login
import com.exemplio.geapfitmobile.view.screens.client.ClientViewModel

@Composable
fun ClientScreen(
    clientViewModel: ClientViewModel = hiltViewModel()
) {

    val uiState by clientViewModel.uiState.collectAsStateWithLifecycle()
    val mockClients by clientViewModel.clients.collectAsState()

    val showModalErr = remember { mutableStateOf(false) }
    val showModalSession = remember { mutableStateOf(false) }
    val modalMessage = remember { mutableStateOf("") }

    LaunchedEffect(uiState.initialState) {
        if (uiState.initialState) {
            clientViewModel.getClients()
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
            HeaderSection("Cliente", onCloseSession = {
                showModalSession.value = true
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
                    ErrorBanner()
//                InfoCard()
                    ClientAssignmentDropdown()
                    SearchAndFilterSection()
                    ClientListSection(mockClients)
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
                    clientViewModel.closeSession()
                }
            )
        }
    }
}

@Composable
fun ClientAssignmentDropdown() {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf("10 Clientes asignados") }
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable { expanded = true }
            .padding(vertical = 12.dp, horizontal = 14.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selectedText, fontSize = 15.sp, color = Color.White, modifier = Modifier.weight(1f))
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFFB0BEC5))
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("10 Clientes asignados") },
                    onClick = {
                        selectedText = "10 Clientes asignados"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Todos los clientes") },
                    onClick = {
                        selectedText = "Todos los clientes"
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SearchAndFilterSection() {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Nombre o email") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(40.dp),
            singleLine = true
        )
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = { /* Filtrar */ }) {
            Icon(Icons.Default.Check, contentDescription = "Filtrar")
        }
        IconButton(onClick = { /* Selección múltiple */ }) {
            Icon(Icons.Default.CheckCircle, contentDescription = "Selección múltiple")
        }
    }
}

@Composable
fun ClientListSection(clients: List<Client>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
    ) {
        items(clients) { client ->
            ClientListItem(client)
        }
    }
}

@Composable
fun ClientListItem(client: Client) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(12.dp),
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
//            Text(client.lastActivity.stringValue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            client.createdAt?.let { Text(it, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
        }
        Text(
//            client.status.stringValue,
            client.email,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 13.sp,
            color = Color.Black
        )
        IconButton(onClick = { /* More options */ }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Más")
        }
    }
}