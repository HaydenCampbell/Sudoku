package com.haydencampbell.sudoku.util

import com.haydencampbell.sudoku.domain.model.Difficulty
import com.haydencampbell.sudoku.domain.model.SudokuNode
import com.haydencampbell.sudoku.domain.model.SudokuPuzzle
import com.haydencampbell.sudoku.domain.model.getHash
import java.util.*
import kotlin.random.Random

internal fun SudokuPuzzle.unsolve(): SudokuPuzzle {
    var remove = ((boundary * boundary) - (boundary * boundary * difficulty.modifier)).toInt()

    val allocations = mutableListOf<SudokuNode>()


    var counter = 0

    while (counter <= remove) {
        var colored = true
        while (colored) {
            val randX = Random.nextInt(1, boundary + 1)
            val randY = Random.nextInt(1, boundary + 1)

            val node = this.graph[getHash(randX, randY)]!!.first



            if (node.color != 0) {
                allocations.add(
                    SudokuNode(
                        node.x,
                        node.y,
                        node.color,
                        node.readOnly
                    )
                )

                node.color = 0
                colored = false
                counter++
            }
        }
    }

    return when (determineDifficulty(this)) {
        SolvingStrategy.BASIC -> if (this.difficulty == Difficulty.EASY || this.difficulty == Difficulty.MEDIUM) this.apply {
            allocations.forEach { node ->
                this.graph[node.hashCode()]!!.first.color = 0
                this.graph[node.hashCode()]!!.first.readOnly = false
            }
        } else {
            this.apply {
                allocations.forEach { node ->
                    this.graph[node.hashCode()]!!.first.color = node.color
                }
            }.unsolve()
        }
        SolvingStrategy.ADVANCED -> if (this.difficulty == Difficulty.HARD )
            this.apply {
                allocations.forEach { node ->
                    this.graph[node.hashCode()]!!.first.color = 0
                    this.graph[node.hashCode()]!!.first.readOnly = false
                }
            } else
            this.apply {
                allocations.forEach { node ->
                    this.graph[node.hashCode()]!!.first.color = node.color
                }
            }.unsolve()
        else -> this.apply {
            allocations.forEach { node ->
                this.graph[node.hashCode()]!!.first.color = node.color
            }
        }.unsolve()
    }
}

internal fun determineDifficulty(
    puzzle: SudokuPuzzle
): SolvingStrategy {
    val basicSolve = isBasic(
        puzzle
    )
    val advancedSolve = isAdvanced(
        puzzle
    )

    if (basicSolve) return SolvingStrategy.BASIC
    else if (advancedSolve) return SolvingStrategy.ADVANCED
    else {
        puzzle.print()
        return SolvingStrategy.UNSOLVABLE
    }
}

internal fun isBasic(puzzle: SudokuPuzzle): Boolean {
    var solveable = true

    while (solveable) {
        solveable = false

        puzzle.graph.values.forEach {
            if (basicSolver(it, puzzle.boundary)) solveable = true
        }

        if (puzzleIsComplete(puzzle)) return true
    }

    return solveable
}

internal fun basicSolver(clique: LinkedList<SudokuNode>, boundary: Int): Boolean {
    if (clique.first.color == 0) {
        val options = getPossibleValues(clique, boundary)

        if (options.size == 1) {
            clique.first.color = options.first()
            return true
        }
    }

    return false
}

internal fun isAdvanced(puzzle: SudokuPuzzle): Boolean {
    var solveable = true

    while (solveable) {
        solveable = false

        puzzle.graph.values.filter { adjList -> adjList.first.color == 0 }.forEach {
            if (basicSolver(it, puzzle.boundary)) solveable = true
            else {
                val superClique: LinkedList<SudokuNode> = getSuperClique(it.first, puzzle)
                if (advancedSolver(puzzle, superClique, puzzle.boundary)) solveable = true
            }
        }

        if (puzzleIsComplete(puzzle)) return true
    }

    return solveable
}

fun advancedSolver(
    puzzle: SudokuPuzzle,
    superClique: LinkedList<SudokuNode>,
    boundary: Int
): Boolean {
    val firstNode = superClique.first()

    val firstOptions = getPossibleValues(firstNode, superClique, boundary)

    if (firstOptions.size != 2) return false

    val pairs = mutableListOf<SudokuNode>()

    superClique
        .forEach { node ->
            if (node.color == 0 && node != firstNode) {
                val secondOptions = getPossibleValues(node, superClique, boundary)
                if (secondOptions.size == 2 && areSameOptions(firstOptions, secondOptions)) {
                    pairs.add(node)
                }
            }
        }

    if (pairs.size == 0) return false

    if (
        puzzle.graph.values.count {
            it[0].color == 0
        } == firstOptions.size * 2
    ) return false

    pairs.forEach { pairNode ->
        if (
            testPair(
                firstOptions,
                firstNode,
                pairNode,
                puzzle
            )
        ) return true
    }

    return false
}

fun testPair(
    options: List<Int>,
    firstNode: SudokuNode,
    pairNode: SudokuNode,
    puzzle: SudokuPuzzle
): Boolean {
    firstNode.color = options[0]
    pairNode.color = options[1]

    val firstConfigIsValid = puzzleIsValid(puzzle)

    firstNode.color = options[1]
    pairNode.color = options[0]

    val secondConfigIsValid = puzzleIsValid(puzzle)

    if (firstConfigIsValid && !secondConfigIsValid) {
        firstNode.color = options[0]
        pairNode.color = options[1]

        return true
    }
    else if (!firstConfigIsValid && secondConfigIsValid) {
        firstNode.color = options[1]
        pairNode.color = options[0]

        return true
    }
    else if (firstConfigIsValid && secondConfigIsValid) {
        firstNode.color = options[1]
        pairNode.color = options[0]
        return true
    }
    else {
        firstNode.color = 0
        pairNode.color = 0
        return false
    }
}

fun areSameOptions(firstOptions: List<Int>, secondOptions: List<Int>): Boolean {
    firstOptions.forEach {
        if (!secondOptions.contains(it)) return false
    }

    return true
}

internal fun getSuperClique(first: SudokuNode, puzzle: SudokuPuzzle): LinkedList<SudokuNode> {
    val superClique = LinkedList<SudokuNode>()
    superClique.add(first)

    val iMaxX = getIntervalMax(puzzle.boundary, first.x)
    val iMaxY = getIntervalMax(puzzle.boundary, first.y)

    //get nodes by x interval:
    ((iMaxX - puzzle.boundary.sqrt) + 1..iMaxX).forEach { xIndex ->
        (1..puzzle.boundary).forEach { yIndex ->
            val node = puzzle.graph[getHash(xIndex, yIndex)]!!.first
            if (!superClique.contains(node)) superClique.add(node)
        }
    }

    //get nodes by y interval:
    ((iMaxY - puzzle.boundary.sqrt) + 1..iMaxY).forEach { yIndex ->
        (1..puzzle.boundary).forEach { xIndex ->
            val node = puzzle.graph[getHash(xIndex, yIndex)]!!.first
            if (!superClique.contains(node)) superClique.add(node)
        }
    }

    return superClique
}




enum class SolvingStrategy {
    BASIC,
    ADVANCED,
    UNSOLVABLE
}
