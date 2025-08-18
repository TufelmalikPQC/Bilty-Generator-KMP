package com.bilty.generator.model.constants

object PDF {
    fun pdfSavePath(pdfName: String?) = "/Downloads/receipt-$pdfName.pdf"
}