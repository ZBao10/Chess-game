import java.awt.Image;

public class Ram extends Piece {
    private boolean movingForward;

    public Ram(int color, int col, int row, Image image) {
        super(color, col, row, image);
        this.movingForward = color == GameController.RED; // Red moves down, Blue moves up
    }

    @Override
    public boolean isMoveValid(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        int direction;
        if (movingForward) {
            direction = 1;
        } else {
            direction = -1;
        }

        if (toRow == fromRow + direction && toCol == fromCol) {
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

            if (toRow == 0 || toRow == board.length - 1) {
                movingForward = !movingForward;
            }
        } else {
            throw new IllegalArgumentException("Invalid move for Ram");
        }
    }
}
