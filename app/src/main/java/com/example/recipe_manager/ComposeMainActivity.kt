package com.example.recipe_manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ComposeMainActivity : ComponentActivity() {

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "frontpage") {
                composable("frontpage") {
                    Frontpage(navController = navController)
                }
                composable("login") {
                    Login(
                        auth = auth,
                        onLoginSuccess = {
                            navController.navigate("folders") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
                composable("folders") {
                    Folders(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController
                    )
                }
                composable("folder_content/{folderName}") { backStackEntry ->
                    val folderName = backStackEntry.arguments?.getString("folderName") ?: ""
                    FolderContent(
                        folderName = folderName,
                        modifier = Modifier.fillMaxSize(),
                        navController = navController
                    )
                }
                // now pass BOTH folderName and recipeTitle
                composable("new_recipe/{folderName}/{recipeTitle}") { backStackEntry ->
                    val folderName = backStackEntry.arguments?.getString("folderName") ?: "Folder"
                    val recipeTitle =
                        backStackEntry.arguments?.getString("recipeTitle") ?: "New Recipe"
                    NewRecipe(
                        modifier = Modifier.fillMaxSize(),
                        recipeTitle = recipeTitle,
                        folderName = folderName
                    )
                }
            }
        }
    }
}
