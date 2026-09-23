package ch.bbw.m450.tictactoe;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.assertj.core.api.WithAssertions;

import java.util.stream.Stream;

public class TicTacToeMainTest implements WithAssertions {

	// --- Helper: baut ein Stone[]-Board aus lesbarem String-Layout ---
	private static Stone[] boardFrom(String layout) {
		var chars = layout.replace(" ", "").toCharArray();
		var board = new Stone[9];
		for (int i = 0; i < chars.length; i++) {
			board[i] = switch (chars[i]) {
				case 'X' -> Stone.CROSS;
				case 'O' -> Stone.CIRCLE;
				default -> null;
			};
		}
		return board;
	}

	// --- Helper-Assertion: liesbari Assertion für "wer gwinnt" ---
	private void assertWinner(Stone[] board, Stone winner) {
		assertThat(TicTacToeMain.isWin(board, winner)).isTrue();
	}

	private void assertNoWinner(Stone[] board, Stone color) {
		assertThat(TicTacToeMain.isWin(board, color)).isFalse();
	}

	// --- Parameterized Test: alli Gwinn-Konstellatione für X ---
	@ParameterizedTest
	@MethodSource("winningBoardsForX")
	void detectsWinsForX(Stone[] board) {
		assertWinner(board, Stone.CROSS);
	}

	private static Stream<Stone[]> winningBoardsForX() {
		return Stream.of(
				boardFrom("XXX OO. ..."),   // Reihe oben
				boardFrom("OO. XXX ..."),   // Reihe mitti
				boardFrom("OO. ... XXX"),   // Reihe unte
				boardFrom("X.. X.. X.."),   // Spalte links
				boardFrom(".X. .X. .X."),   // Spalte mitti
				boardFrom("..X ..X ..X"),   // Spalte rechts
				boardFrom("X.. .X. ..X"),   // Diagonale \
				boardFrom("..X .X. X..")    // Diagonale /
		);
	}

	// --- S'gliche für O ---
	@ParameterizedTest
	@MethodSource("winningBoardsForO")
	void detectsWinsForO(Stone[] board) {
		assertWinner(board, Stone.CIRCLE);
	}

	private static Stream<Stone[]> winningBoardsForO() {
		return Stream.of(
				boardFrom("OOO XX. ..."),
				boardFrom("O.. O.. O.."),
				boardFrom("O.. .O. ..O")
		);
	}

	// --- Kein Gwinner: leers Board, unentschiede Muster ---
	@ParameterizedTest
	@MethodSource("nonWinningBoards")
	void detectsNoWinner(Stone[] board) {
		assertNoWinner(board, Stone.CROSS);
		assertNoWinner(board, Stone.CIRCLE);
	}

	private static Stream<Stone[]> nonWinningBoards() {
		return Stream.of(
				boardFrom("... ... ..."),   // leers Board
				boardFrom("XOX OXO OXO"),   // Draw ohni Gwinner
				boardFrom("XO. OX. ...")    // no offe, kei Reihe voll
		);
	}
}