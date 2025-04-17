import java.awt.*;

//Base class for Pieces
public abstract class Piece {
    private int col;
    private int row;
    private final Image image;
    public int color;

    public Piece(int color, int col, int row, Image image) {
        this.color = color;
        this.col = col;
        this.row = row;
        this.image = image;
    }

    public int getCol() {
        return col;
    }
    
    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public Image getImage() {
        return image;
    }

    public void setPosition(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public int getColor() {
        return color;
    }

    //move method for polymorphism
    public abstract boolean isMoveValid(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board);

    public abstract void move(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board);
}
