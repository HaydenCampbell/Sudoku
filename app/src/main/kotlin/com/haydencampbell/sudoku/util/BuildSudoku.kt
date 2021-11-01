package com.haydencampbell.sudoku.util

import com.haydencampbell.sudoku.domain.model.Difficulty
import com.haydencampbell.sudoku.domain.model.SudokuNode
import com.haydencampbell.sudoku.domain.model.SudokuPuzzle
import com.haydencampbell.sudoku.domain.model.getHash
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.random.Random

internal fun buildNewSudoku(
    boundary: Int,
    difficulty: Difficulty
): SudokuPuzzle = buildNodes(boundary, difficulty)
    .buildEdges()
    .seedColors()
    .solve()
    .unsolve()

internal fun buildNodes(n: Int, difficulty: Difficulty): SudokuPuzzle {
    val newMap = LinkedHashMap<Int, LinkedList<SudokuNode>>()

    (1..n).forEach { xIndex ->
        (1..n).forEach { yIndex ->
            val newNode = SudokuNode(
                xIndex,
                yIndex,
                0
            )

            val newList = LinkedList<SudokuNode>()
            newList.add(newNode)
            newMap.put(
                newNode.hashCode(),
                newList
            )
        }
    }
    return SudokuPuzzle(n, difficulty, newMap)
}

internal fun SudokuPuzzle.buildEdges(): SudokuPuzzle {
    this.graph.forEach {
        val x = it.value.first.x
        val y = it.value.first.y

        it.value.mergeWithoutRepeats(
            getNodesByColumn(this.graph, x)
        )

        it.value.mergeWithoutRepeats(
            getNodesByRow(this.graph, y)
        )

        it.value.mergeWithoutRepeats(
            getNodesBySubgrid(this.graph, x, y, boundary)
        )

    }
    return this
}

internal fun LinkedList<SudokuNode>.mergeWithoutRepeats(new: List<SudokuNode>) {
    val hashes: MutableList<Int> = this.map { it.hashCode() }.toMutableList()
    new.forEach {
        if (!hashes.contains(it.hashCode())) {
            this.add(it)
            hashes.add(it.hashCode())
        }
    }
}

internal fun SudokuPuzzle.seedColors(): SudokuPuzzle {
    val allocatedNumbers = mutableListOf<Int>()

    var allocations = 0

    var byRow = true
    var ttb = true

    var loopCounter = 0

    while (loopCounter < boundary * 1000) {
        val rowOrColumnProgression = mutableListOf<Int>()
        if (ttb) (1..boundary.sqrt).forEach { rowOrColumnProgression.add(it) }
        else (boundary.sqrt downTo 1).forEach { rowOrColumnProgression.add(it) }

        rowOrColumnProgression.forEach { rowOrColumnIndex ->
            var newInt = Random.nextInt(1, boundary + 1)
            var notNew = true

            while (notNew) {
                if (!allocatedNumbers.contains(newInt)) notNew = false
                else if (allocatedNumbers.size == boundary) notNew = false
                else newInt = Random.nextInt(1, boundary + 1)
            }

            allocatedNumbers.add(newInt)

            ttb = Random.nextBoolean()

            (1..boundary.sqrt).forEach { subgridOffset ->
                val fixedCoordinate = boundary.sqrt * rowOrColumnIndex - boundary.sqrt
                val variantLowerBound = boundary.sqrt * subgridOffset - boundary.sqrt + 1
                val variantUpperBound = variantLowerBound + boundary.sqrt

                val hashList = mutableListOf<Int>()

                if (byRow) {
                    (variantLowerBound until variantUpperBound).forEach { variantCoordinate ->
                        hashList.add(getHash(variantCoordinate, fixedCoordinate + subgridOffset))
                    }
                } else {
                    (variantLowerBound until variantUpperBound).forEach { variantCoordinate ->
                        hashList.add(getHash(fixedCoordinate + subgridOffset, variantCoordinate))
                    }
                }

                hashList.firstOrNull { this.graph[it]?.first?.color == 0 }.let {
                    if (it != null) {
                        this.graph[it]!!.first.color = newInt
                        allocations++
                    }
                }

                if (boundary == 4 || allocatedNumbers.size == boundary - 1) return this
                else if (allocatedNumbers.size == boundary) {
                    return this
                }
            }
        }
        byRow = !byRow
        loopCounter++
    }
    return this
}

