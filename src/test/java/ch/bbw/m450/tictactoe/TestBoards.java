package ch.bbw.m450.tictactoe;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;

public final class TestBoards {

	private TestBoards() {
	}

	public static Stone[] boardFrom(String layout) {
		var chars = layout.replace(" ", "").toCharArray();
		var board = new Stone[TicTacToeMain.BOARD_SIZE];
		for (var i = 0; i < chars.length; i++) {
			board[i] = switch (chars[i]) {
				case 'X' -> Stone.CROSS;
				case 'O' -> Stone.CIRCLE;
				default -> null;
			};
		}
		return board;
	}
}