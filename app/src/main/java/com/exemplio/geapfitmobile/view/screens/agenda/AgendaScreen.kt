import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.exemplio.geapfitmobile.view.screens.agenda.AgendaViewModel
import com.exemplio.geapfitmobile.view.core.components.HeaderSection
import com.exemplio.geapfitmobile.view.core.components.ModalDialogError
import com.exemplio.geapfitmobile.view.core.components.ModalDialogSession
import com.exemplio.geapfitmobile.view.core.components.kalendar.Kalendar
import com.exemplio.geapfitmobile.view.core.navigation.Login
import java.time.LocalDate
import java.time.YearMonth

// --- Data Models ---
data class CalendarEvent(val day: Int, val label: String)

val sampleEvents = listOf(
    CalendarEvent(2, "Cumple"),
    CalendarEvent(2, "Cumple"),
    CalendarEvent(9, "Evento especial")
)

@Composable
fun AgendaScreen(principalNavHost: NavHostController ,agendaViewModel: AgendaViewModel = hiltViewModel()) {
    var currentMonth by remember { mutableStateOf(YearMonth.of(2025, 3)) }
    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }

    val uiState by agendaViewModel.uiState.collectAsStateWithLifecycle()
    val mockClients by agendaViewModel.agendas.collectAsState()

    val showModalErr = remember { mutableStateOf(false) }
    val showModalSession = remember { mutableStateOf(false) }
    val modalMessage = remember { mutableStateOf("") }

    LaunchedEffect(uiState.initialState) {
        if (uiState.initialState) {
            agendaViewModel.getAgenda()
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
            HeaderSection("Agenda", onCloseSession = {
                showModalSession.value = true
            })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add new event */ },
                containerColor = Color.Black,
                shape = CircleShape
            ) { Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Red) }
        }
    ) { padding ->
            AnimatedContent(targetState = uiState) { state ->
                if (state.loaded) {
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        CalendarKPIs()
                        Column(modifier = Modifier.weight(1f)) {
                            Kalendar()
                        }
                        Text("Prueba")
                    }
                }
                if (uiState.isLoading) {
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
                        agendaViewModel.closeSession()
                        principalNavHost.navigate(Login) {
                            popUpTo(Login) { inclusive = true }
                        }
                    }
                )
            }
        }
}

@Composable
fun CalendarKPIs() {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        KpiItem(icon = Icons.Default.Person, label = "Total sesiones", value = "0%")
        KpiItem(icon = Icons.AutoMirrored.Filled.ArrowBack, label = "Clientes en cola", value = "0%")
        KpiItem(icon = Icons.AutoMirrored.Filled.ArrowForward, label = "Asistencia", rate = "0%")
    }
}

@Composable
fun KpiItem(icon: ImageVector, value: String? = null, label: String, rate: String? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = Color(0xFFB0BEC5), modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (value != null) {
                Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
            }
            if (rate != null) {
                Spacer(Modifier.width(2.dp))
                Text(rate, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF3983E8))
            }
        }
        Text(label, fontSize = 13.sp, color = Color(0xFFB0BEC5))
    }
}

//@Composable
//fun AgendaCalendar(
//    currentMonth: YearMonth,
////    selectedDay: LocalDate?,
////    onDaySelected: (LocalDate) -> Unit
//) {
//    val today = LocalDate.now()
//    val firstDayOfMonth = currentMonth.atDay(1)
//    val lastDayOfMonth = currentMonth.atEndOfMonth()
//    val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.value % 7) // 0 (Sun) to 6 (Sat)
//
//    val daysInMonth = (1..lastDayOfMonth.dayOfMonth).map { currentMonth.atDay(it) }
//    val calendarDays = buildList<LocalDate?> {
//        repeat(firstDayOfWeek) { add(null) }
//        addAll(daysInMonth)
//        val remaining = (7 - (size % 7)) % 7
//        repeat(remaining) { add(null) }
//    }
//    Spacer(Modifier.height(8.dp))
//    Column(Modifier.padding(horizontal = 2.dp)) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 0.dp),
//            horizontalArrangement = Arrangement.SpaceEvenly,
//        ) {
//            listOf("L", "M", "X", "J", "V", "S", "D").forEach { dayName ->
//                Text(
//                    dayName,
//                    fontWeight = FontWeight.Bold,
//                    color = Color(0xFFB0BEC5),
//                    fontSize = 16.sp,
//                    modifier = Modifier.padding(horizontal = 8.dp)
//                )
//            }
//        }
//        calendarDays.chunked(7).forEach { week ->
//            Row(
//                Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                week.forEach { day ->
//                    Box(
//                        modifier = Modifier
//                            .weight(1f)
//                            .aspectRatio(1f)
//                            .padding(1.dp)
//                            .clip(RoundedCornerShape(14.dp))
////                            .background(
////                                when {
////                                    day == selectedDay -> Color(0xFF703EFE)
////                                    day == today -> Color(0xFF703EFE).copy(alpha = 0.08f)
////                                    else -> Color.Transparent
////                                }
////                            )
////                            .clickable(day != null) { day?.let { onDaySelected(it) } },
////                        contentAlignment = Alignment.TopCenter
//                    ) {
//                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
//                            if (day == null) {
//                                Spacer(Modifier.height(24.dp))
//                            } else {
//                                Text(
//                                    day.dayOfMonth.toString(),
////                                    color = if (day == selectedDay) Color.White else Color(0xFF303030),
//                                    fontWeight = FontWeight.Bold,
//                                    fontSize = 17.sp,
//                                    modifier = Modifier.padding(top = 8.dp)
//                                )
//                                if (day == LocalDate.of(2025, 3, 9)) {
//                                    Spacer(Modifier.height(12.dp))
//                                    Box(
//                                        Modifier
//                                            .size(20.dp)
//                                            .clip(CircleShape)
//                                            .background(Color(0xFF303030)),
//                                        contentAlignment = Alignment.Center
//                                    ) {}
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}