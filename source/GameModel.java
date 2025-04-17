import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

public class GameModel {
    private ArrayList<Piece> piecesList;
    private int turnCounter = 0; // Track full turns
    private int currentTurn; // Track the current turn

    public static final int RED = 0;
    public static final int BLUE = 1;

    // Flipped images
    private Image flipRedRamImage;
    private Image flipRedSauImage;
    private Image flipBlueRamImage;
    private Image flipBlueSauImage;

    public GameModel() {
        piecesList = new ArrayList<>();
        initializePieces();
        currentTurn = BLUE; // Initialize the current turn
    }

    private void initializePieces() {
        // Load normal images
        Image RedTorImage = new ImageIcon(getClass().getResource("/Pic/RTor.png")).getImage();
        Image RedBizImage = new ImageIcon(getClass().getResource("/Pic/RBiz.png")).getImage();
        Image RedSauImage = new ImageIcon(getClass().getResource("/Pic/RSau.png")).getImage();
        Image RedRamImage = new ImageIcon(getClass().getResource("/Pic/RRam.png")).getImage();
        Image RedXorImage = new ImageIcon(getClass().getResource("/Pic/RXor.png")).getImage();
        Image BlueTorImage = new ImageIcon(getClass().getResource("/Pic/BTor.png")).getImage();
        Image BlueBizImage = new ImageIcon(getClass().getResource("/Pic/BBiz.png")).getImage();
        Image BlueSauImage = new ImageIcon(getClass().getResource("/Pic/BSau.png")).getImage();
        Image BlueRamImage = new ImageIcon(getClass().getResource("/Pic/BRam.png")).getImage();
        Image BlueXorImage = new ImageIcon(getClass().getResource("/Pic/BXor.png")).getImage();
    
        // Load flipped images
        flipRedRamImage = new ImageIcon(getClass().getResource("/Pic/FlipRRam.png")).getImage();
        flipRedSauImage = new ImageIcon(getClass().getResource("/Pic/FlipRSau.png")).getImage();
        flipBlueRamImage = new ImageIcon(getClass().getResource("/Pic/FlipBRam.png")).getImage();
        flipBlueSauImage = new ImageIcon(getClass().getResource("/Pic/FlipBSau.png")).getImage();
    
        // Use normal images for initial positions
        // Red Side pieces
        piecesList.add(new Tor(0, 0, 0, RedTorImage));
        piecesList.add(new Biz(0, 1, 0, RedBizImage));
        piecesList.add(new Sau(0, 2, 0, RedSauImage));
        piecesList.add(new Biz(0, 3, 0, RedBizImage));
        piecesList.add(new Xor(0, 4, 0, RedXorImage));
        piecesList.add(new Ram(0, 0, 1, RedRamImage));
        piecesList.add(new Ram(0, 1, 1, RedRamImage));
        piecesList.add(new Ram(0, 2, 1, RedRamImage));
        piecesList.add(new Ram(0, 3, 1, RedRamImage));
        piecesList.add(new Ram(0, 4, 1, RedRamImage));
    
        // Blue Side pieces
        piecesList.add(new Ram(1, 0, 6, BlueRamImage));
        piecesList.add(new Ram(1, 1, 6, BlueRamImage));
        piecesList.add(new Ram(1, 2, 6, BlueRamImage));
        piecesList.add(new Ram(1, 3, 6, BlueRamImage));
        piecesList.add(new Ram(1, 4, 6, BlueRamImage));
        piecesList.add(new Tor(1, 0, 7, BlueTorImage));
        piecesList.add(new Biz(1, 1, 7, BlueBizImage));
        piecesList.add(new Sau(1, 2, 7, BlueSauImage));
        piecesList.add(new Biz(1, 3, 7, BlueBizImage));
        piecesList.add(new Xor(1, 4, 7, BlueXorImage));
    }
    

    public ArrayList<Piece> getPiecesList() {
        return piecesList;
    }

    public boolean movePiece(int index, int newCol, int newRow) {
        if (index >= 0 && index < piecesList.size()) {
            Piece piece = piecesList.get(index);
            if (!piece.isMoveValid(piece.getRow(), piece.getCol(), newRow, newCol, getPieceArray())) {
                System.out.println("Invalid move for this piece!");
                return false;
            }
            if (newCol < 0 || newCol >= 5 || newRow < 0 || newRow >= 8) {
                System.out.println("Move out of bounds!");
                return false;
            }
            Piece targetPiece = null;
            for (Piece otherPiece : piecesList) {
                if (otherPiece.getCol() == newCol && otherPiece.getRow() == newRow) {
                    if (otherPiece.getColor() == piece.getColor()) {
                        System.out.println("Position already occupied by your own piece!");
                        return false;
                    }
                    // If the position is occupied by an enemy piece, mark it for removal
                    targetPiece = otherPiece;
                    break;
                }
            }
            // Remove the enemy piece from the list
            if (targetPiece != null) {
                piecesList.remove(targetPiece);
            }
            piece.setPosition(newCol, newRow);
            return true;
        }
        return false;
    }

    public Piece[][] getPieceArray() {
        Piece[][] board = new Piece[8][5];
        for (Piece piece : piecesList) {
            board[piece.getRow()][piece.getCol()] = piece;
        }
        return board;
    }

    public void incrementTurnCounter() {
        turnCounter++;
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public void resetTurnCounter() {
        turnCounter = 0;
    }

    public void swapTorAndXor() {
        Image RedTorImage = new ImageIcon(getClass().getResource("/Pic/RTor.png")).getImage();
        Image RedXorImage = new ImageIcon(getClass().getResource("/Pic/RXor.png")).getImage();
        Image BlueTorImage = new ImageIcon(getClass().getResource("/Pic/BTor.png")).getImage();
        Image BlueXorImage = new ImageIcon(getClass().getResource("/Pic/BXor.png")).getImage();

        for (int i = 0; i < piecesList.size(); i++) {
            Piece piece = piecesList.get(i);
            if (piece instanceof Tor) {
                Image newImage = (piece.getColor() == GameController.RED) ? RedXorImage : BlueXorImage;
                piecesList.set(i, new Xor(piece.getColor(), piece.getCol(), piece.getRow(), newImage));
            } else if (piece instanceof Xor) {
                Image newImage = (piece.getColor() == RED) ? RedTorImage : BlueTorImage;
                piecesList.set(i, new Tor(piece.getColor(), piece.getCol(), piece.getRow(), newImage));
            }
        }
    }

    public void saveBoard() {
        String filename = "save.txt";  // Use default filename
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Save turnCounter and currentTurn
            writer.write(turnCounter + "," + currentTurn);
            writer.newLine();
            System.out.println("Saving: turnCounter=" + turnCounter + ", currentTurn=" + (currentTurn == RED ? "Red" : "Blue"));

            // Save the pieces
            for (Piece piece : piecesList) {
                writer.write(piece.getClass().getSimpleName() + "," +
                             piece.getColor() + "," +
                             piece.getCol() + "," +
                             piece.getRow());
                writer.newLine();
            }
            System.out.println("Game saved successfully to " + filename + "!");
        } catch (IOException e) {
            System.out.println("Failed to save the game: " + e.getMessage());
        }
    }

    public void loadBoard() {
        String filename = "save.txt";  // Use default filename
        piecesList.clear(); // Clear current board
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;

            // Read turnCounter and currentTurn from the first line
            line = reader.readLine();
            if (line != null) {
                String[] gameState = line.split(",");
                turnCounter = Integer.parseInt(gameState[0]);  // Load turnCounter
                currentTurn = Integer.parseInt(gameState[1]);  // Load currentTurn
                System.out.println("Loading: turnCounter=" + turnCounter + ", currentTurn=" + (currentTurn == RED ? "Red" : "Blue"));
            }

            // Read the pieces
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String type = parts[0];
                int color = Integer.parseInt(parts[1]);
                int col = Integer.parseInt(parts[2]);
                int row = Integer.parseInt(parts[3]);

                Image image;
                switch (type) {
                    case "Tor":
                        image = new ImageIcon(getClass().getResource(color == RED ? "/Pic/RTor.png" : "/Pic/BTor.png")).getImage();
                        piecesList.add(new Tor(color, col, row, image));
                        break;
                    case "Biz":
                        image = new ImageIcon(getClass().getResource(color == RED ? "/Pic/RBiz.png" : "/Pic/BBiz.png")).getImage();
                        piecesList.add(new Biz(color, col, row, image));
                        break;
                    case "Sau":
                        image = new ImageIcon(getClass().getResource(color == RED ? "/Pic/RSau.png" : "/Pic/BSau.png")).getImage();
                        piecesList.add(new Sau(color, col, row, image));
                        break;
                    case "Xor":
                        image = new ImageIcon(getClass().getResource(color == RED ? "/Pic/RXor.png" : "/Pic/BXor.png")).getImage();
                        piecesList.add(new Xor(color, col, row, image));
                        break;
                    case "Ram":
                        image = new ImageIcon(getClass().getResource(color == RED ? "/Pic/RRam.png" : "/Pic/BRam.png")).getImage();
                        piecesList.add(new Ram(color, col, row, image));
                        break;
                }
            }
            System.out.println("Game loaded successfully from " + filename + "!");
        } catch (IOException e) {
            System.out.println("Failed to load the game: " + e.getMessage());
        }
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public boolean isSauPresent(int color) {
        for (Piece piece : piecesList) {
            if (piece instanceof Sau && piece.getColor() == color) {
                return true;
            }
        }
        return false;
    }

    // Getter methods for flipped images
    public Image getFlipRedRamImage() {
        return flipRedRamImage;
    }

    public Image getFlipRedSauImage() {
        return flipRedSauImage;
    }

    public Image getFlipBlueRamImage() {
        return flipBlueRamImage;
    }

    public Image getFlipBlueSauImage() {
        return flipBlueSauImage;
    }
}

