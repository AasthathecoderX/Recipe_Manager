package com.example.recipe_manager

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun NewRecipe(
    modifier: Modifier = Modifier,
    recipeTitle: String = "New Recipe",
    folderName: String = "Folder"
) {
    var ingredientsText by remember { mutableStateOf("") }
    var stepsText by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val recipeRef = remember(currentUser, folderName, recipeTitle) {
        if (currentUser != null) {
            FirebaseDatabase
                .getInstance("https://YOUR-PROJECT-ID-default-rtdb.asia-southeast1.firebasedatabase.app/") # Replace with your database URL
                .reference
                .child("users")
                .child(currentUser.uid)
                .child("folders")
                .child(folderName)
                .child("recipes")
                .child(recipeTitle)   // node key = recipe title
        } else {
            null
        }
    }

    // Load existing data
    LaunchedEffect(recipeRef) {
        recipeRef?.get()?.addOnSuccessListener { snap ->
            val ingredients = snap.child("ingredients").getValue(String::class.java)
            val steps = snap.child("steps").getValue(String::class.java)
            if (ingredients != null) ingredientsText = ingredients
            if (steps != null) stepsText = steps
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.97f)
                .align(Alignment.Center)
                .background(Color(0xFF7e1111), shape = RoundedCornerShape(20.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .fillMaxHeight(0.95f)
                    .align(Alignment.Center)
                    .background(Color.White, shape = RoundedCornerShape(14.dp))
                    .border(2.dp, Color(0xFFbb6161), shape = RoundedCornerShape(14.dp))
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = recipeTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = Color(0xFF7e1111),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 18.dp)
                    )

                    Text(
                        text = "Recipe Title",
                        fontSize = 18.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(Color(0xFFF7C4C1), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = recipeTitle,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Ingredients",
                        fontSize = 18.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFFF7C4C1), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        BasicTextField(
                            value = ingredientsText,
                            onValueChange = { ingredientsText = it },
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Steps",
                        fontSize = 18.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color(0xFFF7C4C1), shape = RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        BasicTextField(
                            value = stepsText,
                            onValueChange = { stepsText = it },
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Box(
                        modifier = Modifier
                            .width(170.dp)
                            .height(61.dp)
                            .background(Color(0xFF7e1111), shape = RoundedCornerShape(8.dp))
                            .clickable(enabled = recipeRef != null) {
                                val data = mapOf(
                                    "title" to recipeTitle,
                                    "ingredients" to ingredientsText,
                                    "steps" to stepsText
                                )
                                recipeRef?.setValue(data)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Save",
                            color = Color.White,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Image(
            painter = painterResource(R.drawable.frame),
            contentDescription = "Sparkles",
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.TopStart)
                .offset(x = 9.dp, y = 0.3.dp),
            colorFilter = ColorFilter.tint(Color(0xAA180101))
        )
        Image(
            painter = painterResource(R.drawable.below),
            contentDescription = "Bottom floral",
            modifier = Modifier
                .width(150.dp)
                .height(120.dp)
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
                .offset(x = (-8).dp, y = (-10).dp)
        )
        Image(
            painter = painterResource(R.drawable.frame2),
            contentDescription = "Top floral",
            modifier = Modifier
                .width(127.dp)
                .height(100.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-1).dp, y = (-9).dp)
        )
    }
}

@Preview(widthDp = 360, heightDp = 700)
@Composable
fun NewRecipePreview() {
    NewRecipe(recipeTitle = "Sample Recipe")
}
