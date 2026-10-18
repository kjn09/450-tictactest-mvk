package ch.bbw.m450.tictactoe.e2e;

import ch.bbw.m450.tictactoe.TicTacToeMain;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.StdIo;

@Tag("e2e")
class InvalidInputE2ETest implements WithAssertions {

	@Test
	@StdIo("abc")
	void rejectsTextInsteadOfANumber() {
		assertGameFailsWith(NumberFormatException.class, "\"abc\"");
	}

	@Test
	@StdIo({"", ""})
	void rejectsAnEmptyLine() {
		assertGameFailsWith(NumberFormatException.class, "For input string: \"\"");
	}

	@Test
	@StdIo("4.0")
	void rejectsDecimalNumbers() {
		assertGameFailsWith(NumberFormatException.class, "\"4.0\"");
	}

	@Test
	@StdIo(" 4")
	void rejectsALeadingSpace() {
		assertGameFailsWith(NumberFormatException.class, "\" 4\"");
	}

	@Test
	@StdIo("99999999999")
	void rejectsANumberThatIsTooBigForAnInt() {
		assertGameFailsWith(NumberFormatException.class, "\"99999999999\"");
	}

	@Test
	@StdIo("9")
	void rejectsAPositionAboveTheBoard() {
		assertGameFailsWith(IllegalStateException.class, "cannot play to position 9");
	}

	@Test
	@StdIo("-1")
	void rejectsANegativePosition() {
		assertGameFailsWith(IllegalStateException.class, "cannot play to position -1");
	}

	@Test
	@StdIo({"4", "4"})
	void rejectsAFieldThatIsAlreadyTaken() {
		assertGameFailsWith(IllegalStateException.class, "cannot play to position 4");
	}

	private void assertGameFailsWith(Class<? extends Throwable> type, String messagePart) {
		assertThatThrownBy(() -> TicTacToeMain.main(new String[0]))
				.isInstanceOf(type)
				.hasMessageContaining(messagePart);
	}
}