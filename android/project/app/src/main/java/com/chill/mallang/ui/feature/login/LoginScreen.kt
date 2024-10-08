package com.chill.mallang.ui.feature.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.chill.mallang.R
import com.chill.mallang.ui.theme.Gray4
import com.chill.mallang.ui.theme.Gray6
import com.chill.mallang.ui.theme.Typography
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun LoginScreen(
    onLoginSuccess: (String, String) -> Unit,
    onAuthLoginSuccess: () -> Unit,
    onFirstLaunched: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.loginUiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.game_splash_background))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )

    LaunchedEffect(Unit) {
        viewModel.initCredentialManager(context)
        viewModel.initCredentialRequest()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetUi()
        }
    }

    // Credential Manager 방식
    val credentialLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    viewModel.getCredential(context)
                }
            }
        }

    // Google Sign-In 방식
    val googleSignInLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            viewModel.handleActivityResult(LoginViewModel.RC_SIGN_IN, result.data)
        }

    LaunchedEffect(googleSignInLauncher) {
        viewModel.setGoogleSignInLauncher(googleSignInLauncher)
    }

    HandleLoginEvent(
        loginUiState = uiState,
        authLogin = onAuthLoginSuccess,
        loginSuccess = { userEmail, userProfileImageUrl ->
            onLoginSuccess(userEmail, userProfileImageUrl)
        },
        loginUiEvent = viewModel.uiEvent,
        showSnackBar = { errorMessage ->
            coroutineScope.launch {
                snackBarHostState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Short,
                )
            }
        },
        firstLaunched = onFirstLaunched,
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
        )
        Column(
            modifier =
                Modifier
                    .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.14f))
            Image(
                painter = painterResource(id = R.drawable.ic_title),
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxWidth(),
            )
            Spacer(modifier = Modifier.weight(1f))
            GoogleLoginButton(onClick = {
                viewModel.initializeLogin(context, credentialLauncher)
            }, text = stringResource(R.string.login_button_message))
            Spacer(modifier = Modifier.fillMaxHeight(0.35f))
        }
    }
}

@Composable
fun GoogleLoginButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
) {
    Button(
        onClick = onClick,
        colors =
            ButtonDefaults.buttonColors(
                contentColor = Gray6,
                containerColor = Color.White,
            ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(2.dp, Gray4),
        modifier =
            modifier
                .fillMaxWidth(0.8f)
                .height(48.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = null,
                modifier = Modifier.width(28.dp),
            )
            Box(modifier = Modifier.width(10.dp))
            Text(
                text,
                style = Typography.displayLarge,
            )
        }
    }
}

@Composable
fun HandleLoginEvent(
    loginUiState: LoginUiState,
    loginUiEvent: SharedFlow<LoginUiEvent>,
    authLogin: () -> Unit,
    loginSuccess: (String, String) -> Unit,
    showSnackBar: (String) -> Unit,
    firstLaunched: () -> Unit,
) {
    LaunchedEffect(loginUiEvent) {
        loginUiEvent.collectLatest { event ->
            when (event) {
                LoginUiEvent.AuthLogin -> {
                    authLogin()
                }
            }
        }
    }
    LaunchedEffect(loginUiState) {
        when (loginUiState) {
            is LoginUiState.Success -> {
                loginSuccess(
                    loginUiState.userEmail ?: "",
                    URLEncoder.encode(
                        loginUiState.userProfileImageUrl.toString(),
                        StandardCharsets.UTF_8.toString(),
                    ),
                )
            }

            LoginUiState.Loading -> {}

            is LoginUiState.Error -> {
                showSnackBar(loginUiState.errorMessage)
            }

            LoginUiState.FirstLaunched -> firstLaunched()
        }
    }
}
