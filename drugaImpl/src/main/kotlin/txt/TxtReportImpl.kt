package txt

import calc.Calculator
import com.google.gson.Gson
import spec.ReportInterface
import java.io.File

class TxtReportImpl : ReportInterface{

    private val calculator = Calculator()
    override fun generateReport(data: Map<String, List<String>>, destinationPath: String, configJson: String) {
        val config = Gson().fromJson(configJson, ReportInterface.ReportConfig::class.java)
        val updatedData = applyCalculations(data, config.calculations ?: emptyList())

        File(destinationPath).printWriter().use { writer ->
            config.title?.let {
                writer.println(it)
                writer.println("-".repeat(it.length))
            }

            val columns = updatedData.keys.toList()
            val columnWidths = columns.map { column ->
                val maxDataWidth = updatedData[column]?.maxOfOrNull { it.length } ?: 0
                maxOf(column.length, maxDataWidth)
            }

            if (config.includeHeader) {
                columns.forEachIndexed { index, column ->
                    writer.print(column.padEnd(columnWidths[index] + 2))
                }
                writer.println()
                columnWidths.forEach { width ->
                    writer.print("-".repeat(width + 2))
                }
                writer.println()
            }

            val numRows = updatedData.values.first().size
            for (i in 0 until numRows) {
                columns.forEachIndexed { index, column ->
                    val cell = updatedData[column]?.get(i) ?: ""
                    writer.print(cell.padEnd(columnWidths[index] + 2))
                }
                writer.println()
            }

            if (config.summary != null) {
                writer.println()
                writer.println("Summary:")
                config.summary?.forEach { entry ->
                    val summaryValue = generateSummary(entry, updatedData)
                    writer.println("${entry.label}: $summaryValue")
                }
            }
        }
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


}