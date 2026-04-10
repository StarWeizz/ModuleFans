package fr.modulefans.controllers;

import fr.modulefans.dao.TicTacToeDAO;
import fr.modulefans.services.TicTacToeService;
import fr.modulefans.utils.NavigationManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class TicTacToeController implements Initializable {

    @FXML private Button cell0, cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8;
    @FXML private Label lblStatus;
    @FXML private Label lblWins, lblLosses, lblDraws;
    @FXML private ToggleButton btnEasy, btnHard;

    private final TicTacToeService service = new TicTacToeService();
    private final TicTacToeDAO dao = new TicTacToeDAO();

    private char[] board = new char[9];
    private boolean gameOver = false;
    private boolean hardMode = false;
    private Button[] cells;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cells = new Button[]{cell0, cell1, cell2, cell3, cell4, cell5, cell6, cell7, cell8};

        ToggleGroup group = new ToggleGroup();
        btnEasy.setToggleGroup(group);
        btnHard.setToggleGroup(group);
        btnEasy.setSelected(true);

        btnEasy.setOnAction(e -> hardMode = false);
        btnHard.setOnAction(e -> hardMode = true);

        for (int i = 0; i < 9; i++) {
            final int idx = i;
            cells[i].setOnAction(e -> handleCellClick(idx));
        }

        newGame();
        updateStats();
    }

    private void handleCellClick(int idx) {
        if (gameOver || board[idx] != TicTacToeService.EMPTY) return;

        // Player move
        board[idx] = TicTacToeService.PLAYER;
        cells[idx].setText("✕");
        cells[idx].getStyleClass().removeAll("ttt-cell-x", "ttt-cell-o");
        cells[idx].getStyleClass().add("ttt-cell-x");
        cells[idx].setDisable(true);

        if (checkEndGame()) return;

        // AI move after short delay
        setAllCellsDisabled(true);
        PauseTransition pause = new PauseTransition(Duration.millis(400));
        pause.setOnFinished(e -> {
            int aiMove = hardMode ? service.getHardMove(board) : service.getEasyMove(board);
            if (aiMove != -1) {
                board[aiMove] = TicTacToeService.AI;
                cells[aiMove].setText("○");
                cells[aiMove].getStyleClass().removeAll("ttt-cell-x", "ttt-cell-o");
                cells[aiMove].getStyleClass().add("ttt-cell-o");
                cells[aiMove].setDisable(true);
            }
            if (!checkEndGame()) {
                setAllCellsDisabled(false);
                // Re-disable already-played cells
                for (int i = 0; i < 9; i++) {
                    if (board[i] != TicTacToeService.EMPTY) cells[i].setDisable(true);
                }
            }
        });
        pause.play();
    }

    private boolean checkEndGame() {
        char winner = service.checkWinner(board);
        if (winner != TicTacToeService.EMPTY) {
            gameOver = true;
            setAllCellsDisabled(true);
            highlightWinningCombo();
            if (winner == TicTacToeService.PLAYER) {
                lblStatus.setText("🏆 Victoire ! Vous avez battu l'IA en duel premium !");
                dao.saveGame("PLAYER", hardMode ? "HARD" : "EASY");
            } else {
                lblStatus.setText("😈 L'IA vous a domptée. Abonnez-vous au niveau supérieur !");
                dao.saveGame("AI", hardMode ? "HARD" : "EASY");
            }
            updateStats();
            return true;
        }
        if (service.isBoardFull(board)) {
            gameOver = true;
            setAllCellsDisabled(true);
            lblStatus.setText("🤝 Match nul — Deux esprits également premium !");
            dao.saveGame("DRAW", hardMode ? "HARD" : "EASY");
            updateStats();
            return true;
        }
        return false;
    }

    private void highlightWinningCombo() {
        int[] combo = service.getWinningCombo(board);
        if (combo == null) return;
        for (int idx : combo) {
            cells[idx].setStyle("-fx-background-color: rgba(255,107,53,0.4); -fx-border-color: #ff6b35;");
        }
    }

    private void setAllCellsDisabled(boolean disabled) {
        for (Button cell : cells) cell.setDisable(disabled);
    }

    @FXML
    public void newGame() {
        board = new char[]{' ',' ',' ',' ',' ',' ',' ',' ',' '};
        gameOver = false;
        for (Button cell : cells) {
            cell.setText("");
            cell.setDisable(false);
            cell.setStyle("");
            cell.getStyleClass().removeAll("ttt-cell-x", "ttt-cell-o");
        }
        lblStatus.setText("À vous de jouer ! Montrez ce que vous valez. 🎯");
    }

    private void updateStats() {
        lblWins.setText(String.valueOf(dao.countByWinner("PLAYER")));
        lblLosses.setText(String.valueOf(dao.countByWinner("AI")));
        lblDraws.setText(String.valueOf(dao.countByWinner("DRAW")));
    }

    @FXML
    public void goBack() {
        NavigationManager.navigateTo("main_menu");
    }
}
