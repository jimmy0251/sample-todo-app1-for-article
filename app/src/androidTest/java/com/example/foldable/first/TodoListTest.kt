package com.example.foldable.first

import androidx.activity.viewModels
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TodoListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController
    private val viewModel = TodoViewModel()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "/home",
            ) {
                composable("/home") {
                    TodoList(navController, viewModel)
                }
                composable("/add") {
                    AddTodoView(navController, AddTodoViewModel(TaskRepositoryImpl()))
                }
                composable("/edit/{taskId}") {
                    val taskId = (it.arguments?.getString("taskId", "") ?: "").toInt()
                    val viewModel = UpdateTodoViewModel(taskId = taskId)
                    UpdateTodoView(navController, viewModel)
                }
            }
        }
        composeTestRule.waitForIdle()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testScreenTitleIsShown() {
        composeTestRule.onNodeWithText("TODOs").assertIsDisplayed()
    }

    @Test
    fun testTaskItemsAreShown() {
        composeTestRule.onNodeWithText("Hello TDD").assertIsDisplayed()
        composeTestRule.onNodeWithText("This is fun").assertIsDisplayed()
        composeTestRule.onNodeWithText("I can not live without TDD").assertIsDisplayed()
    }

    @Test
    fun testAddButtonIsShown() {
        composeTestRule.onNodeWithTag(TEST_TAG_ADD_BUTTON).assertIsDisplayed()
    }

    @Test
    fun testAddButtonRedirectsToAddTodoView() {
        composeTestRule.onNodeWithTag(TEST_TAG_ADD_BUTTON).performClick()
        composeTestRule.waitForIdle()
        assertEquals("/add", navController.currentBackStackEntry?.destination?.route)
        composeTestRule.onNodeWithText("Add Todo").assertIsDisplayed()
    }

    @Test
    fun testClickingOnItemRedirectsToUpdateTodoView() {
        val tasks: List<Task>
        runBlocking(Dispatchers.IO) {
            tasks = TaskRepositoryImpl.INSTANCE.getTasks().first()
        }

        val task = tasks[2]
        composeTestRule.onNodeWithText(task.title).performClick()
        composeTestRule.waitForIdle()

        assertEquals("/edit/{taskId}", navController.currentBackStackEntry?.destination?.route)
        composeTestRule.onNodeWithText("Update Todo").assertIsDisplayed()
        composeTestRule.onNodeWithText(task.title).assertIsDisplayed()
    }

    @Test
    fun testClickingOnCheckboxCompletesTodo() {
        val tasks: List<Task>
        runBlocking(Dispatchers.IO) {
            tasks = TaskRepositoryImpl.INSTANCE.getTasks().first()
        }

        val task = tasks[1]

        composeTestRule.onNodeWithTag(TEST_TAG_CHECKBOX_BUTTON + task.id)
            .assertIsOff()
            .performClick()

        composeTestRule.onNodeWithTag(TEST_TAG_CHECKBOX_BUTTON + task.id)
            .assertIsOn()
    }
}