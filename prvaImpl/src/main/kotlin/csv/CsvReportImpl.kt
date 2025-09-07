package csv


import calc.Calculator
import com.google.gson.Gson
import spec.ReportInterface
import java.io.BufferedWriter

import java.io.FileWriter

class CsvReportImpl : ReportInterface{

    private val calculator = Calculator()

    override fun generateReport(data: Map<String, List<String>>, destinationPath: String, configJson: String) {
        val config = Gson().fromJson(configJson, ReportInterface.ReportConfig::class.java)

        val updatedData = applyCalculations(data, config.calculations ?: emptyList())

        val columns = updatedData.keys.toList()
        val numRows = updatedData.values.first().size

        BufferedWriter(FileWriter(destinationPath)).use { writer ->
            if (config.includeHeader) {
                writer.write(columns.joinToString(",") + "\n")
            }

            for (i in 0 until numRows) {
                val rowValues = columns.map { column ->
                    updatedData[column]?.getOrNull(i) ?: ""
                }
                writer.write(rowValues.joinToString(",") + "\n")
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
}