// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the pictures of the app for the README, taken in an emulator against a demo monitor (invented data) started by the workflow.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
//
// The workflow android-screenshots.yml starts tools/contract/demo_server.py --team on the runner; the emulator reaches it on 10.0.2.2. Without the instrumentation argument
// `monitor` the test is skipped. The pictures are made with `screencap` in /data/local/tmp/shots, where the workflow pulls them with adb. Nothing here touches a phone.
package es.electrohobby3d.ghamonitor

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScreenshotsTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val monitor: String? = InstrumentationRegistry.getArguments().getString("monitor")

    private fun shell(command: String) {
        android.os.ParcelFileDescriptor.AutoCloseInputStream(instrumentation.uiAutomation.executeShellCommand(command)).use { it.readBytes() }   // read to the end: the command has finished then
    }

    private fun shot(name: String) {
        rule.waitForIdle()
        Thread.sleep(2_000)   // the answers arrive and the animations end
        rule.waitForIdle()
        shell("mkdir -p /data/local/tmp/shots")
        shell("screencap -p /data/local/tmp/shots/$name.png")
    }

    private fun present(tag: String): Boolean = rule.onAllNodes(hasTestTag(tag)).fetchSemanticsNodes().isNotEmpty()

    @Test
    fun connectingAndTheFiveScreens() {
        assumeTrue("no monitor was given", monitor != null)
        rule.onNodeWithTag("tab-settings").performClick()
        rule.onNodeWithTag("settings-url").performTextInput(monitor!!)
        rule.onNodeWithTag("settings-token").performTextInput("demo-access-0123456789")
        Espresso.closeSoftKeyboard()
        rule.onNodeWithTag("settings-save").performClick()
        rule.waitUntil(timeoutMillis = 60_000) { present("settings-user") }
        rule.onNodeWithTag("settings-user").performScrollTo().performTextInput("demo-admin")
        rule.onNodeWithTag("settings-password").performScrollTo().performTextInput("demo-passphrase-2468")
        Espresso.closeSoftKeyboard()
        shot("sign-in")
        rule.onNodeWithTag("settings-signin").performScrollTo().performClick()
        rule.waitUntil(timeoutMillis = 60_000) { present("connected") }
        shot("settings")
        for (name in listOf("summary", "alerts", "repos", "proposals")) {
            rule.onNodeWithTag("tab-$name").performClick()
            Thread.sleep(1_000)   // the screen asks the monitor
            rule.waitUntil(timeoutMillis = 90_000) { !present("loading") }
            shot(name)
            check(!present("error")) { "the screen $name shows an error" }
        }
    }
}
