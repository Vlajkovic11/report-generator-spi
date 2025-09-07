package spec
/**
 * Defines the API for generating reports with various formats and styles.
 */
interface ReportInterface {
    /**
     * Generates a report from the given data and configuration.
     *
     * @param data The data to be reported, organized by column names with their corresponding values.
     * @param destinationPath The file system path where the report will be saved.
     * @param configJson JSON configuration specifying report options like headers, titles, etc.
     */
    fun generateReport(data: Map<String, List<String>>, destinationPath: String, configJson: String)


    /**
     * Configuration for generating reports.
     * @property includeHeader Indicates whether the report should include a header row.
     * @property title Optional title of the report, displayed at the top.
     * @property summary Optional list of summary entries to be displayed at the end of the report.
     * @property calculations Optional list of calculations applied to data within the report.
     * @property formatting Optional formatting settings to apply to the report.
     */
    data class ReportConfig(
        val includeHeader: Boolean,
        val title: String? = null,
        val summary: List<SummaryEntry>? = null,
        val calculations: List<CalculationConfig>? = null,
        val formatting: FormattingOptions? = null
    )
    /**
     * Specifies a calculation to be performed on the report data.
     * @property operation Type of calculation, "SUM", "AVERAGE", "MULTIPLY", "DIVIDE", "SUBTRACT".
     * @property columns Columns involved in the calculation.
     * @property resultColumn Column where the result of the calculation will be stored.
     */
    data class CalculationConfig(
        val operation: String,
        val columns: List<String>,
        val resultColumn: String
    )
    /**
     * Represents a summary entry for the report.
     * @property label Label for the summary entry.
     * @property value Fixed value for the summary, if not calculating from data.
     * @property operation Type of operation to perform if calculating, e.g., "SUM".
     * @property columns Columns to use for the calculation, if applicable.
     */
    data class SummaryEntry(
        val label: String,
        val value: String?,
        val operation: String?,
        val columns: List<String>?
    )
    /**
     * Formatting options for elements of the report.
     * @property titleStyle Formatting for the report title.
     * @property headerStyle Formatting for the report header.
     * @property summaryStyle Formatting for the summary section.
     * @property columnStyles Formatting settings for specific columns.
     */
    data class FormattingOptions(
        val titleStyle: StyleOptions? = null,
        val headerStyle: StyleOptions? = null,
        val summaryStyle: StyleOptions? = null,
        val columnStyles: Map<String, StyleOptions>? = null,
    )
    /**
     * Style settings for text.
     * @property isBold Whether the text should be bold.
     * @property isItalic Whether the text should be italic.
     * @property isUnderlined Whether the text should be underlined.
     * @property color Color of the text, specified as a hex code.
     */
    data class StyleOptions(
        val isBold: Boolean = false,
        val isItalic: Boolean = false,
        val isUnderlined: Boolean = false,
        val color: String = "#000000"
    )

}