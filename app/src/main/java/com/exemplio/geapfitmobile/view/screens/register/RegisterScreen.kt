package com.exemplio.geapfitmobile.view.screens.register

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exemplio.geapfitmobile.R
import com.exemplio.geapfitmobile.view.core.components.CustomButtonSecondary
import com.exemplio.geapfitmobile.view.core.components.GeapButton
import com.exemplio.geapfitmobile.view.core.components.CustomText
import com.exemplio.geapfitmobile.view.core.components.CustomTextField
import com.exemplio.geapfitmobile.view.core.components.ModalDialogError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(registerViewModel: RegisterViewModel = hiltViewModel(), navigateBack: () -> Unit) {

    val uiState by registerViewModel.uiState.collectAsStateWithLifecycle()
    val showModal = remember { mutableStateOf(false) }
    val modalMessage = remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ), title = {}, navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "back",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable { navigateBack() }
                    )
                })
        }) { padding ->
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CustomText(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.email,
                    label = stringResource(R.string.register_screen_title_name),
                    onValueChange = { registerViewModel.onEmailChanged(it) })
                Spacer(Modifier.height(10.dp))
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.email,
                    label = stringResource(R.string.register_screen_title_lastname),
                    onValueChange = { registerViewModel.onEmailChanged(it) })
                Spacer(Modifier.height(10.dp))
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.email,
                    label = stringResource(R.string.register_screen_textfield_register_email),
                    onValueChange = { registerViewModel.onEmailChanged(it) })
                Spacer(Modifier.height(10.dp))
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.password,
                    label = stringResource(R.string.login_screen_textfield_password),
                    onValueChange = { registerViewModel.onPasswordChanged(it) })
                Spacer(Modifier.height(10.dp))
                GeapButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.register_screen_title_email),
                    onClick = { registerViewModel.onClickSelected() },
                    enabled = uiState.isLoginEnabled && !uiState.isLoading,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "   O   ",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(onClick = {}) {
                    CustomText(
                        text = stringResource(R.string.login_screen_button_login),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        if (showModal.value) {
            ModalDialogError(
                message = modalMessage.value,
                onDismiss = {
                    showModal.value = false;
                    modalMessage.value = ""
                    uiState.errorCode = null
                    uiState.errorMessage = null
                }
            )
        }
    }
}