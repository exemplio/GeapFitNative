package com.exemplio.geapfitmobile.view.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exemplio.geapfitmobile.R
import com.exemplio.geapfitmobile.view.auth.login.LoginViewModel
import com.exemplio.geapfitmobile.view.core.components.GeapButton
import com.exemplio.geapfitmobile.view.core.components.CustomButtonSecondary
import com.exemplio.geapfitmobile.view.core.components.CustomText
import com.exemplio.geapfitmobile.view.core.components.CustomTextField
import com.exemplio.geapfitmobile.view.core.components.ModalDialogError

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    navigateToRegister: () -> Unit,
    navigateToHome: () -> Unit
) {
    val uiState by loginViewModel.uiState.collectAsStateWithLifecycle()

    val showModal = remember { mutableStateOf(false) }
    val modalMessage = remember { mutableStateOf("") }

    LaunchedEffect(uiState.errorCode, uiState.errorMessage) {
        if (uiState.errorCode != null && uiState.errorCode != 200) {
            modalMessage.value = uiState.errorMessage ?: "Error para conectar con el servidor"
            showModal.value = true
        }
    }

    LaunchedEffect(uiState.isUserLogged) {
        if (uiState.isUserLogged) {
            navigateToHome()
        }else{
            loginViewModel.check()
        }
    }

    Scaffold { padding ->
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
                    text = "Iniciar sesión",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.email,
                    label = stringResource(R.string.login_screen_textfield_email),
                    onValueChange = { loginViewModel.onEmailChanged(it) })
                Spacer(Modifier.height(10.dp))
                CustomTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.password,
                    label = stringResource(R.string.login_screen_textfield_password),
                    onValueChange = { loginViewModel.onPasswordChanged(it) })
                Spacer(Modifier.height(10.dp))
                GeapButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.login_screen_button_login),
                    onClick = { loginViewModel.onClickSelected() },
                    enabled = uiState.isLoginEnabled && !uiState.isLoading,
                )
                TextButton(onClick = {}) {
                    CustomText(
                        text = stringResource(R.string.login_screen_text_forgot_password),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
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
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_google), // Add your Google icon
                            contentDescription = "Google Sign In",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(18.dp))
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_apple), // Add your Apple icon
                            contentDescription = "Apple Sign In",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                CustomButtonSecondary(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { navigateToRegister() },
                    title = stringResource(R.string.login_screen_button_register)
                )
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

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(navigateToRegister = {}, navigateToHome = {})
}