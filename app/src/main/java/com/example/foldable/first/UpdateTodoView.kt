package com.example.foldable.first

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun UpdateTodoView(navController: NavController, viewModel: UpdateTodoViewModel) {

    val title by viewModel.titleText.collectAsState()
    val dismiss by viewModel.dismissView.collectAsState()

    if (dismiss) {
        navController.popBackStack()
        viewModel.reset()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Update Todo", fontSize = 22.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        TextField(
            value = title,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .testTag(TEST_TAG_ADD_TEXT_FIELD),
            onValueChange = {
                viewModel.titleText.value = it
            })

        Button(
            onClick = { viewModel.onConfirm() },
            modifier = Modifier.padding(vertical = 16.dp),
        ) {
            Text(text = "Update")
        }
    }
}

class UpdateTodoViewModel(
    private val taskRepository: TaskRepository = TaskRepositoryImpl.INSTANCE,
    private val taskId: Int
) : ViewModel() {

    val task: Task? = taskRepository.getTask(taskId)

    val dismissView = MutableStateFlow(false)
    var titleText = MutableStateFlow(task?.title ?: "")

    fun onConfirm() {
        if (titleText.value.isEmpty()) return
        val task = task ?: return

        val updated = task.copy(title = titleText.value)
        taskRepository.updateTask(updated)
        dismissView.value = true
    }

    fun reset() {
        dismissView.value = false
        titleText.value = ""
    }

}