package multidlib.elemental;

import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;

public abstract class Particle {
	// system
	private final FloatObject[] location;
	private final FloatObject[] adjustments;
	// cached
	public final List<Coordinate> coordinates;
	public final List<CoordinateTuple> lines;
	public final List<Set<Coordinate>> shapes;
	
	public Particle(List<Float> alocation){
		location = new FloatObject[Math.max(3, alocation.size())];
		adjustments = new FloatObject[Math.max(3, alocation.size())];
		coordinates = new LinkedList<Coordinate>();
		lines = new LinkedList<CoordinateTuple>();
		shapes = new LinkedList<Set<Coordinate>>();

		for(int index = 0; index < alocation.size(); index++){
			location[index] = new FloatObject(index, alocation.get(index));
			adjustments[index] = new FloatObject(0.0f);
		}
		for(int index = alocation.size(); index < 3; index++){
			location[index] = new FloatObject(index, 0.0f);
			adjustments[index] = new FloatObject(index, 0.0f);
		}
		permuteCoordinates();
		permuteLines();
		permuteShapes();
	}
	
	// implement this in a derived class, this moves other
	public abstract void affect(Particle other);
	
	// calculates the distance to another particle
	public float distanceTo(Particle other){
		float distance = 0.0f;
		for(int index = 0; index < Math.min(location.length, other.location.length); index++){
			distance += (location[index].v - other.location[index].v) * (location[index].v - other.location[index].v);
		}
		return (float) Math.sqrt(distance);
	}
	
	// performs a quick check to see in a particle is in range and not at 0 distance
	public boolean inRange(float range, Particle other){
		float distance = 0.0f;
		for(int index = 0; index < Math.min(location.length, other.location.length); index++){
			distance += (location[index].v - other.location[index].v) * (location[index].v - other.location[index].v);
		}
		return range * range >= distance && distance != 0f;
	}
	
	// calculates the vector to another particle
	public List<Float> vectorTo(Particle other){
		LinkedList<Float> vectors = new LinkedList<Float>();
		for(int index = 0; index < Math.min(location.length, other.location.length); index++){
			vectors.addLast(other.location[index].v - location[index].v);
		}
		return vectors;
	}
	
	// forces this particle along the vector
	public void force(float force, List<Float> vector){
		synchronized(this){
			int i = 0;
			for(Float v : vector){
				if(isValid(v * force)){
					adjustments[i++].v += (v * force);
				}
			}
		}
	}
	
	// aggregates all forces acted on this particle
	public void collect(float scale){
		synchronized(this){
			for(int index = 0; index < location.length; index++){
				if(isValid(location[index].v + (adjustments[index].v * scale))){
					location[index].v += (adjustments[index].v * scale);
				}
				adjustments[index].v = 0.0f;
			}
		}
	}
	
	private static boolean isValid(Float value){
		return !value.equals(Float.NaN) && !value.equals(Float.POSITIVE_INFINITY) && !value.equals(Float.NEGATIVE_INFINITY);
	}
	
	private void permuteCoordinates(){
		for(int a = 0; a < location.length; a++){
			for(int b = a + 1; b < location.length; b++){
				for(int c = b + 1; c < location.length; c++){
					coordinates.add(new Coordinate(location[a],location[b],location[c]));
				}
			}
		}
	}
	
	private void permuteLines(){
		Coordinate ac;
		Coordinate bc;
		for(int a = 0; a < coordinates.size(); a++){
			for(int b = a + 1; b < coordinates.size(); b++){
				ac = coordinates.get(a);
				bc = coordinates.get(b);
				if(ac.deminsionalDifference(bc) == 1){
					lines.add(new CoordinateTuple(ac, bc));
				}
			}
		}
	}
	
	private void permuteShapes(){
		HashSet<Coordinate> shape;
		for(Coordinate anchor: coordinates){
			shape = new HashSet<Coordinate>();
			shape.add(anchor);
			for(Coordinate other: coordinates){
				if(anchor.deminsionalDifference(other) == 1){
					shape.add(other);
				}
			}
			shapes.add(shape);
		}
	}
}
