package com.example

import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.captureScreenRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * يشغّل التطبيق الحقيقي (القاعدة المبنية مسبقاً) على Robolectric ويلتقط صور الشاشات
 * إلى app/build/screenshots؛ سير عمل CI ينسخها إلى screenshots/ في الفرع للمراجعة.
 */
@OptIn(ExperimentalRoborazziApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w390dp-h844dp-xxhdpi")
class ScreenshotTest {

    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    private fun shot(name: String) {
        compose.waitForIdle()
        captureScreenRoboImage("build/screenshots/$name.png")
    }

    private fun waitFor(matcher: androidx.compose.ui.test.SemanticsMatcher) {
        compose.waitUntil(timeoutMillis = 30_000) {
            compose.onAllNodes(matcher, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun captureAllScreens() {
        // الآثار
        waitFor(hasText("شجرة السند"))
        shot("01-main")

        // منسدلة الإقليم
        compose.onNode(hasContentDescription("الإقليم: كل الأقاليم")).performClick()
        waitFor(hasText("أهل الشام"))
        shot("02-region-dropdown")
        compose.onAllNodes(hasText("أهل الشام")).onFirst().performClick()
        waitFor(hasText("أهل الشام"))
        shot("03-main-filtered-sham")
        compose.onNode(hasText("تصفير الكل")).performClick()

        // شجرة السند ثم الترجمة
        waitFor(hasText("شجرة السند"))
        compose.onAllNodes(hasText("شجرة السند")).onFirst().performClick()
        waitFor(hasText("الإسناد كما في المصدر"))
        shot("04-chain-sheet")
        compose.onAllNodes(hasText("الترجمة")).onFirst().performClick()
        waitFor(hasText("عرض آثاره في الموسوعة"))
        shot("05-narrator-bio")
        compose.onNode(hasText("إغلاق")).performClick()
        compose.onAllNodes(hasContentDescription("إغلاق")).onFirst().performClick()
        compose.waitForIdle()

        // حفظ أول أثر ليظهر في المحفوظات
        compose.onAllNodes(hasContentDescription("حفظ في المحفوظات")).onFirst().performClick()

        // الرواة
        compose.onNode(hasText("الرواة")).performClick()
        waitFor(hasText("الفهرس"))
        shot("06-narrators")

        // الكتب
        compose.onNode(hasText("الكتب")).performClick()
        waitFor(hasText("تصفّح آثار الكتاب"))
        shot("07-books")

        // المحفوظات
        compose.onNode(hasText("المحفوظات")).performClick()
        waitFor(hasContentDescription("إزالة من المحفوظات"))
        shot("08-favorites")
    }
}
