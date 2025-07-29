package com.konnipativenkatesh7.queensrush

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class GameBoard(context: Context, attrs: AttributeSet) : View(context, attrs) {
    // Add board size property
    private var boardSize = 8 // Default size, can be changed via setter
    private lateinit var board: Array<Array<Int>>
    private var moveListener: (() -> Unit)? = null
    private var score = 0

    init {
        // Initialize the board
        initializeBoard()
    }

    private fun initializeBoard() {
        try {
            board = Array(boardSize) { Array(boardSize) { 0 } }
        } catch (e: Exception) {
            Log.e("GameBoard", "Error initializing board: ${e.message}")
            board = Array(8) { Array(8) { 0 } } // Fallback to default size
            boardSize = 8
        }
    }

    // Add setter for board size
    fun setBoardSize(size: Int) {
        if (size < 1) {
            throw IllegalArgumentException("Board size must be positive")
        }
        boardSize = size
        initializeBoard()
        invalidate()
    }

    fun setOnMoveListener(listener: () -> Unit) {
        moveListener = listener
    }

    fun getScore(): Int = score

    // Existing game over check functions
    fun checkGameOver(): Boolean {
        // Add game over condition check
        for (i in 0 until boardSize) {
            for (j in 0 until boardSize) {
                if (board[i][j] == 1) {
                    // Check if any valid moves exist for this queen
                    if (hasValidMoves(i, j)) {
                        return false
                    }
                }
            }
        }
        return true
    }

    fun hasValidMoves(row: Int, col: Int): Boolean {
        // Check all possible knight moves
        val moves = listOf(
            Pair(-2, -1), Pair(-2, 1), Pair(-1, -2), Pair(-1, 2),
            Pair(1, -2), Pair(1, 2), Pair(2, -1), Pair(2, 1)
        )
        
        for (move in moves) {
            val newRow = row + move.first
            val newCol = col + move.second
            if (isValidPosition(newRow, newCol) && board[newRow][newCol] == 0) {
                return true
            }
        }
        return false
    }

    private fun isValidPosition(row: Int, col: Int): Boolean {
        return row in 0 until boardSize && col in 0 until boardSize
    }

    // Add method to notify listener after a move
    private fun notifyMove() {
        moveListener?.invoke()
    }

    // Call notifyMove() after each successful move in your onTouchEvent or move handling method
    // For example:
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Handle touch down
                return true
            }
            MotionEvent.ACTION_UP -> {
                try {
                    // Process move
                    notifyMove()
                } catch (e: Exception) {
                    Log.e("GameBoard", "Error processing move: ${e.message}")
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // Add state restoration
    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return Bundle().apply {
            putParcelable("superState", superState)
            putInt("boardSize", boardSize)
            putInt("score", score)
            // Save board state
            val boardState = Array(boardSize) { i ->
                IntArray(boardSize) { j -> board[i][j] }
            }
            putSerializable("board", boardState)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val superState = state.getParcelable<Parcelable>("superState")
            super.onRestoreInstanceState(superState)
            
            boardSize = state.getInt("boardSize", 8)
            score = state.getInt("score", 0)
            
            // Restore board state
            @Suppress("UNCHECKED_CAST")
            val boardState = state.getSerializable("board") as Array<IntArray>
            board = Array(boardSize) { i ->
                Array(boardSize) { j -> boardState[i][j] }
            }
            invalidate()
        } else {
            super.onRestoreInstanceState(state)
        }
    }
} 