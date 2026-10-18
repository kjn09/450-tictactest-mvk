package ch.bbw.m450.tictactoe;

import ch.bbw.m450.tictactoe.players.GreedyPlayer;
import ch.bbw.m450.tictactoe.players.MinimaxPlayer;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

class GameOutputApprovalTest {

	@Test
	@StdIo
	void completeGameOutputOfGreedyAgainstMinimax(StdOut out) {
		TicTacToeMain.play(new GreedyPlayer(), new MinimaxPlayer());

		Approvals.verify(withoutColorCodes(String.join("\n", out.capturedLines())));
	}

	private static String withoutColorCodes(String text) {
		return text.replaceAll("\u001B\\[[0-9;]*m", "");
	}
}