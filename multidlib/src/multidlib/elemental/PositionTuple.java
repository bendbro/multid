package multidlib.elemental;

/**
 * @author Ben
 * Represents a pair of positions (a line).
 */
public class PositionTuple {
	public final Position one;
	public final Position two;
	
	public PositionTuple(Position o, Position t){
		one = o;
		two = t;
	}
}
