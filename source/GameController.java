import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class GameController implements Runnable {
    private final GameModel model;
    private final GameView view;
    private Thread gameThread;
    private final int FPS = 60;     // Target frame rate: 60 frames per second

    public static final int RED = 0;
    public static final int BLUE = 1;
    private int currentTurn; 

    private Piece selectedPiece = null;

    public GameController(GameModel model) {
        this.model = model;
        this.view = new GameView(this);                 // Pass the controller to the view
        this.currentTurn = model.getCurrentTurn();      // Initialize from model

        if (currentTurn == RED) {
            System.out.println("Initial currentTurn: Red");
        } else {
            System.out.println("Initial currentTurn: Blue");
        }
    }

    public MouseAdapter getMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point relativePoint = SwingUtilities.convertPoint(
                        e.getComponent(),
                        e.getPoint(),
                        view.getCheckerboardPanel()
                );
                int col = relativePoint.x / 85;
                int row = relativePoint.y / 85;

                if (view.isFlipped() && currentTurn == RED) {
                    col = 4 - col;
                    row = 7 - row;
                }

                for (Piece piece : model.getPiecesList()) {
                    if (piece.getCol() == col && piece.getRow() == row && piece.color == currentTurn) {
                        selectedPiece = piece;
                        view.setDraggingPiece(selectedPiece, relativePoint.x, relativePoint.y);

                        if (selectedPiece != null) {
                            List<Point> movableTiles = calculateMovableTiles(selectedPiece);
                            view.setMovableTiles(movableTiles);
                        }
                        return;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedPiece != null) {
                    Point relativePoint = SwingUtilities.convertPoint(
                            e.getComponent(),
                            e.getPoint(),
                            view.getCheckerboardPanel()
                    );
                    int col = relativePoint.x / 85;
                    int row = relativePoint.y / 85;

                    if (view.isFlipped() && currentTurn == RED) {
                        col = 4 - col;
                        row = 7 - row;
                    }

                    if (model.movePiece(model.getPiecesList().indexOf(selectedPiece), col, row)) {
                        // Update the current turn after a successful move
                        currentTurn = (currentTurn == RED) ? BLUE : RED;
                        model.setCurrentTurn(currentTurn); // Update the model's current turn
                        System.out.println("Turn changed to: " + (currentTurn == RED ? "Red" : "Blue"));

                        // Increment turn counter and check for piece swapping
                        if (currentTurn == RED) {
                            model.incrementTurnCounter(); // Notify model to increment the turn counter
                            if (model.getTurnCounter() >= 2) {
                                model.swapTorAndXor();          // Swap pieces in the model
                                model.resetTurnCounter();       // Reset turn counter after swap
                            }
                        }

                        // Show the turn text
                        view.showTurnText((currentTurn == RED ? "Red" : "Blue") + "'s Turn");

                        // Check for win condition
                        if (!model.isSauPresent(GameModel.RED)) {
                            endGame("Blue");
                        } else if (!model.isSauPresent(GameModel.BLUE)) {
                            endGame("Red");
                        }

                        view.clearMovableTiles(); // Clear movable tiles after a valid move
                    } else {
                        System.out.println("Invalid move!");
                        view.clearMovableTiles(); // Clear movable tiles after an invalid move
                    }

                    // Clear the dragging state and highlight after the move
                    selectedPiece = null;
                    view.clearDraggingPiece();
                    view.clearHighlight();
                    view.repaint();
                }
            }
        };
    }

    public MouseMotionAdapter getMouseMotionListener() {
        return new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedPiece != null) {
                    Point relativePoint = SwingUtilities.convertPoint(
                            e.getComponent(),
                            e.getPoint(),
                            view.getCheckerboardPanel()
                    );
                    view.updateDraggingPiecePosition(relativePoint.x, relativePoint.y);
                    view.repaint();
                }
            }
        };
    }

    public void startGame() {
        gameThread = new Thread(this);  // Create a new thread for the game loop
        gameThread.start();             // Start the thread, which runs the `run()` method
    }

    @Override
    public void run() {
        // Calculate the time interval between frames in nanoseconds.
        // 1 second = 1,000,000,000 nanoseconds. FPS will defines how many frames should be drawn per second.
        double drawInterval = 1000000000.0 / FPS; // Time per frame (in nanoseconds) to achieve the target FPS
        double delta = 0;                         // Keeps track of how much time has passed since the last frame.
        long lastTime = System.nanoTime();        // Records the current time when the loop starts (in nanoseconds).

        // Main game loop
        while (gameThread != null) { // Keep running the loop as long as the game thread is active
            long currentTime = System.nanoTime();               // Record the current time in each iteration of the loop
            delta += (currentTime - lastTime) / drawInterval;   // Calculate the elapsed time since the last frame
            lastTime = currentTime;                             // Update `lastTime` to the current time

            if (delta >= 1) {               // If enough time has passed for one frame (delta >= 1)
                view.repaint();             // Request the game view to redraw the game board
                delta--;                    // Reduce `delta` by 1 to account for the processed frame
            }
        }
    }


    // Method to save the game state
    public void saveGame() {
        model.saveBoard();
    }

    // Method to load the game state
    public void loadGame() {
        model.loadBoard();
        currentTurn = model.getCurrentTurn(); // Update the current turn from the model
        System.out.println("Loaded currentTurn: " + (currentTurn == RED ? "Red" : "Blue"));
        
        // Show the turn text after loading
        view.showTurnText((currentTurn == RED ? "Red" : "Blue") + "'s Turn");
        
        view.repaint(); // Refresh the view after loading
    }

    public ArrayList<Piece> getPiecesList() {
        return model.getPiecesList();
    }

    // Method to handle the end of the game
    private void endGame(String winner) {
        System.out.println(winner + " wins!");
        JOptionPane.showMessageDialog(view.getFrame(), winner + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0); // Exit the game
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public GameModel getModel() {
        return model;
    }

    private List<Point> calculateMovableTiles(Piece piece) {
        List<Point> movableTiles = new ArrayList<>();
        Piece[][] board = model.getPieceArray();

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (piece.isMoveValid(piece.getRow(), piece.getCol(), row, col, board)) {
                    // Adjust for flipped board
                    if (view.isFlipped() && currentTurn == GameModel.RED) {
                        movableTiles.add(new Point(4 - col, 7 - row));
                    } else {
                        movableTiles.add(new Point(col, row));
                    }
                }
            }
        }
        return movableTiles;
    }
}