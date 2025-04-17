import java.awt.Image;

public class Biz extends Piece {

    public Biz(int color, int col, int row, Image image) {
        super(color, col, row, image);
    }

    @Override
    public boolean isMoveValid(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
            Piece targetPiece = board[toRow][toCol];
            return targetPiece == null || targetPiece.getColor() != this.getColor();
        }
        return false;
    }

    @Override
    public void move(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        if (isMoveValid(fromRow, fromCol, toRow, toCol, board)) {
            Piece targetPiece = board[toRow][toCol];
            if (targetPiece != null) {
                System.out.println("Killed " + (targetPiece.getColor() == GameController.RED ? "Red" : "Blue") + " piece!");
                board[toRow][toCol] = null;
            }
            board[toRow][toCol] = this;
            board[fromRow][fromCol] = null;
            setPosition(toCol, toRow);
        } else {
            System.out.println("Invalid move for Biz.");
        }
    }
}