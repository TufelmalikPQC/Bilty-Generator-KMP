package com.bilty.generator.bridge

import biltygenerator.composeapp.generated.resources.Res
import com.bilty.generator.model.constants.Constants.RECEIPT_HEIGHT_POINTS
import com.bilty.generator.model.constants.Constants.RECEIPT_WIDTH_POINTS
import com.bilty.generator.model.data.PrinterInfo
import com.bilty.generator.model.enums.PrintStatus
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.printing.PDFPrintable
import org.apache.pdfbox.printing.Scaling
import java.awt.print.PageFormat
import java.awt.print.Paper
import java.awt.print.PrinterJob
import javax.print.PrintService
import javax.print.PrintServiceLookup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.io.StringWriter

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PrinterManager {
    
    // Store the last saved PDF path for UI display
    var lastSavedPdfPath: String? = null
        private set

    private fun logToFile(message: String) {
        try {
            val logFile = java.io.File(System.getProperty("java.io.tmpdir"), "bilty_debug_log.txt")
            val timestamp = java.time.LocalDateTime.now().toString()
            logFile.appendText("[$timestamp] $message\n")
            println(message)
        } catch (e: Exception) {
            println("Failed to write to log file: ${e.message}")
        }
    }

    actual fun getAvailablePrinters(): List<PrinterInfo> {
        val printServices = PrintServiceLookup.lookupPrintServices(null, null)
        val defaultService = PrintServiceLookup.lookupDefaultPrintService()
        return printServices.map { service ->
            PrinterInfo(name = service.name, isDefault = service == defaultService)
        }
    }

    actual suspend fun printBiltyText(textContent: String, printerName: String?): PrintStatus = withContext(Dispatchers.IO) {
        return@withContext try {
            logToFile("Starting printBiltyText with printer: $printerName")
            val document = PDDocument()
            // Create page in LANDSCAPE: width (5.82") > height (4.12")
            val page = PDPage(PDRectangle(RECEIPT_WIDTH_POINTS.toFloat(), RECEIPT_HEIGHT_POINTS.toFloat()))
            document.addPage(page)
            
            val contentStream = PDPageContentStream(document, page)
            
            var yPosition = RECEIPT_HEIGHT_POINTS.toFloat() - 20f
            val xMargin = 10f
            val lineHeight = 12f
            
            for (line in textContent.lines()) {
                if (line.trim().isEmpty()) {
                    yPosition -= lineHeight
                    continue
                }
                
                var currentFontSize = 10f
                val textSegments = mutableListOf<Pair<String, Float>>()
                var currentText = ""
                
                // Parse ESC/P codes inline
                for (ch in line) {
                    when (ch.code) {
                        0x0E -> {
                            if (currentText.isNotEmpty()) {
                                textSegments.add(Pair(currentText, currentFontSize))
                                currentText = ""
                            }
                            currentFontSize = 14f
                        }
                        0x0F -> {
                            if (currentText.isNotEmpty()) {
                                textSegments.add(Pair(currentText, currentFontSize))
                                currentText = ""
                            }
                            currentFontSize = 8f
                        }
                        0x12 -> {
                            if (currentText.isNotEmpty()) {
                                textSegments.add(Pair(currentText, currentFontSize))
                                currentText = ""
                            }
                            currentFontSize = 10f
                        }
                        0x0D, 0x0A -> { }
                        in 32..126 -> currentText += ch
                    }
                }
                
                if (currentText.isNotEmpty()) {
                    textSegments.add(Pair(currentText, currentFontSize))
                }
                
                // Print all segments on this line using absolute positioning
                if (textSegments.isNotEmpty()) {
                    var xPosition = xMargin
                    for ((text, fontSize) in textSegments) {
                        if (text.isNotEmpty()) {
                            contentStream.beginText()
                            contentStream.setFont(PDType1Font.COURIER, fontSize)
                            contentStream.newLineAtOffset(xPosition, yPosition)
                            contentStream.showText(text)
                            contentStream.endText()
                            
                            // Calculate approximate text width for next segment
                            xPosition += (text.length * fontSize * 0.6f)
                        }
                    }
                }
                
                yPosition -= lineHeight
                if (yPosition < 20f) break
            }
            
            contentStream.close()
            
            // Save PDF to Documents folder for verification
            val documentsPath = System.getProperty("user.home") + "\\Documents\\bilty_formatted.pdf"
            val outputFile = java.io.File(documentsPath)
            document.save(outputFile)
            
            // Store path for UI display
            lastSavedPdfPath = documentsPath
            
            logToFile("📄 PDF saved to: $documentsPath")
            logToFile("📐 PDF Page Size: ${RECEIPT_WIDTH_POINTS/72.0} × ${RECEIPT_HEIGHT_POINTS/72.0} inches (LANDSCAPE)")
            
            // Also get bytes for printing
            val outputStream = java.io.ByteArrayOutputStream()
            document.save(outputStream)
            val pdfBytes = outputStream.toByteArray()
            document.close()
            
            // Print without rotation - PDF is already landscape (5.82" × 4.12")
            printPdf(pdfBytes, printerName, null, false, null)
        } catch (e: Throwable) {
            logToFile("❌ Error in printBiltyText: ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
            val sw = java.io.StringWriter()
            val pw = java.io.PrintWriter(sw)
            e.printStackTrace(pw)
            logToFile(sw.toString())
            PrintStatus.FAILED
        }
    }


    actual suspend fun printBiltyTextTest(printerName: String?): PrintStatus = withContext(Dispatchers.IO) {
        return@withContext try {
            // Read the test file from resources - try multiple loading strategies
            logToFile("\n📄 Reading test file from resources: files/pqc_epson_lx.Txt")

            val testFileBytes = try {
                // Strategy 1: Use Compose Resources
                Res.readBytes("files/pqc_epson_lx.Txt")
            } catch (e: Exception) {
                logToFile("⚠️  Strategy 1 failed: ${e.message}")
                try {
                    // Strategy 2: Use ClassLoader
                    val inputStream = this@PrinterManager.javaClass.classLoader.getResourceAsStream("files/pqc_epson_lx.Txt")
                        ?: throw Exception("Resource not found via ClassLoader")
                    inputStream.readBytes()
                } catch (e2: Exception) {
                    logToFile("⚠️  Strategy 2 failed: ${e2.message}")
                    // Strategy 3: Try with leading slash
                    val inputStream = this@PrinterManager.javaClass.getResourceAsStream("/files/pqc_epson_lx.Txt")
                        ?: throw Exception("Resource not found with leading slash")
                    inputStream.readBytes()
                }
            }

            if (testFileBytes.isEmpty()) {
                logToFile("❌ Test file not found or empty in resources")
                logToFile("   Expected location: composeApp/src/commonMain/resources/files/pqc_epson_lx.Txt")
                return@withContext PrintStatus.FAILED
            }

            // Decode file content
            val fileContent = testFileBytes.decodeToString()
            logToFile("✅ Successfully loaded test file (${testFileBytes.size} bytes)")

            val document = PDDocument()
            // Create page in LANDSCAPE: width (5.82" > height (4.12")
            val page = PDPage(PDRectangle(RECEIPT_WIDTH_POINTS.toFloat(), RECEIPT_HEIGHT_POINTS.toFloat()))
            document.addPage(page)

            val contentStream = PDPageContentStream(document, page)

            var yPosition = RECEIPT_HEIGHT_POINTS.toFloat() - 20f
            val xMargin = 10f
            val lineHeight = 12f

            for (line in fileContent.lines()) {
                if (line.trim().isEmpty()) {
                    yPosition -= lineHeight
                    continue
                }

                var currentFontSize = 10f
                val textSegments = mutableListOf<Pair<String, Float>>()
                var currentText = ""

                // Parse ESC/P codes inline
                for (ch in line) {
                    when (ch.code) {
                        0x0E -> {
                            if (currentText.isNotEmpty()) {
                                textSegments.add(Pair(currentText, currentFontSize))
                                currentText = ""
                            }
                            currentFontSize = 14f
                        }
                        0x0F -> {
                            if (currentText.isNotEmpty()) {
                                textSegments.add(Pair(currentText, currentFontSize))
                                currentText = ""
                            }
                            currentFontSize = 8f
                        }
                        0x12 -> {
                            if (currentText.isNotEmpty()) {
                                textSegments.add(Pair(currentText, currentFontSize))
                                currentText = ""
                            }
                            currentFontSize = 10f
                        }
                        0x0D, 0x0A -> { }
                        in 32..126 -> currentText += ch
                    }
                }

                if (currentText.isNotEmpty()) {
                    textSegments.add(Pair(currentText, currentFontSize))
                }

                // Print all segments on this line using absolute positioning
                if (textSegments.isNotEmpty()) {
                    var xPosition = xMargin
                    for ((text, fontSize) in textSegments) {
                        if (text.isNotEmpty()) {
                            contentStream.beginText()
                            contentStream.setFont(PDType1Font.COURIER, fontSize)
                            contentStream.newLineAtOffset(xPosition, yPosition)
                            contentStream.showText(text)
                            contentStream.endText()

                            // Calculate approximate text width for next segment
                            xPosition += (text.length * fontSize * 0.6f)
                        }
                    }
                }

                yPosition -= lineHeight
                if (yPosition < 20f) break
            }

            contentStream.close()

            // Save PDF to Documents folder for verification
            val documentsPath = System.getProperty("user.home") + "\\Documents\\bilty_formatted.pdf"
            val outputFile = java.io.File(documentsPath)
            document.save(outputFile)
            
            // Store path for UI display
            lastSavedPdfPath = documentsPath
            
            logToFile("📄 PDF saved to: $documentsPath")
            logToFile("📐 PDF Page Size: ${RECEIPT_WIDTH_POINTS/72.0} × ${RECEIPT_HEIGHT_POINTS/72.0} inches (LANDSCAPE)")

            // Also get bytes for printing
            val outputStream = java.io.ByteArrayOutputStream()
            document.save(outputStream)
            val pdfBytes = outputStream.toByteArray()
            document.close()

            // Print without rotation - PDF is already landscape (5.82" × 4.12")
            printPdf(pdfBytes, printerName, null, false, null)
        } catch (e: Throwable) {
            logToFile("❌ Error in printBiltyTextTest: ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
            val sw = java.io.StringWriter()
            val pw = java.io.PrintWriter(sw)
            e.printStackTrace(pw)
            logToFile(sw.toString())
            PrintStatus.FAILED
        }
    }


    actual suspend fun printPdf(
        pdfContentData: ByteArray,
        printerName: String?,
        fontSize: Int?,
        isLandscapeMode: Boolean?,
        fontFamilyName: String?
    ): PrintStatus {
        if (pdfContentData.isEmpty()) {
            logToFile("❌ printPdf: pdfContentData is empty.")
            return PrintStatus.FAILED
        }

        val printServices = PrintServiceLookup.lookupPrintServices(null, null)
        val selectedService: PrintService? = if (printerName != null) {
            printServices.find { it.name == printerName }
        } else {
            PrintServiceLookup.lookupDefaultPrintService()
        }

        if (selectedService == null) {
            logToFile("❌ printPdf: No printer service found for name: $printerName. Default service also not found.")
            return PrintStatus.FAILED
        }

        var document: PDDocument? = null
        return try {
            logToFile("Starting printPdf with printer: $printerName")
            document = PDDocument.load(pdfContentData)
            logToFile("PDF loaded successfully. Size: ${pdfContentData.size} bytes")
            
            val job = PrinterJob.getPrinterJob()
            job.printService = selectedService
            logToFile("Printer job created for service: ${selectedService.name}")
            val pageFormat = job.defaultPage()
            
            // IMPORTANT: Always use PORTRAIT - the PDF is already landscape (5.82" × 4.12")
            // Setting LANDSCAPE would rotate it incorrectly
            pageFormat.orientation = PageFormat.PORTRAIT

            val paper = Paper()
            // PDF already has correct dimensions (5.82" × 4.12"), don't swap them
            paper.setSize(RECEIPT_WIDTH_POINTS, RECEIPT_HEIGHT_POINTS)
            paper.setImageableArea(0.0, 0.0, RECEIPT_WIDTH_POINTS, RECEIPT_HEIGHT_POINTS)
            
            pageFormat.paper = paper
            
            logToFile("🖨️  Print Settings:")
            logToFile("   Orientation: PORTRAIT (PDF is already landscape)")
            logToFile("   Paper Size: ${paper.width/72.0} × ${paper.height/72.0} inches")

            job.setPrintable(PDFPrintable(document, Scaling.ACTUAL_SIZE), pageFormat)
            logToFile("Starting job.print()")
            job.print()
            logToFile("Job printed successfully")
            document.close()
            PrintStatus.SUCCESS
        } catch (e: Throwable) {
            logToFile("❌ Error in printPdf: ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
            val sw = java.io.StringWriter()
            val pw = java.io.PrintWriter(sw)
            e.printStackTrace(pw)
            logToFile(sw.toString())
            PrintStatus.FAILED
        } finally {
            document?.close()
        }
    }
}