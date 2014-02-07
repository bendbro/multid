package multidlib.elemental;

public class FloatObject {
	public int d;
	public float v;
	
	public FloatObject(int deminsion, float value){
		d = deminsion;
		v = value;
	}
	
	public FloatObject(float value){
		d = -1;
		v = value;
	}
}
