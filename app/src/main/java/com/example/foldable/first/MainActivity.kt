package com.example.foldable.first

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foldable.first.ui.theme.TodoTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TodoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "/home",
                    ) {
                        composable("/home") {
                            val viewModel by viewModels<TodoViewModel>()
                            TodoList(navController, viewModel)
                        }
                        composable("/add") {
                            AddTodoView(navController, AddTodoViewModel())
                        }
                        composable("/edit/{taskId}") {
                            val taskId = (it.arguments?.getString("taskId", "") ?: "").toInt()
                            val viewModel = UpdateTodoViewModel(taskId = taskId)
                            UpdateTodoView(navController, viewModel)
                        }
                    }
                }
            }
        }
    }
}