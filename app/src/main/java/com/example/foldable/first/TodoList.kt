package com.example.foldable.first

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun TodoList(navController: NavController, viewModel: TodoViewModel) {
    val tasks by viewModel.tasks.collectAsState(emptyList())
    val redirectToAdd by viewModel.redirectToAdd.collectAsState()
    val redirectToEdit by viewModel.redirectToEdit.collectAsState()

    LaunchedEffect(key1 = redirectToAdd) {
        if (redirectToAdd) {
            navController.navigate("/add")
            viewModel.redirectToAdd.value = false
        }
    }

    LaunchedEffect(key1 = redirectToEdit) {
        redirectToEdit?.let {
            navController.navigate("/edit/${it.id}")
            viewModel.redirectToEdit.value = null
        }
    }

    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAddClick() },
                modifier = Modifier.testTag(
                    TEST_TAG_ADD_BUTTON
                )
            ) {
                Icon(Icons.Filled.Add, "")
            }
        }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "TODOs", fontSize = 22.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            TaskView(
                tasks = tasks,
                onClick = {
                    viewModel.onEditClick(it)
                },
                onComplete = { task, complete ->
                    viewModel.onComplete(task, complete)
                })
        }
    }
}

const val TEST_TAG_ADD_BUTTON = "add_button"
const val TEST_TAG_CHECKBOX_BUTTON = "checkbox"

@Composable
fun TaskView(
    tasks: List<Task>,
    onComplete: (task: Task, complete: Boolean) -> Unit,
    onClick: (task: Task) -> Unit
) {
    tasks.forEach {
        Row(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                onClick(it)
            }
        ) {
            val checkedState = remember { mutableStateOf(it.isComplete) }
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = { complete ->
                    checkedState.value = complete
                    onComplete(it, complete)
                },
                modifier = Modifier.testTag(TEST_TAG_CHECKBOX_BUTTON + it.id)
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = it.title,
                fontSize = 16.sp,
            )
        }
    }
}

class TodoViewModel(
    private val taskRepository: TaskRepository = TaskRepositoryImpl.INSTANCE
) : ViewModel() {

    val redirectToAdd = MutableStateFlow(false)
    val redirectToEdit = MutableStateFlow<Task?>(null)

    val tasks = taskRepository.getTasks()

    fun onAddClick() = viewModelScope.launch {
        redirectToAdd.value = true
    }

    fun onEditClick(task: Task) = viewModelScope.launch {
        redirectToEdit.value = task
    }

    fun onComplete(task: Task, complete: Boolean) = viewModelScope.launch {
        taskRepository.updateTask(task.copy(isComplete = complete))
    }
}
