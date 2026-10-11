package ch.bbw.m450.tictactoe;


import ch.bbw.m450.tictactoe.TicTacToePlayer.Stone;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StoneTest implements WithAssertions {

	@ParameterizedTest
	@CsvSource({"CROSS, CIRCLE", "CIRCLE, CROSS"})
	void opponentIsTheOtherColor(Stone stone, Stone expected) {
		assertThat(stone.opponent()).isEqualTo(expected);
	}
}