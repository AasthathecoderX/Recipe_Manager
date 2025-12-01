package com.example.recipe_manager

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Folders(
    modifier: Modifier = Modifier,
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

    var folderList by remember { mutableStateOf(listOf<String>()) }
    var searchText by remember { mutableStateOf("") }
    var renameIndex by remember { mutableStateOf(-1) }
    var renameText by remember { mutableStateOf("") }
    var selectedFolderIndex by remember { mutableStateOf(-1) }

    val database = FirebaseDatabase
        .getInstance("https://recipe-manager-91957-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .reference
    val userFoldersRef = database.child("users").child(currentUser.uid).child("folders")

    DisposableEffect(Unit) {
        val valueListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val folders = snapshot.children.mapNotNull { it.key }
                folderList = folders
            }

            override fun onCancelled(error: DatabaseError) { }
        }

        userFoldersRef.addValueEventListener(valueListener)

        onDispose {
            userFoldersRef.removeEventListener(valueListener)
        }
    }

    val filteredFolders = folderList.filter { it.contains(searchText, ignoreCase = true) }
    val columns = if (filteredFolders.size > 6) 3 else 2

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
                    .fillMaxHeight(0.92f)
                    .align(Alignment.Center)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFFbb6161), shape = RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 32.dp, horizontal = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Folders",
                        color = Color(0xFF7e1111),
                        fontSize = 32.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5AEAE), shape = RoundedCornerShape(7.dp))
                            .height(56.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        TextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            placeholder = {
                                Text(
                                    text = "Search Here",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontStyle = FontStyle.Italic
                                    )
                                )
                            },
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp)
                                .background(Color.Transparent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search icon",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { searchText = "" }
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp, max = 500.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        itemsIndexed(
                            items = filteredFolders,
                            key = { _, name -> name }
                        ) { index, folderName ->
                            val actualIndex = index
                            Box(contentAlignment = Alignment.TopEnd) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .pointerInput(folderName) {
                                            detectTapGestures(
                                                onTap = {
                                                    Log.d("Folders", "Opening folder = $folderName")
                                                    navController.navigate(
                                                        "folder_content/$folderName"
                                                    )
                                                    selectedFolderIndex = -1
                                                    renameIndex = -1
                                                },
                                                onDoubleTap = {
                                                    // enter rename mode
                                                    renameIndex = actualIndex
                                                    renameText = folderName
                                                    selectedFolderIndex = -1
                                                },
                                                onLongPress = {
                                                    // show delete icon
                                                    selectedFolderIndex = actualIndex
                                                }
                                            )
                                        }
                                        .padding(8.dp)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.folder),
                                        contentDescription = "Folder",
                                        modifier = Modifier.size(72.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    if (renameIndex == actualIndex) {
                                        TextField(
                                            value = renameText,
                                            onValueChange = { renameText = it },
                                            singleLine = true,
                                            modifier = Modifier.width(110.dp)
                                        )
                                    } else {
                                        Text(
                                            text = folderName,
                                            color = Color.Black,
                                            fontSize = 18.sp
                                        )
                                    }
                                }

                                if (selectedFolderIndex == actualIndex) {
                                    IconButton(
                                        onClick = {
                                            userFoldersRef.child(folderName).removeValue()
                                            selectedFolderIndex = -1
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Folder",
                                            tint = Color.Red
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .width(170.dp)
                                .height(58.dp)
                                .background(Color(0xFF7e1111), shape = RoundedCornerShape(8.dp))
                                .clickable {
                                    if (renameIndex != -1 && renameText.isNotBlank()) {
                                        val oldName = folderList[renameIndex]
                                        if (oldName != renameText) {
                                            userFoldersRef.child(oldName).get()
                                                .addOnSuccessListener { snapshot ->
                                                    val folderContent = snapshot.value
                                                    val updates = mapOf(
                                                        oldName to null,
                                                        renameText to folderContent
                                                    )
                                                    userFoldersRef.updateChildren(updates)
                                                }
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
                                    val baseName = "New Folder"
                                    var finalName = baseName
                                    var counter = 1
                                    while (folderList.contains(finalName)) {
                                        finalName = "$baseName $counter"
                                        counter++
                                    }

                                    userFoldersRef.child(finalName).setValue(true)
                                        .addOnSuccessListener {
                                            renameText = finalName
                                            renameIndex = folderList.indexOf(finalName)
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
                .offset(x = (-8).dp, y = (-10).dp)
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

@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
fun FoldersPreview() {
    val navController = rememberNavController()
    Folders(
        modifier = Modifier.fillMaxSize(),
        navController = navController
    )
}
