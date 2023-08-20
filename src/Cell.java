public class Cell {
    public int row;
    public int col;


    // Method 1 - Getter


    // Method 2 - Setter
    public Cell(int row, int col)
    {

        // This keyword refers to current instance itself
        this.row = row;
        this.col = col;
    }
    public void setX(int row){
        this.row = row;
    }
    public void setY(int col){
        this.col = col;
    }
    public int getX(){
        return row;
    }
    public int getY(){
        return col;
    }
}
