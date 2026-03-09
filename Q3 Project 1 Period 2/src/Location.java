
public class Location {

	public int row;
    public int col;
    public int level;

    // This Location Constructor sets up the location
    public Location(int r, int c, int l) {
        this.row = r;
        this.col = c;
        this.level = l;
    }

    //printing corrdinates
    @Override
    public String toString() {
        return "("+ row + ", "+ col + ", " + level+ ")";
    }
}
