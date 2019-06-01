package parser;

public class Location {

    public final int pos;
    public final int row;
    public final int col;

    public Location(int pos, int row, int col) {
        this.pos = pos;
        this.row = row;
        this.col = col;
    }

    public Location(Location loc) {
        this(loc.pos, loc.row, loc.col);
    }

    @Override
    public String toString() {
        return row + ", " + col;
    }

}
