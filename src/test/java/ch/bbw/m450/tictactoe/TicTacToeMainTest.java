package ch.bbw.m450.tictactoe;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import static ch.bbw.m450.tictactoe.TestBoards.boardFrom;

import java.util.stream.Stream;

class TicTacToeMainTest implements WithAssertions {

	// --- Helper-Assertion: lesbari Assertion für "wer gwinnt" ---
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

	private static Stream<Arguments> winningBoardsForX() {
		return Stream.of(
				Arguments.of((Object) boardFrom("XXX OO. ...")),   // Reihe oben
				Arguments.of((Object) boardFrom("OO. XXX ...")),   // Reihe mitti
				Arguments.of((Object) boardFrom("OO. ... XXX")),   // Reihe unte
				Arguments.of((Object) boardFrom("X.. X.. X..")),   // Spalte links
				Arguments.of((Object) boardFrom(".X. .X. .X.")),   // Spalte mitti
				Arguments.of((Object) boardFrom("..X ..X ..X")),   // Spalte rechts
				Arguments.of((Object) boardFrom("X.. .X. ..X")),   // Diagonale \
				Arguments.of((Object) boardFrom("..X .X. X.."))    // Diagonale /
		);
	}

	// --- das gliche für O ---
	@ParameterizedTest
	@MethodSource("winningBoardsForO")
	void detectsWinsForO(Stone[] board) {
		assertWinner(board, Stone.CIRCLE);
	}

	private static Stream<Arguments> winningBoardsForO() {
		return Stream.of(
				Arguments.of((Object) boardFrom("OOO XX. ...")),
				Arguments.of((Object) boardFrom("O.. O.. O..")),
				Arguments.of((Object) boardFrom("O.. .O. ..O"))
		);
	}

	// --- Kein Gwinner: leerss Board, unentschiede Muster ---
	@ParameterizedTest
	@MethodSource("nonWinningBoards")
	void detectsNoWinner(Stone[] board) {
		assertNoWinner(board, Stone.CROSS);
		assertNoWinner(board, Stone.CIRCLE);
	}

	private static Stream<Arguments> nonWinningBoards() {
		return Stream.of(
				Arguments.of((Object) boardFrom("... ... ...")),   // leeres Board
				Arguments.of((Object) boardFrom("XOX OXO OXO")),   // Draw ohni Gwinner
				Arguments.of((Object) boardFrom("XO. OX. ..."))    // no offe, kei Reihe voll
		);
	}
}