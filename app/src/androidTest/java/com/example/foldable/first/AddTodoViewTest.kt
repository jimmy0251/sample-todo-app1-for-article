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
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddTodoViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController

    private fun init(startDestination: String = "/add") {
        composeTestRule.setContent {
            navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = startDestination,
            ) {
                composable("/home") {
                    TodoList(navController, TodoViewModel())
                }
                composable("/add") {
                    AddTodoView(navController, AddTodoViewModel())
                }
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun testScreenViewsAreShown() {
        init()
        composeTestRule.onNodeWithText("Add Todo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add").assertIsDisplayed()
        composeTestRule.onNodeWithTag(TEST_TAG_ADD_TEXT_FIELD).assertIsDisplayed()
    }

    @Test
    fun testAddButtonAddsTheItem() {
        init(startDestination = "/home")
        composeTestRule.onNodeWithTag(TEST_TAG_ADD_BUTTON).performClick()

        composeTestRule.onNodeWithTag(TEST_TAG_ADD_TEXT_FIELD)
            .performTextInput("this is a new task")

        composeTestRule.onNodeWithText("Add").performClick()

        // Verify we are on list screen and new task is shown
        composeTestRule.onNodeWithText("TODOs").assertIsDisplayed()
        composeTestRule.onNodeWithText("this is a new task").assertIsDisplayed()
    }
}