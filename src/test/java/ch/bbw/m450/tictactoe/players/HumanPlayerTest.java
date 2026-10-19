package ch.bbw.m450.tictactoe.players;

import ch.bbw.m450.tictactoe.TestInput;
import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

class HumanPlayerTest implements WithAssertions {

	private final HumanPlayer player = new HumanPlayer();

	@Test
	@StdIo("4")
	void returnsPositionTypedByTheUser() {
		assertThat(player.play(new Stone[9], Stone.CROSS)).isEqualTo(4);
	}

	@Test
	@StdIo("0")
	void asksForTheNextMoveOfTheGivenColor(StdOut out) {
		player.play(new Stone[9], Stone.CIRCLE);
		assertThat(String.join("\n", out.capturedLines()))
				.contains("where to to put the next CIRCLE? (0-8):");
	}

	@Test
	@StdIo("abc")
	void rejectsInputThatIsNoNumber() {
		var board = new Stone[9];
		assertThatThrownBy(() -> player.play(board, Stone.CROSS))
				.isInstanceOf(NumberFormatException.class);
	}

	@Test
	void readsSeveralMovesFromTheSameInput() {
		var input = TestInput.with("4\n2\n");
		try (input) {
			var board = new Stone[9];
			assertThat(player.play(board, Stone.CROSS)).isEqualTo(4);
			assertThat(player.play(board, Stone.CROSS)).isEqualTo(2);
		}
	}
}