package ch.bbw.m450.tictactoe.players;

import static ch.bbw.m450.tictactoe.TestBoards.boardFrom;

import ch.bbw.m450.tictactoe.TicTacToeMain;
import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MinimaxPlayerTest implements WithAssertions {

	private final MinimaxPlayer player = new MinimaxPlayer();

	@ParameterizedTest
	@CsvSource({
			"XX. OO. ..., CROSS, 2",
			"OO. XX. ..., CIRCLE, 2",
			"X.. XO. ..O, CROSS, 6"
	})
	void takesTheWinningMove(String layout, Stone color, int expected) {
		assertThat(player.play(boardFrom(layout), color)).isEqualTo(expected);
	}

	@ParameterizedTest
	@CsvSource({
			"XX. .O. ..., CIRCLE, 2",
			"OO. .X. ..X, CROSS, 2"
	})
	void blocksTheOpponentsWinningMove(String layout, Stone color, int expected) {
		assertThat(player.play(boardFrom(layout), color)).isEqualTo(expected);
	}

	@Test
	void doesNotChangeTheBoardItIsGiven() {
		var board = boardFrom("X.. .O. ...");
		var copy = board.clone();
		player.play(board, Stone.CROSS);
		assertThat(board).containsExactly(copy);
	}

	@Test
	void failsOnFullBoard() {
		var board = boardFrom("XOX OXO OXO");
		assertThatThrownBy(() -> player.play(board, Stone.CROSS))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("cannot play at all");
	}

	@Test
	void twoPerfectPlayersDraw() {
		assertThat(TicTacToeMain.play(new MinimaxPlayer(), new MinimaxPlayer())).isNull();
	}

	@Test
	void beatsGreedyPlayerAsCross() {
		assertThat(TicTacToeMain.play(new MinimaxPlayer(), new GreedyPlayer())).isEqualTo(Stone.CROSS);
	}

	@Test
	void beatsGreedyPlayerAsCircle() {
		assertThat(TicTacToeMain.play(new GreedyPlayer(), new MinimaxPlayer())).isEqualTo(Stone.CIRCLE);
	}
}