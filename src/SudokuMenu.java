import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SudokuMenu extends Application {

    private Label lastGameInfo;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sudoku Solver");

        // Buttons
        Button playButton = new Button("Play");
        Button solveButton = new Button("Find Solution");
        Button exitButton = new Button("Exit");

        // Info Label
        lastGameInfo = new Label("Last played: None | Difficulty: None");

        // Button Actions
        playButton.setOnAction(e -> startNewGame());
        solveButton.setOnAction(e -> openSolverGrid());
        exitButton.setOnAction(e -> primaryStage.close());

        // Layout
        VBox layout = new VBox(15);
        layout.getChildren().addAll(playButton, solveButton, exitButton, lastGameInfo);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(layout, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startNewGame() {
        // TODO: Launch Sudoku game window
        lastGameInfo.setText("Last played: " + java.time.LocalTime.now() + " | Difficulty: Normal");
    }

    private void openSolverGrid() {
        // TODO: Launch empty grid for user input
    }
}