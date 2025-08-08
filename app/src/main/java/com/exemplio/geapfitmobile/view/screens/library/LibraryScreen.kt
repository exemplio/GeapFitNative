import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.exemplio.geapfitmobile.view.core.components.HeaderSection
import com.exemplio.geapfitmobile.view.core.components.ModalDialogError
import com.exemplio.geapfitmobile.view.core.components.ModalDialogSession
import com.exemplio.geapfitmobile.view.core.navigation.Login
import com.exemplio.geapfitmobile.view.home.screens.library.LibraryViewModel


@Composable
fun LibraryScreen(
    principalNavHost: NavHostController,
    libraryViewModel: LibraryViewModel = hiltViewModel()
) {

    val uiState by libraryViewModel.uiState.collectAsStateWithLifecycle()
    val mockClients by libraryViewModel.library.collectAsState()

    val showModalErr = remember { mutableStateOf(false) }
    val showModalSession = remember { mutableStateOf(false) }
    val modalMessage = remember { mutableStateOf("") }

    LaunchedEffect(uiState.initialState) {
        if (uiState.initialState) {
            libraryViewModel.getClients()
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
            HeaderSection("Librería", onCloseSession = {
                showModalSession.value = true
            })
        },
    ) { padding ->
        AnimatedContent(targetState = uiState) { state ->
            if (!state.isLoading && state.loaded) {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    LibrarySearchBar(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(libraryItems) { item ->
                            LibraryVideoCard(item, modifier = Modifier.padding(8.dp))
                        }
                    }
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
                    libraryViewModel.closeSession()
                    principalNavHost.navigate(Login) {
                        popUpTo(Login) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun LibraryTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "User",
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFB0BEC5), shape = CircleShape)
                .padding(4.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Librería",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { /* Help action */ }) {
            Icon(Icons.Default.Menu, contentDescription = "Help")
        }
        IconButton(onClick = { /* Menu action */ }) {
            Icon(Icons.Default.Menu, contentDescription = "Menu")
        }
    }
}

@Composable
fun LibrarySearchBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Busca por nombre...") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(40.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = { /* Filter action */ }) {
            Icon(Icons.Default.Settings, contentDescription = "Filtrar")
        }
    }
}


@Composable
fun LibraryVideoCard(item: LibraryItem, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
    ) {
        Column {
            Box(modifier = Modifier.height(170.dp)) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                // ...rest of your UI as before
            }
            // ...rest of your UI as before
        }
    }
}
@Composable
fun ChipTag(text: String) {
    Box(
        modifier = Modifier
            .background(color = Color(0xFF1A1A1A), shape = RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = Color(0xFFE53935), fontSize = 13.sp)
    }
}

data class LibraryItem(
    val title: String,
    val imageUrl: String,
    val tags: List<String>
)

val libraryItems = listOf(
    LibraryItem(
        title = "1/2 Dominada con agarre neutro",
        imageUrl = "https://img.youtube.com/vi/iqBkUGKd4Eo/maxresdefault.jpg",
        tags = listOf(
            "barra de dominadas", "bilateral", "tracción", "dominadas",
            "intermedio", "fuerza", "dorsal", "espalda", "compuesto", "tren superior"
        )
    ),
    LibraryItem(
        title = "1/2 Dominada con agarre neutro asistida con goma",
        imageUrl = "https://img.youtube.com/vi/MPzfQMxrjdQ/maxresdefault.jpg",
        tags = listOf(
            "barra de dominadas", "goma elástica", "bilateral", "tracción",
            "dominadas", "principiante", "fuerza", "dorsal", "espalda", "compuesto", "tren superior"
        )
    ),
    LibraryItem(
        title = "1/2 Dominada con agarre prono",
        imageUrl = "https://img.youtube.com/vi/wKBYBuqJlk8/maxresdefault.jpg",
        tags = listOf(
            "barra de dominadas", "bilateral", "tracción", "dominadas",
            "intermedio", "fuerza", "dorsal", "espalda", "compuesto", "tren superior"
        )
    )
)