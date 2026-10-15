package ch.bbw.m450.tictactoe;

import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import ch.bbw.m450.tictactoe.players.GreedyPlayer;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TicTacToeMainPlayTest implements WithAssertions {

	private static final class ScriptedPlayer implements TicTacToePlayer {
		private final int[] moves;
		private int next;

		ScriptedPlayer(int... moves) {
			this.moves = moves;
		}

		@Override
		public int play(Stone[] board, Stone colorToPlay) {
			return moves[next++];
		}
	}

	@Test
	void crossWinsWithTopRow() {
		var winner = TicTacToeMain.play(new ScriptedPlayer(0, 1, 2), new ScriptedPlayer(3, 4));
		assertThat(winner).isEqualTo(Stone.CROSS);
	}

	@Test
	void circleWinsWithMiddleRow() {
		var winner = TicTacToeMain.play(new ScriptedPlayer(0, 1, 8), new ScriptedPlayer(3, 4, 5));
		assertThat(winner).isEqualTo(Stone.CIRCLE);
	}

	@Test
	void fullBoardWithoutWinnerIsDraw() {
		var winner = TicTacToeMain.play(new ScriptedPlayer(0, 2, 5, 6, 7), new ScriptedPlayer(1, 3, 4, 8));
		assertThat(winner).isNull();
	}

	@Test
	void samePlayerTwiceIsRejected() {
		var player = new GreedyPlayer();
		assertThatThrownBy(() -> TicTacToeMain.play(player, player))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("players must differ");
	}

	@ParameterizedTest
	@ValueSource(ints = {-1, 9})
	void moveOutsideTheBoardIsRejected(int position) {
		assertThatThrownBy(() -> TicTacToeMain.play(new ScriptedPlayer(position), new ScriptedPlayer(0)))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("cannot play to position " + position);
	}

	@Test
	void moveOnOccupiedFieldIsRejected() {
		assertThatThrownBy(() -> TicTacToeMain.play(new ScriptedPlayer(0), new ScriptedPlayer(0)))
				.isInstanceOf(IllegalStateException.class)
				.hasMessage("cannot play to position 0");
	}
}