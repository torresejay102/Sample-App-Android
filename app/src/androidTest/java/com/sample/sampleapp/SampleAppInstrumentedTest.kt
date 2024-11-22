package com.sample.sampleapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule
import java.util.Timer
import java.util.TimerTask

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testInsert() {
        composeTestRule.setContent {
            MainView()
        }

        checkButtonClick("Button 1")

        composeTestRule.onNodeWithText("Logs").run {
            performClick()
        }

        checkList("Button 1")
    }

    private fun checkButtonClick(text: String) {
        composeTestRule.waitUntilTimeout(1000)
        composeTestRule.onNodeWithText(text).run {
            performClick()
        }
        composeTestRule.waitUntilTimeout(1000)
        val snackbar = composeTestRule.onNodeWithText("$text clicked")
        snackbar.assertIsDisplayed()
    }

    private fun checkList(text: String) {
        val text = composeTestRule.onNodeWithText("$text pressed")
        text.assertIsDisplayed()
    }
}

fun ComposeContentTestRule.waitUntilTimeout(
    timeoutMillis: Long
) {
    AsyncTimer.start(timeoutMillis)
    this.waitUntil(
        condition = { AsyncTimer.expired },
        timeoutMillis = timeoutMillis + 1000
    )
}

object AsyncTimer {
    var expired = false
    fun start(delay: Long = 1000) {
        expired = false
        val timerTask = TimerTaskImpl {
            expired = true
        }
        Timer().schedule(timerTask, delay)
    }
}

class TimerTaskImpl(private val runnable: Runnable) : TimerTask() {

    override fun run() {
        runnable.run()
    }
}