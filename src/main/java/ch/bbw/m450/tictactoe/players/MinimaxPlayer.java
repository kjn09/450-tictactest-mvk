package ch.bbw.m450.tictactoe.players;

import ch.bbw.m450.tictactoe.TicTacToeMain;
import ch.bbw.m450.tictactoe.TicTacToePlayer;


public class MinimaxPlayer implements TicTacToePlayer {

	private static final int WIN_SCORE = 10;

	@Override
	public int play(Stone[] board, Stone colorToPlay) {
		var bestScore = Integer.MIN_VALUE;
		var bestMove = -1;
		for (var i = 0; i < TicTacToeMain.BOARD_SIZE; i++) {
			if (board[i] == null) {
				board[i] = colorToPlay;
				var score = minimax(board, colorToPlay, colorToPlay.opponent(), 1);
				board[i] = null;
				if (score > bestScore) {
					bestScore = score;
					bestMove = i;
				}
			}
		}
		if (bestMove < 0) {
			throw new IllegalStateException("cannot play at all");
		}
		return bestMove;
	}

	private int minimax(Stone[] board, Stone me, Stone toMove, int depth) {
		if (TicTacToeMain.isWin(board, me)) {
			return WIN_SCORE - depth;
		}
		if (TicTacToeMain.isWin(board, me.opponent())) {
			return depth - WIN_SCORE;
		}
		var maximizing = toMove == me;
		var best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		var moved = false;
		for (var i = 0; i < TicTacToeMain.BOARD_SIZE; i++) {
			if (board[i] == null) {
				moved = true;
				board[i] = toMove;
				var score = minimax(board, me, toMove.opponent(), depth + 1);
				board[i] = null;
				best = maximizing ? Math.max(best, score) : Math.min(best, score);
			}
		}
		return moved ? best : 0;
	}
}