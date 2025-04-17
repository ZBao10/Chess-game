import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;

public class GameView {
    private final int tileSize = 85;    // Tile size for the checkerboard
    private final int cols = 5;         // Number of columns
    private final int rows = 8;         // Number of rows
    private final JFrame frame;
    private final JPanel checkerboardPanel;
    private final JLabel turnLabel;     // Label to display the turn

    private int highlightedCol = -1;    // Column of the highlighted tile
    private int highlightedRow = -1;    // Row of the highlighted tile

    private Piece draggingPiece = null; // The piece being dragged
    private int draggingX = 0;          // Current X position of the dragged piece
    private int draggingY = 0;          // Current Y position of the dragged piece

    private GameController controller;

    private boolean isFlipped = false;  // Track the flip state

    private List<Point> movableTiles = new ArrayList<>(); // List to store movable tiles

    public GameView(GameController controller) {
        this.controller = controller;
    
        // Create the main frame
        frame = new JFrame("Checkerboard Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        // Set the minimum size of the window
        frame.setMinimumSize(new Dimension(900, 1050)); // Minimum size 
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizes the window to cover the screen
        frame.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds()); // Use full screen size excluding taskbar
    
        frame.setVisible(true);
    
        // Create a container to center the checkerboard
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(Color.LIGHT_GRAY);
    
        // Create the checkerboard panel
        checkerboardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawCheckerboard(g);
                drawHighlight(g);
                drawPieces(g);
                drawDraggingPiece(g); // Draw the dragged piece on top
            }
        };
    
        // Set the size of the checkerboard based on tiles
        checkerboardPanel.setPreferredSize(new Dimension(cols * tileSize, rows * tileSize));
        container.add(checkerboardPanel, new GridBagConstraints());
        frame.add(container);
    
        // Create buttons for saving, loading, and flipping
        JButton saveButton = new JButton("Save Game");
        JButton loadButton = new JButton("Load Game");
        JToggleButton flipButton = new JToggleButton("Flip OFF"); // Toggle button for flipping
    
        // Add action listeners for the buttons
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.saveGame();
            }
        });
    
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.loadGame();
            }
        });
    
        flipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (flipButton.isSelected()) {
                    flipButton.setText("Flip ON");
                    isFlipped = true;
                } else {
                    flipButton.setText("Flip OFF");
                    isFlipped = false;
                }
                repaint(); // Repaint to reflect the flip state
            }
        });
    
        // Create a panel for buttons and add them
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(flipButton); // Add the flip button
    
        // Add the button panel to the frame
        frame.add(buttonPanel, BorderLayout.SOUTH);
    
        // Add key listener for saving and loading
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_S) { // Press 'S' to save
                    controller.saveGame();
                } else if (e.getKeyCode() == KeyEvent.VK_L) { // Press 'L' to load
                    controller.loadGame();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { // Press 'ESC' to exit the application
                    frame.dispose();
                    System.exit(0);
                }
            }
        });
    
        // Initialize the turn label
        turnLabel = new JLabel("", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 48)); // Large font size
        turnLabel.setForeground(Color.BLACK); // Fully opaque text
        turnLabel.setBounds(0, 0, frame.getWidth(), 100); // Position at the top
        frame.add(turnLabel, BorderLayout.NORTH);
    
        frame.setVisible(true); // Show the frame
        
        // Remove logic from view, delegate to controller
        checkerboardPanel.addMouseListener(controller.getMouseListener());
        checkerboardPanel.addMouseMotionListener(controller.getMouseMotionListener());
    }
    
    private void drawCheckerboard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g2d.setColor((r + c) % 2 == 0 ? Color.WHITE : Color.BLACK);
                g2d.fillRect(c * tileSize, r * tileSize, tileSize, tileSize);
            }
        }
    }

    private void drawHighlight(Graphics g) {
        // Highlight the selected tile
        if (highlightedCol != -1 && highlightedRow != -1) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(255, 255, 0, 128)); // Semi-transparent yellow
            g2d.fillRect(highlightedCol * tileSize, highlightedRow * tileSize, tileSize, tileSize);
        }

        // Highlight movable tiles
        for (Point tile : movableTiles) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(255, 255, 0, 128)); // Semi-transparent yellow
            g2d.fillRect(tile.x * tileSize, tile.y * tileSize, tileSize, tileSize);
        }
    }

    private void drawPieces(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (Piece piece : controller.getPiecesList()) {
            if (piece != draggingPiece && piece.getImage() != null) {
                int col = piece.getCol();
                int row = piece.getRow();
                Image image = piece.getImage(); // Get the default image of the piece
    
                // Flip position and update image if the board is flipped
                if (shouldFlipBoard()) {
                    col = cols - 1 - col;
                    row = rows - 1 - row;
    
                    // Replace the image with the flipped version if necessary
                    if (piece instanceof Ram && piece.getColor() == GameModel.RED) {
                        image = controller.getModel().getFlipRedRamImage();
                    } else if (piece instanceof Ram && piece.getColor() == GameModel.BLUE) {
                        image = controller.getModel().getFlipBlueRamImage();
                    } else if (piece instanceof Sau && piece.getColor() == GameModel.RED) {
                        image = controller.getModel().getFlipRedSauImage();
                    } else if (piece instanceof Sau && piece.getColor() == GameModel.BLUE) {
                        image = controller.getModel().getFlipBlueSauImage();
                    }
                    // Add other piece types here if needed
                }
    
                // Draw the piece
                g2d.drawImage(image, col * tileSize + 10, row * tileSize + 10, tileSize - 20, tileSize - 20, null);
            }
        }
    }
    
    private void drawDraggingPiece(Graphics g) {
        if (draggingPiece != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)); // Semi-transparent

            Image image = draggingPiece.getImage(); // Default image

            // Check if the board is flipped and use the flipped image if necessary
            if (shouldFlipBoard()) {
                if (draggingPiece instanceof Ram && draggingPiece.getColor() == GameModel.RED) {
                    image = controller.getModel().getFlipRedRamImage();
                } else if (draggingPiece instanceof Ram && draggingPiece.getColor() == GameModel.BLUE) {
                    image = controller.getModel().getFlipBlueRamImage();
                } else if (draggingPiece instanceof Sau && draggingPiece.getColor() == GameModel.RED) {
                    image = controller.getModel().getFlipRedSauImage();
                } else if (draggingPiece instanceof Sau && draggingPiece.getColor() == GameModel.BLUE) {
                    image = controller.getModel().getFlipBlueSauImage();
                }
                // Add other piece types here if needed
            }

            g2d.drawImage(image, draggingX - tileSize / 2, draggingY - tileSize / 2, tileSize - 20, tileSize - 20, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }

    public void highlightTile(int col, int row) {
        highlightedCol = col;
        highlightedRow = row;
    }

    public void clearHighlight() {
        highlightedCol = -1;
        highlightedRow = -1;
        repaint(); // Force the view to update and remove the highlight
    }

    public void setDraggingPiece(Piece piece, int x, int y) {
        draggingPiece = piece;
        draggingX = x;
        draggingY = y;
    }

    public void clearDraggingPiece() {
        draggingPiece = null;
    }

    public void repaint() {
        frame.repaint();
    }

    public JFrame getFrame() {
        return frame;
    }

    public JPanel getCheckerboardPanel() {
        return checkerboardPanel;
    }

    public void updateDraggingPiecePosition(int x, int y) {
        draggingX = x;
        draggingY = y;

        // Calculate the closest tile based on the dragging position
        int closestCol = Math.max(0, Math.min(cols - 1, draggingX / tileSize));
        int closestRow = Math.max(0, Math.min(rows - 1, draggingY / tileSize));

        highlightTile(closestCol, closestRow);
    }

    // Method to display the turn text
    public void showTurnText(String text) {
        turnLabel.setText(text);
        turnLabel.setForeground(Color.BLACK); // Ensure text is fully opaque
    }

    public boolean isFlipped() {
        return isFlipped;
    }

    // Method to determine if the board should be flipped
    private boolean shouldFlipBoard() {
        return isFlipped && controller.getCurrentTurn() == GameModel.RED;
    }

    public void setMovableTiles(List<Point> tiles) {
        this.movableTiles = tiles;
        repaint(); // Repaint to show the movable tiles
    }

    public void clearMovableTiles() {
        this.movableTiles.clear();
        repaint(); // Repaint to clear the movable tiles
    }
}
