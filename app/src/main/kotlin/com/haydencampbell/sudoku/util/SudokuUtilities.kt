package com.haydencampbell.sudoku.util

import com.haydencampbell.sudoku.domain.model.SudokuNode
import com.haydencampbell.sudoku.domain.model.SudokuPuzzle
import com.haydencampbell.sudoku.domain.model.getHash
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.math.sqrt

internal val Int.sqrt: Int
    get() = sqrt(this.toDouble()).toInt()

internal fun puzzleIsComplete(puzzle: SudokuPuzzle): Boolean {
    return when {
        !puzzleIsValid(puzzle) -> false
        allSquaresAreNotEmpty(puzzle) -> false
        else -> true
    }
}


internal fun puzzleIsValid(puzzle: SudokuPuzzle): Boolean {
    return when {
        rowsAreInvalid(puzzle) -> false
        columnsAreInvalid(puzzle) -> false
        subgridsAreInvalid(puzzle) -> false
        else -> true
    }
}

internal fun rowsAreInvalid(puzzle: SudokuPuzzle): Boolean {
    (1..puzzle.boundary).forEach { row ->
        val nodeList: LinkedList<SudokuNode> = puzzle.graph[getHash(1, row)]!!
        (1..puzzle.boundary).forEach { value ->
            val occurrences = nodeList.filter { node ->
                node.y == value
            }.count { node ->
                node.color == value
            }
            if (occurrences > 1) return true
        }
    }

    return false
}

internal fun columnsAreInvalid(puzzle: SudokuPuzzle): Boolean {
    (1..puzzle.boundary).forEach { column ->
        val nodeList: LinkedList<SudokuNode> = puzzle.graph[getHash(column, 1)]!!
        (1..puzzle.boundary).forEach { value ->
            val occurrences = nodeList.filter { node ->
                node.x == value
            }.count { node ->
                node.color == value
            }
            if (occurrences > 1) return true
        }
    }

    return false
}

internal fun superCliqueIsValid(superClique: LinkedList<SudokuNode>) {

}

internal fun subgridsAreInvalid(puzzle: SudokuPuzzle): Boolean {
    val interval = puzzle.boundary.sqrt
    (1..interval).forEach { xIndex ->
        (1..interval).forEach { yIndex ->
            (1..puzzle.boundary).forEach { value ->
                val occurrences = getNodesBySubgrid(puzzle.graph,
                    xIndex * interval,
                    yIndex * interval,
                    puzzle.boundary
                ).count { node ->
                    node.color == value
                }
                if (occurrences > 1) return true
            }
        }
    }

    return false
}


internal fun getNodesByColumn(graph: LinkedHashMap<Int,
        LinkedList<SudokuNode>>, x: Int): List<SudokuNode> {
    val edgeList = mutableListOf<SudokuNode>()
    graph.values.filter {
        it.first.x == x
    }.forEach {
        edgeList.add(it.first)
    }
    return edgeList
}

internal fun getNodesByRow(graph: LinkedHashMap<Int,
        LinkedList<SudokuNode>>, y: Int): List<SudokuNode> {
    val edgeList = mutableListOf<SudokuNode>()
    graph.values.filter { it.first.y == y }.forEach { edgeList.add(it.first) }
    return edgeList
}

internal fun getNodesBySubgrid(graph: LinkedHashMap<Int,
        LinkedList<SudokuNode>>, x: Int, y: Int, boundary: Int): List<SudokuNode> {
    val edgeList = mutableListOf<SudokuNode>()
    val iMaxX = getIntervalMax(boundary, x)
    val iMaxY = getIntervalMax(boundary, y)

    ((iMaxX - boundary.sqrt) + 1..iMaxX).forEach { xIndex ->
        ((iMaxY - boundary.sqrt) + 1..iMaxY).forEach { yIndex ->
            edgeList.add(
                graph[getHash(xIndex, yIndex)]!!.first
            )
        }
    }

    return edgeList
}

internal fun getIntervalMax(boundary: Int, target: Int): Int {
    var intervalMax = 0
    val interval = boundary.sqrt

    (1..interval).forEach { index ->
        if (
            interval * index >= target &&
            target > (interval * index - interval)
        ) {
            intervalMax = index * interval
            return@forEach
        }
    }
    return intervalMax
}

internal fun allSquaresAreNotEmpty(puzzle: SudokuPuzzle): Boolean {
    puzzle.graph.values.forEach {
        if (it[0].color == 0) return true
    }
    return false
}

internal fun SudokuPuzzle.print() {
    var outputLine = ""
    (1..boundary).forEach { yIndex ->
        graph.values.filter { node ->
            node.first.y == yIndex
        }.forEach { node ->
            outputLine += node.first.color
            outputLine += " "
        }
        outputLine += "\n"
    }
    println(outputLine)
}