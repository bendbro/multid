package multidlib.elemental;

/**
 * @author Ben
 * Represents the combination of any 3 dimensions for visualization.
 */
public class Position {	
	public final Coordinate a;
	public final Coordinate b;
	public final Coordinate c;
	
	public Position(Coordinate aa, Coordinate ba, Coordinate ca){
		a = aa;
		b = ba;
		c = ca;
	}
	
	/**
	 * Returns the number of dimension that are not the same.
	 * @param other The coordinate to compare to.
	 * @return The difference.
	 */
	public int deminsionalDifference(Position other){
		int difference = 3;
		if(a.d == other.a.d || a.d == other.b.d || a.d == other.c.d){
			difference--;
		}
		if(b.d == other.a.d || b.d == other.b.d || b.d == other.c.d){
			difference--;
		}
		if(c.d == other.a.d || c.d == other.b.d || c.d == other.c.d){
			difference--;
		}
		return difference;
	}
}