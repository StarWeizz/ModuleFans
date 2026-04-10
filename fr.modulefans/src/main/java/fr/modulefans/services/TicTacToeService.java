package fr.modulefans.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TicTacToeService {

    public static final char PLAYER = 'X';
    public static final char AI = 'O';
    public static final char EMPTY = ' ';

    private static final int[][] WIN_COMBOS = {
        {0,1,2},{3,4,5},{6,7,8}, // rows
        {0,3,6},{1,4,7},{2,5,8}, // cols
        {0,4,8},{2,4,6}          // diagonals
    };

    private final Random random = new Random();

    public char checkWinner(char[] board) {
        for (int[] combo : WIN_COMBOS) {
            char c = board[combo[0]];
            if (c != EMPTY && c == board[combo[1]] && c == board[combo[2]]) return c;
        }
        return EMPTY;
    }

    public boolean isBoardFull(char[] board) {
        for (char c : board) if (c == EMPTY) return false;
        return true;
    }

    public boolean isGameOver(char[] board) {
        return checkWinner(board) != EMPTY || isBoardFull(board);
    }

    /** Easy AI: random empty cell */
    public int getEasyMove(char[] board) {
        List<Integer> empty = getEmptyCells(board);
        if (empty.isEmpty()) return -1;
        return empty.get(random.nextInt(empty.size()));
    }

    /** Hard AI: win > block > center > corner > random */
    public int getHardMove(char[] board) {
        // 1. Try to win
        int win = findWinningMove(board, AI);
        if (win != -1) return win;

        // 2. Block player from winning
        int block = findWinningMove(board, PLAYER);
        if (block != -1) return block;

        // 3. Take center
        if (board[4] == EMPTY) return 4;

        // 4. Take a corner
        int[] corners = {0, 2, 6, 8};
        List<Integer> freeCorners = new ArrayList<>();
        for (int c : corners) if (board[c] == EMPTY) freeCorners.add(c);
        if (!freeCorners.isEmpty()) return freeCorners.get(random.nextInt(freeCorners.size()));

        // 5. Random remaining
        return getEasyMove(board);
    }

    private int findWinningMove(char[] board, char player) {
        for (int[] combo : WIN_COMBOS) {
            int emptyCount = 0, emptyIndex = -1, playerCount = 0;
            for (int idx : combo) {
                if (board[idx] == EMPTY) { emptyCount++; emptyIndex = idx; }
                else if (board[idx] == player) playerCount++;
            }
            if (playerCount == 2 && emptyCount == 1) return emptyIndex;
        }
        return -1;
    }

    private List<Integer> getEmptyCells(char[] board) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) if (board[i] == EMPTY) list.add(i);
        return list;
    }

    public int[] getWinningCombo(char[] board) {
        for (int[] combo : WIN_COMBOS) {
            char c = board[combo[0]];
            if (c != EMPTY && c == board[combo[1]] && c == board[combo[2]]) return combo;
        }
        return null;
    }
}
