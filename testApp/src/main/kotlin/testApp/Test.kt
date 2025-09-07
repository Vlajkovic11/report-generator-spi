package testApp

import calc.Calculator
import spec.ReportInterface
import java.sql.DriverManager
import java.util.Scanner
import java.util.ServiceLoader
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size < 1) {
        println("Usage: java -jar TestApp.jar <db_url>")
        exitProcess(1)
    }

    val dbUrl = args[0]

    println("Connecting to database...")
    val connection = DriverManager.getConnection(dbUrl)
    println("Connected to the database.")

    val scanner = Scanner(System.`in`)
    println("Enter your SQL query:")
    val sqlQuery = scanner.nextLine()

    val statement = connection.createStatement()
    val resultSet = statement.executeQuery(sqlQuery)

    val data = mutableMapOf<String, MutableList<String>>()
    val metaData = resultSet.metaData
    val columnCount = metaData.columnCount
    for (i in 1..columnCount) {
        data[metaData.getColumnName(i)] = mutableListOf()
    }

    while (resultSet.next()) {
        for (i in 1..columnCount) {
            data[metaData.getColumnName(i)]?.add(resultSet.getString(i))
        }
    }

    val serviceLoader = ServiceLoader.load(ReportInterface::class.java)
    println("Available report formats: ")
    serviceLoader.forEach { println(it.javaClass.simpleName) }

    println("Select a format (CSV, TXT, PDF):")
    val format = scanner.nextLine()

    val selectedService = serviceLoader.find { it.javaClass.simpleName.uppercase() == format.uppercase() }
        ?: throw IllegalArgumentException("Report format not supported.")


    val inputStream = object {}.javaClass.getResourceAsStream("/config2.json") ?: throw IllegalArgumentException("Configuration file not found.")
    val jsonConfig = inputStream.bufferedReader().use { it.readText() }

    println("Enter destination path for the report:")
    val destinationPath = scanner.nextLine()

    selectedService.generateReport(data, destinationPath, jsonConfig)

    println("Report generated successfully at $destinationPath")
}