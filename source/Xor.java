import java.awt.Image;

public class Xor extends Piece {
    private int turnCounter;

    public Xor(int color, int col, int row, Image image) {
        super(color, col, row, image);
        this.turnCounter = 0;
    }

    @Override
    public boolean isMoveValid(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        if (Math.abs(toRow - fromRow) == Math.abs(toCol - fromCol) && (toRow != fromRow || toCol != fromCol)) {
            int steps = Math.abs(toRow - fromRow);
            if (steps == 0) return false; // Prevent division by zero

            int rowStep = (toRow - fromRow) / steps;
            int colStep = (toCol - fromCol) / steps;

            for (int i = 1; i < steps; i++) {
                if (board[fromRow + i * rowStep][fromCol + i * colStep] != null) {
                    return false; // Path is blocked
                }
            }
            Piece targetPiece = board[toRow][toCol];
            return targetPiece == null || targetPiece.getColor() != this.getColor(); // Can kill enemy or move to empty
        }
        return false;
    }

    @Override
    public void move(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        if (isMoveValid(fromRow, fromCol, toRow, toCol, board)) {
            Piece targetPiece = board[toRow][toCol];
            if (targetPiece != null) {
                System.out.println("Killed " + (targetPiece.getColor() == GameController.RED ? "Red" : "Blue") + " piece!");
                // Remove the target piece from the board
                board[toRow][toCol] = null;
            }
            board[toRow][toCol] = this;
            board[fromRow][fromCol] = null;
            setPosition(toCol, toRow);

            turnCounter++;
            if (turnCounter >= 2) {
                transformToTor();
            }
        } else {
            throw new IllegalArgumentException("Invalid move for Xor");
        }
    }

    private void transformToTor() {
        System.out.println("Xor transforms into Tor!");
        turnCounter = 0;
    }
}
