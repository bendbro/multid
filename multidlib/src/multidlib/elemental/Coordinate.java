package multidlib.elemental;

public class Coordinate {	
	public final FloatObject a;
	public final FloatObject b;
	public final FloatObject c;
	
	public Coordinate(FloatObject aa, FloatObject ba, FloatObject ca){
		a = aa;
		b = ba;
		c = ca;
	}
	
	public Coordinate(float aa, float ba, float ca){
		a = new FloatObject(aa);
		b = new FloatObject(ba);
		c = new FloatObject(ca);
	}
	
	public int deminsionalDifference(Coordinate other){
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