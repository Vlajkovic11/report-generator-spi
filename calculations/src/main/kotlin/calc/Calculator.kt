package calc
/**
 * Provides arithmetic calculations over a list of double values.
 * This class can perform basic arithmetic operations such as sum, subtraction, multiplication,
 * division, and averaging on a list of numbers.
 */
class Calculator {

    /**
     * Calculates a specified arithmetic operation on a list of double values.
     *
     * @param operation The arithmetic operation to perform. Supported operations are:
     * - "SUM": Sums up all the elements in the list.
     * - "SUBTRACT": Subtracts the second element from the first. Requires exactly two elements in the list.
     * - "MULTIPLY": Multiplies all elements in the list together.
     * - "DIVIDE": Divides the first element by the second element. Requires exactly two elements in the list and the second cannot be zero.
     * - "AVERAGE": Calculates the average of all elements in the list. Returns 0.0 if the list is empty.
     * @param data The list of double values to calculate on.
     * @return The result of the arithmetic operation as a double.
     * @throws IllegalArgumentException If the operation is "SUBTRACT" or "DIVIDE" and does not have exactly two elements,
     * or if "DIVIDE" is attempted with a zero divisor.
     * @throws UnsupportedOperationException If an unsupported operation is specified.
     */
    fun calculate(operation: String, data: List<Double>): Double {
        return when (operation) {
            "SUM" -> data.sum()
            "SUBTRACT" -> if (data.size == 2) data[0] - data[1] else throw IllegalArgumentException("Subtraction requires exactly two values")
            "MULTIPLY" -> data.reduce(Double::times)
            "DIVIDE" -> if (data.size == 2 && data[1] != 0.0) data[0] / data[1] else throw IllegalArgumentException("Division requires non-zero divisor")
            "AVERAGE" -> if (data.isNotEmpty()) data.average() else 0.0
            else -> throw UnsupportedOperationException("Unsupported operation: $operation")
        }
    }
}