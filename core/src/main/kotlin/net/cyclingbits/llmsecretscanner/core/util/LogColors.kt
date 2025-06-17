package net.cyclingbits.llmsecretscanner.core.util

object LogColors {
    // ANSI Color codes
    const val RESET = "\u001B[0m"
    const val RED = "\u001B[31m"
    const val GREEN = "\u001B[32m"
    const val YELLOW = "\u001B[33m"
    const val BLUE = "\u001B[34m"
    const val PURPLE = "\u001B[35m"
    const val CYAN = "\u001B[36m"
    const val WHITE = "\u001B[37m"
    const val BRIGHT_GREEN = "\u001B[92m"
    const val BRIGHT_YELLOW = "\u001B[93m"
    const val BRIGHT_BLUE = "\u001B[94m"
    const val BRIGHT_CYAN = "\u001B[96m"
    
    // Bold colors
    const val BOLD_RED = "\u001B[1;31m"
    const val BOLD_GREEN = "\u001B[1;32m"
    const val BOLD_YELLOW = "\u001B[1;33m"
    const val BOLD_BLUE = "\u001B[1;34m"
    const val BOLD_CYAN = "\u001B[1;36m"
    
    // Icons/Emojis
    const val SCANNER_ICON = "🔍"
    const val SUCCESS_ICON = "✅"
    const val WARNING_ICON = "⚠️"
    const val ERROR_ICON = "❌"
    const val INFO_ICON = "ℹ️"
    const val DOCKER_ICON = "🐳"
    const val FILE_ICON = "📄"
    const val SECURITY_ICON = "🔒"
    const val ISSUE_ICON = "🚨"
    
    // Helper functions
    fun red(text: String) = "$RED$text$RESET"
    fun green(text: String) = "$GREEN$text$RESET"
    fun yellow(text: String) = "$YELLOW$text$RESET"
    fun blue(text: String) = "$BLUE$text$RESET"
    fun cyan(text: String) = "$CYAN$text$RESET"
    fun boldRed(text: String) = "$BOLD_RED$text$RESET"
    fun boldGreen(text: String) = "$BOLD_GREEN$text$RESET"
    fun boldYellow(text: String) = "$BOLD_YELLOW$text$RESET"
    fun boldBlue(text: String) = "$BOLD_BLUE$text$RESET"
    fun boldCyan(text: String) = "$BOLD_CYAN$text$RESET"
}