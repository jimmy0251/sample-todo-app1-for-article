package com.example.foldable.first

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.random.Random

data class Task(
    val id: Int,
    val title: String,
    val isComplete: Boolean = false
)

interface TaskRepository {
    fun getTasks(): Flow<List<Task>>

    fun getTask(id: Int): Task?

    fun addTask(title: String)

    fun updateTask(task: Task)

}

class TaskRepositoryImpl : TaskRepository {

    private val tasks = mutableListOf<Task>()
    private val taskListFlow = MutableSharedFlow<List<Task>>(replay = 1)

    init {
        tasks.addAll(
            listOf(
                Task(Random.nextInt(), "Hello TDD"),
                Task(Random.nextInt(), "This is fun"),
                Task(Random.nextInt(), "I can not live without TDD")
            )
        )
        taskListFlow.tryEmit(tasks)
    }

    override fun getTasks(): Flow<List<Task>> {
        return taskListFlow
    }

    override fun getTask(id: Int): Task? {
        return tasks.find { it.id == id }
    }

    override fun addTask(title: String) {
        tasks.add(Task(Random.nextInt(), title))
        taskListFlow.tryEmit(tasks)
    }

    override fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
            taskListFlow.tryEmit(tasks)
        }
    }

    companion object {
        val INSTANCE = TaskRepositoryImpl()
    }
}
