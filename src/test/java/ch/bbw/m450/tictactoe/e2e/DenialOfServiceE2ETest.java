package ch.bbw.m450.tictactoe.e2e;

import static java.util.concurrent.TimeUnit.SECONDS;

import ch.bbw.m450.tictactoe.TestInput;
import ch.bbw.m450.tictactoe.TicTacToeMain;
import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import ch.bbw.m450.tictactoe.players.MinimaxPlayer;
import java.util.NoSuchElementException;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

@Tag("e2e")
class DenialOfServiceE2ETest implements WithAssertions {

	@Test
	@StdIo
	@Timeout(value = 10, unit = SECONDS)
	void rejectsAnEnormousInputLineInReasonableTime(StdOut out) {
		var input = TestInput.with("9".repeat(5_000_000) + "\n");
		try (input) {
			assertThatThrownBy(() -> TicTacToeMain.main(new String[0]))
					.isInstanceOf(NumberFormatException.class);
			assertThat(out.capturedLines()).isNotEmpty();
		}
	}

	@Test
	@Timeout(value = 5, unit = SECONDS)
	void perfectPlayerAnswersOnAnEmptyBoardInTime() {
		assertThat(new MinimaxPlayer().play(new Stone[9], Stone.CROSS)).isBetween(0, 8);
	}

	@Test
	@StdIo
	@Timeout(value = 5, unit = SECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
	void gameWithoutAnyInputEndsInsteadOfWaiting(StdOut out) {
		var input = TestInput.with("");
		try (input) {
			assertThatThrownBy(() -> TicTacToeMain.main(new String[0]))
					.isInstanceOf(NoSuchElementException.class);
			assertThat(out.capturedLines()).isNotEmpty();
		}
	}
}