package sorting

import java.io.File
import java.util.*
import kotlin.math.roundToInt
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    try {
        readInputData(args)
    } catch (errorSortType: SortingTypeException) {
        println("No sorting type defined!")
        exitProcess(0)
    } catch (errorDataType: DataTypeException)  {
        println("No data type defined!")
        exitProcess(0)
    } catch (invalidParam: InvalidParameterException) {
        val errorParam = invalidParam.message?.split("\\s+".toRegex())
        errorParam?.forEach { println("\"$it\" is not a valid parameter. It will be skipped.") }
        startProgram(args)
        exitProcess(0)
    }
    startProgram(args)
}

fun startProgram(args: Array<String>) {
    val indexDT = args.indexOf("-dataType")
    val indexST = args.indexOf("-sortingType")
    val indexIF = args.indexOf("-inputFile")
    val indexOF = args.indexOf("-outputFile")

    val typeData = if (indexDT == -1) "long" else args[indexDT + 1]

    val typeSort = when {
        indexST == -1 || args[indexST + 1] == "natural" -> "natural"
        args[indexST + 1] == "byCount" -> "byCount"
        else -> "byCount"
    }

    val typeInput = if (indexIF != -1 && args.lastIndex != indexIF) args[indexIF + 1] else "console"
    val typeOutput = if (indexOF != -1 && args.lastIndex != indexOF) args[indexOF + 1] else "console"


    when {
        typeData == "long" && typeSort == "natural" -> longData(1, typeInput, typeOutput)
        typeData == "long" && typeSort == "byCount" -> longData(2, typeInput, typeOutput)

        typeData == "line" && typeSort == "natural" -> lineData(1, typeInput, typeOutput)
        typeData == "line" && typeSort == "byCount" -> lineData(2, typeInput, typeOutput)

        typeData == "word" && typeSort == "natural" -> wordData(1, typeInput, typeOutput)
        typeData == "word" && typeSort == "byCount" -> wordData(2, typeInput, typeOutput)
    }
}

fun longData(sortingMode: Int, input: String, output: String) {
    val dataList: MutableList<Pair<Long, Int>> = mutableListOf()
    val scanner = Scanner(System.`in`)
    val regex = "\\s+".toRegex()
    val inputList = mutableListOf<String>()

    if (input == "console") {
        while (scanner.hasNext()) {
            inputList.addAll(scanner.next().split(regex))
        }
    } else {
        try {
            val fileName = input
            val lines = File(fileName).readText()
            inputList.addAll(lines.split(regex))
        } catch (e: NullPointerException) {
            println("Файл не найден!\nЗавершение программы...")
        }
    }

    for (elem in inputList) {
        try {
            dataList.add(Pair(elem.toLong(), 0))
        } catch (e: NumberFormatException) {
            println("\"$elem\" is not a long. It will be skipped.")
        }
    }
    sortingLong(dataList, sortingMode)
    if (sortingMode == 1) printNaturalPattern(dataList, output) else printByCountPattern(dataList, output)
}

fun lineData(sortingMode: Int, input: String, output: String) {
    val dataList: MutableList<Pair<String, Int>> = mutableListOf()
    val scanner = Scanner(System.`in`)

    if (input == "console") {
        while (scanner.hasNext()) {
            dataList.add(Pair(scanner.nextLine(), 0))
        }
    } else {
        try {
            val fileName = input
            val lines = File(fileName).readLines()
            for (ln in lines) dataList.add(Pair(ln, 0))
        } catch (e: NullPointerException) {
            println("Файл не найден!\nЗавершение программы...")
        }
    }

    sortingLong(dataList, sortingMode)
    if (sortingMode == 1) printNaturalLine(dataList, output) else printByCountPattern(dataList, output)
}

fun wordData(sortingMode: Int, input: String, output: String) {
    val dataList: MutableList<Pair<String, Int>> = mutableListOf()
    val scanner = Scanner(System.`in`)
    val regex = "\\s+".toRegex()

    if (input == "console") {
        while (scanner.hasNext()) {
            val words = scanner.nextLine().split(regex)
            for (elem in words) dataList.add(Pair(elem, 0))
        }
    } else {
        try {
            val fileName = input
            val lines = File(fileName).readText()
            val words = lines.split(regex)
            for (elem in words) dataList.add(Pair(elem, 0))
        } catch (e: NullPointerException) {
            println("Файл не найден!\nЗавершение программы...")
        }
    }

    sortingLong(dataList, sortingMode)
    if (sortingMode == 1) printNaturalPattern(dataList, output) else printByCountPattern(dataList, output)
}

// Подсчет вхождений элементов в списке

fun <T: Comparable<T>> counting(list: MutableList<Pair<T, Int>>): MutableList<Pair<T, Int>> {
    var copyList: MutableList<Pair<T, Int>> = mutableListOf()
    val newList: MutableList<Pair<T, Int>> = mutableListOf()
    copyList.addAll(list)

    while (copyList.isNotEmpty()) {
        val key = copyList.first().first
        val value = copyList.count { it.first == key }
        copyList = copyList.filter { it.first != key }.toMutableList()
        newList.add(Pair(key, value))
    }
    return newList
}

fun <T: Comparable<T>> sortingLong(list: MutableList<Pair<T, Int>>, sortingMode: Int) {
    if (sortingMode == 1) {
        list.sortBy { it.first }
    } else if (sortingMode == 2) {
        val nL = counting(list)
        list.clear()
        list.addAll(nL)
        list.sortBy { it.first }
        list.sortBy { it.second }
    }
}

fun <T: Comparable<T>> printNaturalPattern(list: MutableList<Pair<T, Int>>, output: String) {
    var dataStr = ""
    for (elem in list) dataStr += "${elem.first} "
    dataStr = dataStr.trimEnd()
    val text = "Total numbers: ${list.size}.\nSorted data: $dataStr"

    if (output == "console") {
        println(text)
    } else {
        try {
            val myFile = File(output)
            myFile.writeText(text)
        } catch (e: NullPointerException) {
            println("Не удалось найти или создать выходной файл!\nЗавершение программы...")
        }
    }
}

fun <T: Comparable<T>> printNaturalLine(list: MutableList<Pair<T, Int>>, output: String) {
    var dataStr = ""
    for (elem in list) dataStr += "\n${elem.first}"
    val text = "Total numbers: ${list.size}.\nSorted data: $dataStr"

    if (output == "console") {
        println(text)
    } else {
        try {
            val myFile = File(output)
            myFile.writeText(text)
        } catch (e: NullPointerException) {
            println("Не удалось найти или создать выходной файл!\nЗавершение программы...")
        }
    }
}

fun <T: Comparable<T>> printByCountPattern(list: MutableList<Pair<T, Int>>, output: String) {
    val totalElements = list.sumOf { it.second }
    var dataStr = ""
    for (elem in list) dataStr += "\n${elem.first}: ${elem.second} time(s), ${(elem.second * 100.0 / totalElements).roundToInt()}%"
    val text = "Total numbers: $totalElements.$dataStr"

    if (output == "console") {
        println(text)
    } else {
        try {
            val myFile = File(output)
            myFile.writeText(text)
        } catch (e: NullPointerException) {
            println("Не удалось найти или создать выходной файл!\nЗавершение программы...")
        }
    }
}

fun readInputData(args: Array<String>) {
    val validSort = arrayOf("natural", "byCount")
    val validData = arrayOf("long", "word", "line")
    val validArgument = arrayOf("-dataType", "-sortingType", "-inputFile", "-outputFile", "natural",
        "byCount", "long", "word", "line")

    val indexDT = args.indexOf("-dataType")
    val indexST = args.indexOf("-sortingType")

    if (indexST != -1 && (args.lastIndex == indexST || args[indexST + 1] !in validSort)) {
        throw SortingTypeException()
    } else if (indexDT != -1 && (args.lastIndex == indexDT || args[indexDT + 1] !in validData)) {
        throw DataTypeException()
    }

    val errorParam = mutableListOf<String>()

    for (i in args.indices) {
        if (i != 0 && (args[i - 1] == "-inputFile" || args[i - 1] == "-outputFile")) {
            continue
        } else if (args[i] !in validArgument) {
            errorParam.add(args[i])
        }
    }

    if (errorParam.isNotEmpty()) {
        throw InvalidParameterException(errorParam.joinToString(" "))
    }
}

class SortingTypeException(mess: String = "No sorting type defined!"): Exception(mess)
class DataTypeException(mess: String = "No data type defined!"): Exception(mess)
class InvalidParameterException(mess: String): Exception(mess)