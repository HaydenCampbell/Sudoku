package com.haydencampbell.sudoku

import com.haydencampbell.sudoku.domain.model.Difficulty
import com.haydencampbell.sudoku.domain.model.SudokuNode
import com.haydencampbell.sudoku.domain.model.SudokuPuzzle
import com.haydencampbell.sudoku.domain.model.getHash
import com.haydencampbell.sudoku.util.*
import org.junit.Test
import java.util.*
import kotlin.collections.LinkedHashMap

class SudokuAlgorithmTests {
    @Test
    fun unsolverTest() {
        val list = mutableListOf<SudokuPuzzle>()
        buildNewSudoku(9, Difficulty.MEDIUM)
    }

    @Test
    fun getPossibleTestValues() {
        val puzzle = SudokuPuzzle(4, Difficulty.EASY)

        puzzle.graph.values.forEach { it.first.color = 0 }

        puzzle.graph[getHash(2, 1)]!!.first.color = 2
        puzzle.graph[getHash(2, 2)]!!.first.color = 1
        puzzle.graph[getHash(3, 1)]!!.first.color = 1
        puzzle.graph[getHash(4, 2)]!!.first.color = 3
        puzzle.graph[getHash(1, 4)]!!.first.color = 2
        puzzle.graph[getHash(3, 3)]!!.first.color = 4

        println(puzzle.print())
    }

    @Test
    fun testSuperCliqueCountOccurences() {
        val puzzle = SudokuPuzzle(4, Difficulty.EASY)
        println(puzzle.print())

        val superClique = getSuperClique(puzzle.graph.values.first().first, puzzle)
        val boundary = puzzle.boundary
        val key = puzzle.graph.values.first().first
        val iMaxX = getIntervalMax(boundary, key.x)
        val iMaxY = getIntervalMax(boundary, key.y)

        assert(superClique.filter { node ->
            when {
                (node.x == key.x && node.y != key.y) -> true
                (node.x != key.x && node.y == key.y) -> true
                (
                        iMaxX == getIntervalMax(boundary, node.x) &&
                                iMaxY == getIntervalMax(boundary, node.y)
                        ) -> true
                else -> false
            }
        }.count().also { println(it) } == 8)
    }

    @Test
    fun difficultyByTechniqueTests() {
        val list = mutableListOf<SudokuPuzzle>()
        (1..100).forEach {
            list.add(buildNewSudoku(9, Difficulty.EASY))
        }

        println(
            list.count {
                isBasic(it.copy())
            }
        )

        println(
            list.count {
                isAdvanced(it.copy())
            }
        )
    }

    @Test
    fun difficultyTests() {
        val fourGraphEasy = buildNewSudoku(4, Difficulty.EASY).graph
        var coloredNodesFour = fourGraphEasy.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesFour == 8)

        val fourGraphMed = buildNewSudoku(4, Difficulty.MEDIUM).graph
        coloredNodesFour = fourGraphMed.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesFour == 7)

        val fourGraphHard = buildNewSudoku(4, Difficulty.HARD).graph
        coloredNodesFour = fourGraphHard.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesFour == 5)

        val nineGraphEasy = buildNewSudoku(9, Difficulty.EASY).graph
        var coloredNodesNine = nineGraphEasy.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesNine == 38)

        val nineGraphMed = buildNewSudoku(9, Difficulty.MEDIUM).graph
        coloredNodesNine = nineGraphMed.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesNine == 30)

        val nineGraphHard = buildNewSudoku(9, Difficulty.HARD).graph
        coloredNodesNine = nineGraphHard.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesNine == 24)

        val sixteenGraphEasy = buildNewSudoku(16, Difficulty.EASY).graph
        var coloredNodesSixteen =
            sixteenGraphEasy.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesSixteen == 122)

        val sixteenGraphMedium = buildNewSudoku(16, Difficulty.MEDIUM).graph
        coloredNodesSixteen =
            sixteenGraphMedium.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesSixteen == 97)

        val sixteenGraphHard = buildNewSudoku(16, Difficulty.HARD).graph
        coloredNodesSixteen = sixteenGraphHard.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesSixteen == 76)
    }

    @Test
    fun rangeTest() {
        (1..9).forEach {
            println(it)
        }
    }

    @Test
    fun minumumCliqueNiceValueTest() {

        println("first")
        var average = 0

        val fourGraph = SudokuPuzzle(4, Difficulty.MEDIUM)
        fourGraph.graph.values.forEach {
            average += getPossibleValues(it, 4).size
        }

        println(average / 16)

        average = 0

        val nineGraph = SudokuPuzzle(9, Difficulty.MEDIUM)
        nineGraph.graph.values.forEach {
            average += getPossibleValues(it, 9).size
        }
        println(average / 81)

        average = 0

        val sixteenGraph = SudokuPuzzle(16, Difficulty.MEDIUM)
        sixteenGraph.graph.values.forEach {
            average += getPossibleValues(it, 16).size
        }
        println(average / 256)

        average = 0

        val twentyFive = SudokuPuzzle(25, Difficulty.MEDIUM)
        twentyFive.graph.values.forEach {
            average += getPossibleValues(it, 25).size
        }
        println(average / 625)
    }


    @Test
    fun linkedHashMapCopyTest() {
        val fourGraph = SudokuPuzzle(4, Difficulty.MEDIUM)
        val newMap = LinkedHashMap(fourGraph.graph)

        println("blah")
    }

    @Test
    fun verifySolverAlgorithm() {

        val fourGraph = SudokuPuzzle(4, Difficulty.MEDIUM)

        fourGraph.graph.values.forEach {
            assert(it.first.color != 0)
        }


        val nineGraph = SudokuPuzzle(9, Difficulty.MEDIUM)

        nineGraph.graph.values.forEach {
            assert(it.first.color != 0)
        }
    }

    @Test
    fun solverBenchmarks() {
        //Run the code once to hopefully warm up the JIT
        SudokuPuzzle(9, Difficulty.EASY).graph.values.forEach {
            assert(it.first.color != 0)
        }

        (1..100).forEach {
            SudokuPuzzle(9, Difficulty.EASY)
        }
    }


    @Test
    fun verifyGraphSize() {
        val fourGraph = SudokuPuzzle(4, Difficulty.MEDIUM)
        val nineGraph = SudokuPuzzle(9, Difficulty.MEDIUM)
        val sixteenGraph = SudokuPuzzle(16, Difficulty.MEDIUM)

        assert(fourGraph.graph.size == 16)
        assert(nineGraph.graph.size == 81)
        assert(sixteenGraph.graph.size == 256)
    }

    @Test
    fun testGraphColorSeed() {
        val fourGraph = buildNewSudoku(4, Difficulty.MEDIUM).graph
        val coloredNodesFour = fourGraph.entries.filter { it.value.first.color != 0 }.count()

        assert(puzzleIsValid(SudokuPuzzle(4, Difficulty.MEDIUM, fourGraph)))

        val nineGraph = buildNewSudoku(9, Difficulty.MEDIUM).graph
        val coloredNodesNine = nineGraph.entries.filter { it.value.first.color != 0 }.count()

        assert(puzzleIsValid(SudokuPuzzle(9, Difficulty.MEDIUM, nineGraph)))

        val sixteenGraph = buildNewSudoku(16, Difficulty.MEDIUM).graph
        val coloredNodesSixteen = sixteenGraph.entries.filter { it.value.first.color != 0 }.count()

        assert(puzzleIsValid(SudokuPuzzle(16, Difficulty.MEDIUM, sixteenGraph)))

        val twentyfiveGraph = buildNewSudoku(25, Difficulty.MEDIUM).graph
        val coloredNodesTwentyFive =
            twentyfiveGraph.entries.filter { it.value.first.color != 0 }.count()

        assert(puzzleIsValid(SudokuPuzzle(25, Difficulty.MEDIUM, twentyfiveGraph)))
    }

    @Test
    fun validityTest() {
        assert(!rowsAreInvalid(SudokuPuzzle(4, Difficulty.MEDIUM)))
        assert(!rowsAreInvalid(SudokuPuzzle(9, Difficulty.MEDIUM)))
        assert(!rowsAreInvalid(SudokuPuzzle(16, Difficulty.MEDIUM)))
        assert(!rowsAreInvalid(SudokuPuzzle(25, Difficulty.MEDIUM)))

        assert(!columnsAreInvalid(SudokuPuzzle(4, Difficulty.MEDIUM)))
        assert(!columnsAreInvalid(SudokuPuzzle(9, Difficulty.MEDIUM)))
        assert(!columnsAreInvalid(SudokuPuzzle(16, Difficulty.MEDIUM)))
        assert(!columnsAreInvalid(SudokuPuzzle(25, Difficulty.MEDIUM)))

        assert(!subgridsAreInvalid(SudokuPuzzle(4, Difficulty.MEDIUM)))
        assert(!subgridsAreInvalid(SudokuPuzzle(9, Difficulty.MEDIUM)))
        assert(!subgridsAreInvalid(SudokuPuzzle(16, Difficulty.MEDIUM)))
        assert(!subgridsAreInvalid(SudokuPuzzle(25, Difficulty.MEDIUM)))

        assert(allSquaresAreNotEmpty(SudokuPuzzle(4, Difficulty.MEDIUM)))
        assert(allSquaresAreNotEmpty(SudokuPuzzle(9, Difficulty.MEDIUM)))
        assert(allSquaresAreNotEmpty(SudokuPuzzle(16, Difficulty.MEDIUM)))
        assert(allSquaresAreNotEmpty(SudokuPuzzle(25, Difficulty.MEDIUM)))
    }

    @Test
    fun testHash() {
        val first = SudokuNode(1, 4)

        assert(first.hashCode() == 1004)
    }

    @Test
    fun getIntervalMaxTest() {
        var boundary = 4
        var target = 1

        var iMax = getIntervalMax(boundary, target)

        assert(iMax == 2)

        boundary = 9
        target = 5

        iMax = getIntervalMax(boundary, target)

        assert(iMax == 6)

        boundary = 16
        target = 2

        iMax = getIntervalMax(boundary, target)

        assert(iMax == 4)
    }

    @Test
    fun mergeTest() {
        val firstList = LinkedList<SudokuNode>()
        firstList.add(SudokuNode(1, 1, 0))
        val secondList = LinkedList<SudokuNode>()

        secondList.add(
            SudokuNode(1, 1, 0)
        )

        secondList.add(
            SudokuNode(1, 2, 0)
        )

        secondList.add(
            SudokuNode(1, 3, 0)
        )

        secondList.add(
            SudokuNode(1, 3, 0)
        )

        secondList.add(
            SudokuNode(1, 4, 0)
        )

        secondList.add(
            SudokuNode(1, 1, 0)
        )

        secondList.add(
            SudokuNode(1, 2, 0)
        )

        secondList.add(
            SudokuNode(1, 3, 0)
        )

        secondList.add(
            SudokuNode(1, 3, 0)
        )

        secondList.add(
            SudokuNode(1, 4, 0)
        )

        firstList.mergeWithoutRepeats(secondList)

        assert(firstList.size == 4)
    }

    @Test
    fun verifyEdgesBuilt() {
        val fourGraph = SudokuPuzzle(4, Difficulty.MEDIUM)
        val nineGraph = SudokuPuzzle(9, Difficulty.MEDIUM)
        val sixteenGraph = SudokuPuzzle(16, Difficulty.MEDIUM)

        fourGraph.graph.forEach {
            assert(it.value.size == 8)
        }

        nineGraph.graph.forEach {
            assert(it.value.size == 21)
        }

        sixteenGraph.graph.forEach {
            assert(it.value.size == 40)
        }
    }
}