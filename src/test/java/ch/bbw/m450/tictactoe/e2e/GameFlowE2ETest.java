package ch.bbw.m450.tictactoe.e2e;

import ch.bbw.m450.tictactoe.TicTacToeMain;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

@Tag("e2e")
class GameFlowE2ETest implements WithAssertions {

	@Test
	@StdIo({"4", "2", "6"})
	void humanCrossWinsWithTheDiagonal(StdOut out) {
		TicTacToeMain.main(new String[0]);

		assertThat(lastLineOf(out)).isEqualTo("...and the winner is: CROSS");
	}

	@Test
	@StdIo({"3", "4", "8"})
	void computerCircleWinsWhenTheHumanDoesNotBlock(StdOut out) {
		TicTacToeMain.main(new String[0]);

		assertThat(lastLineOf(out)).isEqualTo("...and the winner is: CIRCLE");
	}

	@Test
	@StdIo({"1", "3", "4", "6", "8"})
	void fullBoardWithoutWinnerEndsInADraw(StdOut out) {
		TicTacToeMain.main(new String[0]);

		assertThat(lastLineOf(out)).isEqualTo("it's a draw!");
	}

	private static String lastLineOf(StdOut out) {
		var lines = String.join("\n", out.capturedLines()).split("\\R");
		return lines[lines.length - 1];
	}
}