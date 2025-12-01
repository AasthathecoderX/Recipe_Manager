package com.example.recipe_manager

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun Frontpage(modifier: Modifier = Modifier, navController: NavController) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable {
                navController.navigate("login") {
                    popUpTo("frontpage") { inclusive = true }
                }
            }
    ) {
        // Background maroon box
        Box(
            modifier = Modifier
                .fillMaxWidth(0.97f)
                .fillMaxHeight(0.97f)
                .align(Alignment.Center)
                .background(Color(0xFF7e1111), shape = RoundedCornerShape(12.dp))
                .border(2.dp, Color(0xFFbb6161), shape = RoundedCornerShape(12.dp))
                .padding(36.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.rectangle),
                    contentDescription = "Cookbook illustration",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(550.dp)
                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                        .border(2.dp, Color(0xFFbb6161), shape = RoundedCornerShape(12.dp))
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

        // Decorative frames overlaying on top of all layers
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
fun FrontpagePreview() {
    Frontpage(navController = androidx.navigation.compose.rememberNavController())
}
