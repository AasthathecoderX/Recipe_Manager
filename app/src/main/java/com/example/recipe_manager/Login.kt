package com.example.recipe_manager

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipe_manager.util.PreferencesHelper
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    auth: FirebaseAuth? = null,
    onLoginSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    LaunchedEffect(email) {
        if (email.isNotBlank()) {
            val savedPw = PreferencesHelper.getPassword(sharedPref, email.trim())
            if (savedPw != null) {
                password = savedPw
                rememberMe = true
            } else {
                password = ""
                rememberMe = false
            }
        } else {
            password = ""
            rememberMe = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.93f)
                .fillMaxHeight(0.97f)
                .align(Alignment.Center)
                .background(Color(0xFF7e1111), shape = RoundedCornerShape(20.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .fillMaxHeight(0.97f)
                    .align(Alignment.Center)
                    .background(Color(0xFFFCFCFC), shape = RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFFbb6161), shape = RoundedCornerShape(16.dp))
                    // moved content slightly up
                    .padding(horizontal = 16.dp, vertical = 50.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Text(
                        text = "Login",
                        color = Color(0xFF7e1111),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Image(
                        painter = painterResource(R.drawable.book),
                        contentDescription = "Recipe Book",
                        modifier = Modifier
                            .width(200.dp)
                            .height(300.dp)
                            .padding(vertical = 10.dp)
                    )

                    // Email field
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFFbb6161),
                            unfocusedContainerColor = Color(0xFFbb6161),
                            disabledContainerColor = Color(0xFFbb6161),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            cursorColor = Color.White,
                            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.8f),
                            focusedPlaceholderColor = Color.White.copy(alpha = 0.8f)
                        )
                    )

                    // Password field
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFFbb6161),
                            unfocusedContainerColor = Color(0xFFbb6161),
                            disabledContainerColor = Color(0xFFbb6161),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            cursorColor = Color.White,
                            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.8f),
                            focusedPlaceholderColor = Color.White.copy(alpha = 0.8f)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { checked ->
                                rememberMe = checked
                                if (!checked) {
                                    PreferencesHelper.removeCredentials(
                                        sharedPref,
                                        email.trim()
                                    )
                                }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFbb6161))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Always remember me",
                            color = Color.Black,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                if (auth != null) {
                                    auth.signInWithEmailAndPassword(email.trim(), password)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "Login Successful",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                if (rememberMe) {
                                                    PreferencesHelper.saveCredentials(
                                                        sharedPref,
                                                        email.trim(),
                                                        password
                                                    )
                                                }
                                                onLoginSuccess()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Authentication Failed: ${task.exception?.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "FirebaseAuth instance not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter email and password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7e1111))
                    ) {
                        Text(
                            text = "Login",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )
                    }
                }
            }
        }

        // Decorative images
        Image(
            painter = painterResource(R.drawable.frame),
            contentDescription = "Sparkles",
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopStart)
                .offset(x = 9.dp, y = 0.3.dp),
            colorFilter = ColorFilter.tint(Color(0xAA180101))
        )
        Image(
            painter = painterResource(R.drawable.below),
            contentDescription = "Bottom floral",
            modifier = Modifier
                .width(170.dp)
                .height(140.dp)
                .align(Alignment.BottomStart)
                .offset(x = 1.dp, y = 10.dp)
        )
        Image(
            painter = painterResource(R.drawable.frame1),
            contentDescription = "Grid",
            modifier = Modifier
                .width(55.dp)
                .height(55.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-8).dp, y = (-10).dp),
            colorFilter = ColorFilter.tint(Color(0xAA180101))
        )
        Image(
            painter = painterResource(R.drawable.frame2),
            contentDescription = "Top floral",
            modifier = Modifier
                .width(170.dp)
                .height(140.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-1).dp, y = (-9).dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Login(auth = null)
}
