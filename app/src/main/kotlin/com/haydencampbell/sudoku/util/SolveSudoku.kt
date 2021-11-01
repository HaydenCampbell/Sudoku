package com.haydencampbell.sudoku.util

import com.haydencampbell.sudoku.domain.model.SudokuNode
import com.haydencampbell.sudoku.domain.model.SudokuPuzzle
import com.haydencampbell.sudoku.domain.model.getHash
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.random.Random

internal fun SudokuPuzzle.solve()
        : SudokuPuzzle {

    val assignments = LinkedList<SudokuNode>()
    var assignmentAttempts = 0
    var partialBacktrack = false
    var fullbacktrackCounter = 0
    var niceValue: Int = (boundary / 2)
    var niceCounter = 0
    var newGraph = LinkedHashMap(this.graph)
    val uncoloredNodes = LinkedList<SudokuNode>()
    newGraph.values.filter { it.first.color == 0 }.forEach { uncoloredNodes.add(it.first) }

    while (uncoloredNodes.size > 0) {
        if (assignmentAttempts > boundary * boundary && partialBacktrack) {
            assignments.forEach { node ->
                node.color = 0
                uncoloredNodes.add(node)
            }

            assignments.clear()

            assignmentAttempts = 0
            partialBacktrack = false
            fullbacktrackCounter++
        } else if (assignmentAttempts > boundary * boundary * boundary) {
            partialBacktrack = true
            assignments.takeLast(assignments.size / 2)
                .forEach { node ->
                    node.color = 0
                    uncoloredNodes.add(node)
                    assignments.remove(node)
                }

            assignmentAttempts = 0
        }

        if (fullbacktrackCounter == boundary * boundary) {

            newGraph = this.seedColors().graph
            uncoloredNodes.clear()
            newGraph.values.filter { it.first.color == 0 }.forEach { uncoloredNodes.add(it.first) }
            assignments.clear()
            fullbacktrackCounter = 0
            niceValue = (boundary / 2)
        }

        val node = uncoloredNodes[Random.nextInt(0, uncoloredNodes.size)]
        val options = getPossibleValues(newGraph[getHash(node.x, node.y)]!!, boundary)

        if (options.size == 0) assignmentAttempts++
        else if (options.size > niceValue) {
            niceCounter++
            if (niceCounter > boundary * boundary) {
                niceValue++
                niceCounter = 0
            }
        } else {
            val color = options[Random.nextInt(0, options.size)]
            node.color = color
            uncoloredNodes.remove(node)
            assignments.add(node)
            if (niceValue > 1) niceValue--
        }
    }

    this.graph.clear()
    this.graph.putAll(newGraph)
    return this
}

fun getPossibleValues(adjList: LinkedList<SudokuNode>, boundary: Int): List<Int> {
    val options = mutableListOf<Int>()
    (1..boundary).forEach {
        adjList.first.color = it

        val occurrences = adjList.count { node ->
            node.color == it
        }

        if (occurrences == 1) options.add(it)
    }
    adjList.first.color = 0
    return options
}

fun getPossibleValues(
    key: SudokuNode,
    adjList: LinkedList<SudokuNode>,
    boundary: Int
): List<Int> {
    val options = mutableListOf<Int>()

    val iMaxX = getIntervalMax(boundary, key.x)
    val iMaxY = getIntervalMax(boundary, key.y)

    (1..boundary).forEach {
        key.color = it

        val occurrences = adjList.filter { node ->
            when {
                (node.x == key.x && node.y != key.y) -> true
                (node.x != key.x && node.y == key.y) -> true
                (
                        iMaxX == getIntervalMax(boundary, node.x) &&
                                iMaxY == getIntervalMax(boundary, node.y)
                        ) -> true
                else -> false
            }
        }.count { node ->
            node.color == it
        }

        if (occurrences == 1) options.add(it)
    }
    key.color = 0
    return options
}
