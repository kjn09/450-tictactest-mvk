package ch.bbw.m450.tictactoe.players;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class GreedyPlayerTest implements WithAssertions {

	private final GreedyPlayer player = new GreedyPlayer();

	@Test
	void playsTopLeftOnEmptyBoard() {
		assertThat(player.play(new Stone[9], Stone.CROSS)).isZero();
	}

	@Test
	void skipsOccupiedFields() {
		var board = new Stone[9];
		board[0] = Stone.CROSS;
		board[1] = Stone.CIRCLE;
		assertThat(player.play(board, Stone.CROSS)).isEqualTo(2);
	}

	@Test
	void failsOnFullBoard() {
		var board = new Stone[9];
		Arrays.fill(board, Stone.CROSS);
		assertThatThrownBy(() -> player.play(board, Stone.CIRCLE))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("cannot play at all");
	}
}