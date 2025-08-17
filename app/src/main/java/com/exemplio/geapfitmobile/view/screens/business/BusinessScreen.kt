import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.exemplio.geapfitmobile.view.screens.business.BusinessViewModel
import com.exemplio.geapfitmobile.view.core.components.HeaderSection
import com.exemplio.geapfitmobile.view.core.components.ModalDialogError
import com.exemplio.geapfitmobile.view.core.components.ModalDialogSession
import com.exemplio.geapfitmobile.view.core.navigation.Login

@Composable
fun BusinessScrenn(
    businessViewModel: BusinessViewModel = hiltViewModel()
) {

    val uiState by businessViewModel.uiState.collectAsStateWithLifecycle()
    val mockClients by businessViewModel.businessInfo.collectAsState()

    val showModalErr = remember { mutableStateOf(false) }
    val showModalSession = remember { mutableStateOf(false) }    
    val modalMessage = remember { mutableStateOf("") }

    LaunchedEffect(uiState.initialState) {
        if (uiState.initialState) {
            businessViewModel.getClients()
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
            HeaderSection("Negocio", onCloseSession = {
                showModalSession.value = true
            })
        },
    ) { padding ->
        AnimatedContent(targetState = uiState) { state ->
            if (!state.isLoading && state.loaded) {
                Column(
                    Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    DateRangeSelector("02/03/2025 - 09/03/2025")
                    BusinessKpiGrid()
                    PaymentsExternalSection()
                    Spacer(Modifier.height(12.dp))
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
                    businessViewModel.closeSession()
                }
            )
        }
    }
}

@Composable
fun DateRangeSelector(dateRange: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 2.dp)
            .padding(horizontal = 36.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 6.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                dateRange,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            Icon(
                imageVector = Icons.Default.Settings, // substitute for calendar icon
                contentDescription = null,
                tint = Color(0xFFB0BEC5),
                modifier = Modifier.padding(start = 3.dp).size(20.dp)
            )
        }
    }
}

@Composable
fun BusinessKpiGrid() {
    Column(
        Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BusinessKpiBox(
                icon = Icons.Default.Settings,
                value = "0.00\$",
                label = "Total Vendido"
            )
            BusinessKpiBox(
                icon = Icons.Default.Settings,
                value = "No disponible",
                label = "Ventas en Harbiz",
                actionIcon = Icons.Default.Add
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BusinessKpiBox(
                icon = Icons.Default.Settings,
                value = "No disponible",
                label = "Ventas Externas",
                actionIcon = Icons.Default.Add
            )
            BusinessKpiBox(
                icon = Icons.Default.Settings,
                value = "0.00\$",
                label = "Impagos y reembolsos"
            )
        }
    }
}

@Composable
fun BusinessKpiBox(
    icon: ImageVector,
    value: String,
    label: String,
    actionIcon: ImageVector? = null
) {
    Box(
        Modifier
            .height(92.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(18.dp))
            .padding(13.dp)
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFFB0BEC5), modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    value,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF90A4AE),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.weight(1f))
                if (actionIcon != null) {
                    Box(
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1A1A1A))
                            .clickable { /* Add action */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(actionIcon, contentDescription = null, tint = MaterialTheme.colorScheme.background, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Text(
                label,
                fontSize = 14.sp,
                color = Color(0xFFB0BEC5),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PaymentsExternalSection() {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Pagos Externos",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE3EDFF))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    "0.00\$",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF3983E8)
                )
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(
            "Ingresos por el total de ventas con pagos externos",
            fontSize = 15.sp,
            color = Color(0xFF8C8C8C),
            modifier = Modifier.padding(bottom = 11.dp)
        )
        // Placeholder for chart
        Box(
            Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFB1D7FE).copy(alpha = 0.23f),
                            Color.Transparent
                        )
                    )
                ),
            contentAlignment = Alignment.BottomStart
        ) {
            // Replace with real chart (e.g., MPAndroidChart, Compose chart lib, or your own)
            SampleLineChart()
        }
    }
}

@Composable
fun SampleLineChart() {
    // Placeholder: a polyline using Canvas or a chart library is recommended for production
//    Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 14.dp)) {
//        val points = listOf(
//            Pair(0.0f, 0.85f),
//            Pair(0.1f, 0.15f),
//            Pair(0.25f, 0.65f),
//            Pair(0.35f, 0.45f),
//            Pair(0.55f, 0.55f),
//            Pair(0.7f, 0.72f),
//            Pair(0.88f, 0.25f),
//            Pair(1.0f, 0.6f)
//        )
//        val w = size.width
//        val h = size.height

//        // Draw y axis lines (faded)
//        for (i in 1..8) {
//            val y = h * i / 9f
//            drawLine(
//                color = Color(0xFFB1D7FE).copy(alpha = 0.19f),
//                start = androidx.compose.ui.geometry.Offset(0f, y),
//                end = androidx.compose.ui.geometry.Offset(w, y),
//                strokeWidth = 2f
//            )
//        }
//        // Draw curve
//        for (i in 0 until points.size - 1) {
//            val (x0, y0) = points[i]
//            val (x1, y1) = points[i + 1]
//            drawLine(
//                color = Color(0xFF3983E8),
//                start = androidx.compose.ui.geometry.Offset(w * x0, h * y0),
//                end = androidx.compose.ui.geometry.Offset(w * x1, h * y1),
//                strokeWidth = 4f
//            )
//        }
//        // Draw points
//        for ((x, y) in points) {
//            drawCircle(
//                color = Color(0xFF3983E8),
//                radius = 7f,
//                center = androidx.compose.ui.geometry.Offset(w * x, h * y)
//            )
//        }
//    }
}
