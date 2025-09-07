package pdf

import calc.Calculator
import com.google.gson.Gson
import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import spec.ReportInterface
import java.awt.Color
import java.io.FileOutputStream

class PdfReportImpl : ReportInterface {
    private val calculator = Calculator()
    override fun generateReport(data: Map<String, List<String>>, destinationPath: String, configJson: String) {
        val config = Gson().fromJson(configJson, ReportInterface.ReportConfig::class.java)
        val updatedData = applyCalculations(data, config.calculations ?: emptyList())

        val document = Document()
        PdfWriter.getInstance(document, FileOutputStream(destinationPath))
        document.open()

        config.title?.let {
            val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18f)
            applyFontStyle(titleFont, config.formatting?.titleStyle)
            val titleParagraph = Paragraph(it, titleFont)
            titleParagraph.alignment = Element.ALIGN_CENTER
            document.add(titleParagraph)
            document.add(Chunk.NEWLINE)
        }


        val columns = updatedData.keys.toList()
        val table = PdfPTable(columns.size)



        if (config.includeHeader) {
            columns.forEach { column ->
                val headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f)
                applyFontStyle(headerFont, config.formatting?.headerStyle)
                val cell = PdfPCell(Phrase(column, headerFont))
                cell.horizontalAlignment = Element.ALIGN_CENTER
                table.addCell(cell)
            }
            table.completeRow()
        }


        val numRows = updatedData.values.first().size
        for (i in 0 until numRows) {
            columns.forEach { column ->
                val cellData = updatedData[column]?.get(i) ?: ""
                val cellFont = FontFactory.getFont(FontFactory.HELVETICA, 12f)
                applyFontStyle(cellFont, config.formatting?.columnStyles?.get(column))
                val cell = PdfPCell(Phrase(cellData, cellFont))
                table.addCell(cell)
            }
            table.completeRow()
        }

        document.add(table)

        config.summary?.forEach { entry ->
            val summaryValue = generateSummary(entry, updatedData)
            val summaryFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12f)
            applyFontStyle(summaryFont, config.formatting?.summaryStyle)
            document.add(Paragraph("${entry.label}: $summaryValue", summaryFont))
        }

        document.close()
    }


    private fun applyCalculations(
        data: Map<String, List<String>>,
        calculations: List<ReportInterface.CalculationConfig>
    ): Map<String, List<String>> {
        if (calculations.isEmpty()) {
            return data
        }
        val result = data.toMutableMap()

        calculations.forEach { calc ->
            val newColumn = mutableListOf<String>()
            for (i in 0 until data.values.first().size) {
                val values = calc.columns.map { columnName ->
                    data[columnName]?.get(i)?.toDouble() ?: throw IllegalArgumentException("Column $columnName contains non-numeric data")
                }
                val resultValue = calculator.calculate(calc.operation, values)
                newColumn.add(resultValue.toString())
            }
            result[calc.resultColumn] = newColumn
        }

        return result
    }


    private fun generateSummary(entry: ReportInterface.SummaryEntry, data: Map<String, List<String>>): String {

        val operation = entry.operation ?: return entry.value ?: "N/A"

        val values = mutableListOf<Double>()


        entry.columns?.forEach { columnName ->
            data[columnName]?.forEach { stringValue ->
                stringValue.toDoubleOrNull()?.let {
                    values.add(it)
                }
            }
        }

        if (values.isEmpty()) return "N/A"


        val summaryValue = calculator.calculate(operation, values)

        return String.format("%.2f", summaryValue)
    }

    private fun applyFontStyle(font: Font, styleOptions: ReportInterface.StyleOptions?) {
        styleOptions?.let {
            if (it.isBold) font.style = Font.BOLD
            if (it.isItalic) font.style = font.style or Font.ITALIC
            if (it.isUnderlined) font.style = font.style or Font.UNDERLINE
            font.color = Color.decode(it.color)
        }
    }
}