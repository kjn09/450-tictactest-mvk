package ch.bbw.m450.tictactoe;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/** Replaces System.in for the duration of a try-with-resources block. */
public final class TestInput implements AutoCloseable {

	private final InputStream original = System.in;

	private TestInput(String text) {
		System.setIn(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
	}

	public static TestInput with(String text) {
		return new TestInput(text);
	}

	@Override
	public void close() {
		System.setIn(original);
	}
}