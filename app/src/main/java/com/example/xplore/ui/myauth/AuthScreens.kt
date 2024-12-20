package com.example.xplore.ui.myauth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.xplore.R

@Composable
fun SignInScreen(
    onAction: (SigninScreenActions) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyAuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerpad ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerpad)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                //TODO: ADD logo

                // Email TextField
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password TextField
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sign In Button
                Button(
                    onClick = {
                        viewModel.login(email, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                ) {
                    Text("Sign In", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Forgot Password?",
                    modifier = Modifier.clickable { /* Handle forgot password */ }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row {
                    Text("Don't have an account? ")
                    Text(
                        text = "Sign Up",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onAction(SigninScreenActions.SignUp) }
                    )
                }

                LaunchedEffect(loginState) {
                    when (loginState) {
                        is AuthState.Error -> {
                            val errorMessage = (loginState as AuthState.Error).message
                            snackbarHostState.showSnackbar(
                                message = errorMessage,
                                actionLabel = "OK",
                                duration = SnackbarDuration.Long
                            )
                        }

                        is AuthState.Success -> {
                            onAction(SigninScreenActions.SignIn)
                        }

                        else -> {} // Handle other states
                    }
                }
            }
        }
    }
}

sealed class SigninScreenActions {
    object SignIn : SigninScreenActions()
    object SignUp : SigninScreenActions()
    object ForgotPassword : SigninScreenActions()
}

@Composable
fun SignUpScreen(
    onAction: (SignupScreenActions) -> Unit,
    modifier: Modifier=Modifier,
    viewModel: MyAuthViewModel= hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val signupState by viewModel.signupState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { inner ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(inner)
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // TODO: add Logo

                // Username TextField
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email TextField
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password TextField
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Sign Up Button
                Button(
                    onClick = {
                        viewModel.register(username, email, password);
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                ) {
                    Text("Sign Up", color = Color.White)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Sign In Option
                Row {
                    Text("Already have an account? ")
                    Text(
                        text = "Sign In",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onAction(SignupScreenActions.SignIn) }
                    )
                }

                LaunchedEffect(signupState) {
                    when (signupState) {
                        is AuthState.Error -> {
                            val errorMessage = (signupState as AuthState.Error).message
                            snackbarHostState.showSnackbar(
                                message = errorMessage,
                                actionLabel = "OK",
                                duration = SnackbarDuration.Long
                            )
                        }

                        is AuthState.Success -> {
                            onAction(SignupScreenActions.SignUp)
                        }

                        else -> {} // Handle other states
                    }
                }
            }
        }
    }
}

sealed class SignupScreenActions {
    object SignIn : SignupScreenActions()
    object SignUp : SignupScreenActions()
}