package net.cyclingbits.llmsecretscanner.core.util

import net.cyclingbits.llmsecretscanner.core.util.LogColors
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class LogColorsTest {

    @Test
    fun colorMethods_returnNonEmptyStrings() {
        val testText = "test"
        
        assertFalse(LogColors.red(testText).isEmpty())
        assertFalse(LogColors.green(testText).isEmpty())
        assertFalse(LogColors.blue(testText).isEmpty())
        assertFalse(LogColors.yellow(testText).isEmpty())
        assertFalse(LogColors.cyan(testText).isEmpty())
    }

    @Test
    fun boldColorMethods_returnNonEmptyStrings() {
        val testText = "test"
        
        assertFalse(LogColors.boldRed(testText).isEmpty())
        assertFalse(LogColors.boldGreen(testText).isEmpty())
        assertFalse(LogColors.boldCyan(testText).isEmpty())
        assertFalse(LogColors.boldYellow(testText).isEmpty())
    }

    @Test
    fun colorMethods_containInputText() {
        val testText = "hello world"
        
        assertTrue(LogColors.red(testText).contains(testText))
        assertTrue(LogColors.green(testText).contains(testText))
        assertTrue(LogColors.blue(testText).contains(testText))
        assertTrue(LogColors.yellow(testText).contains(testText))
        assertTrue(LogColors.cyan(testText).contains(testText))
    }

    @Test
    fun boldColorMethods_containInputText() {
        val testText = "hello world"
        
        assertTrue(LogColors.boldRed(testText).contains(testText))
        assertTrue(LogColors.boldGreen(testText).contains(testText))
        assertTrue(LogColors.boldCyan(testText).contains(testText))
        assertTrue(LogColors.boldYellow(testText).contains(testText))
    }

    @Test
    fun icons_areNonEmptyStrings() {
        assertFalse(LogColors.SCANNER_ICON.isEmpty())
        assertFalse(LogColors.FILE_ICON.isEmpty())
        assertFalse(LogColors.DOCKER_ICON.isEmpty())
        assertFalse(LogColors.SUCCESS_ICON.isEmpty())
        assertFalse(LogColors.ERROR_ICON.isEmpty())
        assertFalse(LogColors.ISSUE_ICON.isEmpty())
    }

    @Test
    fun colorMethods_handleEmptyString() {
        assertTrue(LogColors.red("").contains(""))
        assertTrue(LogColors.boldGreen("").contains(""))
    }
}