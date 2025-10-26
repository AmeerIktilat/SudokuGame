import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SudokuMenu extends Application {

    private Label lastGameInfo;
    private boolean competitiveMode = false;
    private int mistakesRemaining = 3;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🧩 Sudoku Playground");

        Label title = new Label("Welcome to Sudoku Playground!");
        title.setStyle("-fx-font-size: 26px; -fx-font-family: 'Comic Sans MS'; -fx-text-fill: #6A1B9A;");

        Label subtitle = new Label("Choose your adventure:");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #4E342E;");

        Button playButton = new Button("🎮 Play");
        Button solveButton = new Button("🧠 Find Solution");
        Button exitButton = new Button("🚪 Exit");

        styleButton(playButton, "#FFAB91");
        styleButton(solveButton, "#80DEEA");
        styleButton(exitButton, "#F48FB1");

        lastGameInfo = new Label("Last played: None | Difficulty: None");
        lastGameInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #616161;");

        playButton.setOnAction(e -> startNewGame());
        solveButton.setOnAction(e -> openSolverGrid());
        exitButton.setOnAction(e -> primaryStage.close());

        VBox layout = new VBox(15);
        layout.getChildren().addAll(title, subtitle, playButton, solveButton, exitButton, lastGameInfo);
        layout.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-background-color: linear-gradient(to bottom, #FFF8E1, #FFECB3);");

        Scene scene = new Scene(layout, 350, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; -fx-font-size: 16px; -fx-font-family: 'Comic Sans MS'; -fx-text-fill: #212121; -fx-padding: 10 20 10 20;");
        button.setPrefWidth(200);
    }


    private void startNewGame() {
        SudokuBoard board = new SudokuBoard();
        board.generatePuzzle(30);

        Stage gameStage = new Stage();
        gameStage.setTitle("Sudoku Game");

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setStyle("-fx-padding: 20; -fx-alignment: center;");

        TextField[][] cells = new TextField[9][9];
        Label timerLabel = new Label("Time: 00:00");
        Label statusLabel = new Label();
        Button competitiveButton = new Button("Activate Competitive Mode");

        int[] seconds = {0};
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds[0]++;
            int min = seconds[0] / 60;
            int sec = seconds[0] % 60;
            timerLabel.setText(String.format("Time: %02d:%02d", min, sec));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        competitiveButton.setOnAction(e -> {
            competitiveMode = true;
            mistakesRemaining = 3;
            statusLabel.setText("Competitive Mode Activated: 3 mistakes allowed");
        });

        int[][] puzzle = board.getBoard();
        int[][] solution = board.getSolution();

        String[] blockColors = {
                "#FFAB91", "#80DEEA", "#A5D6A7",
                "#CE93D8", "#FFF176", "#90CAF9",
                "#F48FB1", "#B0BEC5", "#FFD54F"
        };

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                final int r = row;
                final int c = col;
                final int blockRow = r / 3;
                final int blockCol = c / 3;
                final int blockIndex = blockRow * 3 + blockCol;

                TextField cell = new TextField();
                cell.setPrefSize(45, 45);
                String bgColor = blockColors[blockIndex];
                cell.setStyle("-fx-background-color: " + bgColor + "; -fx-font-size: 20; -fx-font-weight: bold; -fx-alignment: center;");
                cells[r][c] = cell;

                int value = puzzle[r][c];
                if (value != 0) {
                    cell.setText(String.valueOf(value));
                    cell.setDisable(true);
                } else {
                    cell.textProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal.matches("[1-9]")) {
                            int val = Integer.parseInt(newVal);
                            board.setValue(r, c, val);

                            int correctValue = solution[r][c];
                            if (val != correctValue) {
                                if (competitiveMode) {
                                    mistakesRemaining--;
                                    statusLabel.setText("Mistake! Remaining: " + mistakesRemaining);
                                    if (mistakesRemaining <= 0) {
                                        Alert lose = new Alert(Alert.AlertType.ERROR, "Game Over! Too many mistakes.");
                                        lose.show();
                                        gameStage.close();
                                    }
                                }
                            }

                            boolean isComplete = true;
                            for (int i = 0; i < 9; i++) {
                                for (int j = 0; j < 9; j++) {
                                    if (board.getValue(i, j) != solution[i][j]) {
                                        isComplete = false;
                                        break;
                                    }
                                }
                            }
                            if (isComplete) {
                                Alert win = new Alert(Alert.AlertType.INFORMATION, "🎉 You solved it!");
                                win.show();
                            }

                        } else if (newVal.isEmpty()) {
                            board.setValue(r, c, 0);
                        } else {
                            cell.setText("");
                        }
                    });

                    cell.setOnMouseEntered(e -> {
                        for (int i = 0; i < 9; i++) {
                            cells[r][i].setStyle("-fx-background-color: #FFF59D; -fx-font-size: 20; -fx-font-weight: bold; -fx-alignment: center;");
                            cells[i][c].setStyle("-fx-background-color: #FFF59D; -fx-font-size: 20; -fx-font-weight: bold; -fx-alignment: center;");
                        }
                        cell.setStyle("-fx-background-color: #FFEB3B; -fx-font-size: 20; -fx-font-weight: bold; -fx-alignment: center;");
                    });

                    cell.setOnMouseExited(e -> {
                        for (int i = 0; i < 9; i++) {
                            int brRow = r / 3;
                            int bcCol = i / 3;
                            String colorRow = blockColors[brRow * 3 + bcCol];
                            cells[r][i].setStyle("-fx-background-color: " + colorRow + "; -fx-font-size: 20; -fx-font-weight: bold; -fx-alignment: center;");

                            brRow = i / 3;
                            bcCol = c / 3;
                            String colorCol = blockColors[brRow * 3 + bcCol];
                            cells[i][c].setStyle("-fx-background-color: " + colorCol + "; -fx-font-size: 20; -fx-font-weight: bold; -fx-alignment: center;");
                        }
                        String resetColor = blockColors[blockIndex];
                        cell.setStyle("-fx-background-color: " + resetColor + "; -fx-font-size: 20; -fx-font-weight: bold; -fx-alignment: center;");
                    });
                }

                grid.add(cell, c, r);
            }
        }

        VBox layout = new VBox(10, timerLabel, competitiveButton, grid, statusLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(layout, 500, 600);
        gameStage.setScene(scene);
        gameStage.show();

        lastGameInfo.setText("Last played: " + java.time.LocalTime.now() + " | Difficulty: Normal");
    }

    private void openSolverGrid() {
        SudokuBoard board = new SudokuBoard();
        TextField[][] cells = new TextField[9][9];

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setStyle("-fx-padding: 20; -fx-alignment: center;");

        String[] blockColors = {
                "#FFAB91", "#80DEEA", "#A5D6A7",
                "#CE93D8", "#FFF176", "#90CAF9",
                "#F48FB1", "#B0BEC5", "#FFD54F"
        };

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                final int r = row;
                final int c = col;
                final int blockIndex = (r / 3) * 3 + (c / 3);
                TextField cell = new TextField();
                cell.setPrefSize(45, 45);
                cell.setStyle("-fx-background-color: " + blockColors[blockIndex] + "; -fx-font-size: 20; -fx-font-weight: bold; -fx-alignment: center;");
                cells[r][c] = cell;

                cell.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal.matches("[1-9]")) {
                        board.setValue(r, c, Integer.parseInt(newVal));
                    } else if (newVal.isEmpty()) {
                        board.setValue(r, c, 0);
                    } else {
                        cell.setText("");
                    }
                });

                grid.add(cell, c, r);
            }
        }

        Button solveButton = new Button("Solve");
        solveButton.setOnAction(e -> {
            if (board.solve()) {
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (cells[i][j].getText().isEmpty()) {
                            int val = board.getValue(i, j);
                            cells[i][j].setText(String.valueOf(val));
                            cells[i][j].setStyle("-fx-background-color: #C8E6C9; -fx-font-size: 20; -fx-font-weight: bold; -fx-alignment: center;");
                        }
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "❌ No solution exists for this puzzle.");
                alert.show();
            }
        });

        VBox layout = new VBox(10, grid, solveButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Stage solverStage = new Stage();
        solverStage.setTitle("Sudoku Solver");
        solverStage.setScene(new Scene(layout, 500, 600));
        solverStage.show();
    }
}