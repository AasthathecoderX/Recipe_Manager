package com.example.recipe_manager

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@Composable
fun FolderContent(
    folderName: String = "Folder",
    recipesInput: List<String> = emptyList(),
    onDelete: (String) -> Unit = {},
    modifier: Modifier,
    navController: NavHostController
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    if (currentUser == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Please log in first",
                color = Color(0xFF7e1111),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        return
    }

    val database = FirebaseDatabase
        .getInstance("https://recipe-manager-91957-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .reference
    val recipesRef = database
        .child("users")
        .child(currentUser.uid)
        .child("folders")
        .child(folderName)
        .child("recipes")

    var recipes by remember { mutableStateOf(recipesInput.toMutableList()) }
    var renameIndex by remember { mutableStateOf(-1) }
    var renameText by remember { mutableStateOf("") }

    // Listener: each child key under "recipes" is the recipe title (Palak, etc.)
    DisposableEffect(folderName) {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<String>()
                for (child in snapshot.children) {
                    child.key?.let { list.add(it) }
                }
                recipes = list.toMutableList()
            }

            override fun onCancelled(error: DatabaseError) { }
        }
        recipesRef.addValueEventListener(listener)
        onDispose { recipesRef.removeEventListener(listener) }
    }

    Box(
        modifier = modifier.background(Color.White)
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
                    .padding(horizontal = 18.dp, vertical = 56.dp)
            ) {
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = folderName,
                        color = Color(0xFF7e1111),
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        recipes.forEachIndexed { index, title ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .background(
                                        Color(0xfff7c4c1),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        1.dp,
                                        Color(0xFFbb6161),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onTap = {
                                                val safeTitle = recipes[index]
                                                navController.navigate("new_recipe/$folderName/$safeTitle")
                                            },
                                            onDoubleTap = {
                                                renameIndex = index
                                                renameText = recipes[index]
                                            }
                                        )
                                    },
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 15.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    if (renameIndex == index) {
                                        TextField(
                                            value = renameText,
                                            onValueChange = { renameText = it },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                    } else {
                                        Text(
                                            text = title,
                                            color = Color.Black,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Image(
                                        painter = painterResource(id = R.drawable.ic_delete),
                                        contentDescription = "Delete",
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable {
                                                val toDelete = recipes[index]
                                                onDelete(toDelete)

                                                // node path is recipes/{title}
                                                recipesRef.child(toDelete).removeValue()

                                                recipes = recipes.toMutableList().also {
                                                    it.removeAt(index)
                                                }
                                                if (renameIndex == index) {
                                                    renameIndex = -1
                                                } else if (renameIndex > index) {
                                                    renameIndex -= 1
                                                }
                                            }
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(170.dp)
                                .height(58.dp)
                                .background(
                                    Color(0xFF7e1111),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    if (renameIndex != -1 && renameText.isNotBlank()) {
                                        val oldTitle = recipes[renameIndex]
                                        val newTitle = renameText

                                        if (oldTitle != newTitle) {
                                            // move node: recipes/oldTitle -> recipes/newTitle
                                            recipesRef.child(oldTitle).get()
                                                .addOnSuccessListener { snapshot ->
                                                    val data = snapshot.value
                                                    val updates = mapOf(
                                                        oldTitle to null,
                                                        newTitle to data
                                                    )
                                                    recipesRef.updateChildren(updates)
                                                }
                                        }

                                        recipes = recipes.toMutableList().also {
                                            it[renameIndex] = newTitle
                                        }
                                        renameIndex = -1
                                    }
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

                        Spacer(modifier = Modifier.width(15.dp))

                        Image(
                            painter = painterResource(id = R.drawable.plus),
                            contentDescription = "Plus",
                            modifier = Modifier
                                .size(68.dp)
                                .clickable {
                                    val baseTitle = "Recipe Title"
                                    var finalTitle = baseTitle
                                    var counter = 1
                                    while (recipes.contains(finalTitle)) {
                                        finalTitle = "$baseTitle $counter"
                                        counter++
                                    }

                                    // create object node at recipes/{finalTitle}
                                    val recipeNode = recipesRef.child(finalTitle)
                                    val data = mapOf(
                                        "title" to finalTitle,
                                        "ingredients" to "",
                                        "steps" to ""
                                    )
                                    recipeNode.setValue(data).addOnSuccessListener {
                                        renameText = finalTitle
                                        renameIndex = recipes.size
                                    }
                                }
                        )
                    }
                }
            }
        }

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
                .height(130.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-1).dp, y = (-9).dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
fun Folder_contentPreview() {
    val navController = rememberNavController()
    FolderContent(
        folderName = "Folder",
        modifier = Modifier.fillMaxSize(),
        navController = navController
    )
}
