public class Location {

	public int row;
    public int col;
    public int level;
    public Location previous; // Keeps track of the previous location we visited for 1 thing it does

    // This Location Constructor sets up the location
    public Location(int r, int c, int l,Location p) {
        this.row = r;
        this.col = c;
        this.level = l;
        this.previous = p;
    }

    //printing corrdinates
    @Override
    public String toString() {
        return "("+ row + ", "+ col + ", " + level+ ")";
    }
}
