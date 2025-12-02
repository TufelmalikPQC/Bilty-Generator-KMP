package com.bilty.generator

import com.bilty.generator.bridge.PrinterManager
import com.bilty.generator.model.constants.Constants.BILTY_TEXT_FILE_PATH
import com.bilty.generator.model.enums.PrintStatus
import kotlinx.coroutines.runBlocking
import java.io.File

/**
 * Simple test program to test raw text printing functionality
 * Usage: Run this file to print $BILTY_TEXT_FILE using the new printRawText() function
 */
fun main() {
    println("═══════════════════════════════════════════════════════")
    println("  Raw Text Printer Test - Similar to thakur.bat")
    println("═══════════════════════════════════════════════════════")

    // Initialize PrinterManager
    val printerManager = PrinterManager()

    // Get available printers
    println("\n📋 Scanning for available printers...")
    val printers = printerManager.getAvailablePrinters()

    if (printers.isEmpty()) {
        println("❌ No printers found on this system!")
        return
    }

    println("\n✅ Found ${printers.size} printer(s):")
    printers.forEachIndexed { index, printer ->
        val marker = if (printer.isDefault) "⭐ [DEFAULT]" else "  "
        println("  ${index + 1}. $marker ${printer.name}")
    }

    // Path to the test text file
    val testFilePath =
        """C:\Users\Tufel Malik\Downloads\LUNIA COMPANY 2025\LUNIA COMPANY\$BILTY_TEXT_FILE_PATH"""

    println("\n📄 Reading test file: $testFilePath")

    val testFile = File(testFilePath)

    if (!testFile.exists()) {
        println("❌ Test file not found: $testFilePath")
        println("   Please update the file path in RawTextPrinterTest.kt")
        return
    }

    // Read file content
    val textContent = testFile.readText()
    println("✅ File loaded successfully (${textContent.length} characters)")

    // Print preview (first 200 characters)
    println("\n📄 Content Preview:")
    println("─────────────────────────────────────────────────────")
    println(textContent.take(200) + "...")
    println("─────────────────────────────────────────────────────")

    // Ask user confirmation
    println("\n⚠️  Ready to print to DEFAULT printer: ${printers.find { it.isDefault }?.name}")
    println("   Press ENTER to continue or type 'cancel' to abort:")

    val userInput = readlnOrNull()
    if (userInput?.lowercase() == "cancel") {
        println("❌ Print cancelled by user")
        return
    }

    // Print with ESC/P code interpretation
    println("\n🖨️  Printing with ESC/P formatting (exact format as thakur.bat)...")

    runBlocking {
        val result: PrintStatus = printerManager.printBiltyTextFile(null, null, null, null)

        val documentsSavedPath =
            System.getProperty("user.home") + "\\Documents\\bilty_formatted.pdf"

        println("\n═══════════════════════════════════════════════════════")
        when (result) {
            PrintStatus.COMPLETED -> {
                println("✅ SUCCESS! Formatted PDF created.")
                println("   📁 PDF Saved on Location: $documentsSavedPath")
                println("   This shows EXACTLY how $BILTY_TEXT_FILE_PATH.Txt would print!")
            }

            else -> {
                println("❌ FAILED!")
            }
        }
        println("═══════════════════════════════════════════════════════")
    }
}
