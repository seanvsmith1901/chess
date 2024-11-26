package websocket.commands;

public class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    @Override
    public String toString() {
        return intToStrRow(col)  + row;
    }

    private String intToStrRow(int rowNum) {
        return switch (rowNum) {
            case 1 -> "A";
            case 2 -> "B";
            case 3 -> "C";
            case 4 -> "D";
            case 5 -> "E";
            case 6 -> "F";
            case 7 -> "G";
            case 8 -> "H";
            default ->
                // Handle invalid input (if the number is not between 1 and 8)
                    "Invalid input";
        };
    }


}
