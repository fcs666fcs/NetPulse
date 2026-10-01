
package com.example.netpulse

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import androidx.activity.ComponentActivity

class ComposeSmokeTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun appRenders() {
        rule.activity.setContent { com.example.netpulse.ui.NetPulseApp() }
        rule.onNodeWithText("NetPulse").assertExists()
    }
}
