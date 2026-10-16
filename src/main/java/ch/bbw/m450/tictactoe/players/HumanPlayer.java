package ch.bbw.m450.tictactoe.players;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import ch.bbw.m450.tictactoe.TicTacToeMain;
import ch.bbw.m450.tictactoe.TicTacToePlayer;

public class HumanPlayer implements TicTacToePlayer {

	private Scanner scanner;

	@Override
	public int play(Stone[] board, Stone colorToPlay) {
		System.out.println(TicTacToeMain.toString(board) + "where to to put the next " + colorToPlay + "? (0-8): ");
		if (scanner == null) {
			scanner = new Scanner(System.in, StandardCharsets.UTF_8);
		}
		return Integer.parseInt(scanner.nextLine());
	}
}
