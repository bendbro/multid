package multidlib.elemental;

import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;

/**
 * An n dimensional particle, the pieces needed to simulate and render it.
 * @author Ben
 */
public abstract class Particle {
	// the current location of a particle.
	// TODO: convert to HashMap<Integer,Coordinate> to allow for non dimensionally continguous particles.
	private final Coordinate[] location;
	// the adjustments to the location, accumulated after a tick.
	private final Coordinate[] adjustments;

	// the 3d representation of n dimensional particle.
	// the points of that representation.
	public final List<Position> coordinates;
	// the connections of those points (lines).
	public final List<PositionTuple> lines;
	public final List<Set<Position>> shapes;
	
	public Particle(List<Float> alocation){
		location = new Coordinate[Math.max(3, alocation.size())];
		adjustments = new Coordinate[Math.max(3, alocation.size())];
		coordinates = new LinkedList<Position>();
		lines = new LinkedList<PositionTuple>();
		shapes = new LinkedList<Set<Position>>();

		for(int index = 0; index < Math.max(alocation.size(),3); index++){
			location[index] = new Coordinate(index, alocation.get(index));
			adjustments[index] = new Coordinate(index, 0.0f);
		}
		
		permuteCoordinates();
		permuteLines();
		permuteShapes();
	}
	
	/**
	 * Implement this method to specify the interaction of particles.
	 * By convention, you affect other particles, not your own state.
	 * TODO: consider inverting this convention and enforcing it by privatizing state.
	 * @param other The particle to affect.
	 */
	public abstract void affect(Particle other);
	
	/**
	 * Calculates the distance to another particle given their common dimensions
	 * @param other
	 * @return
	 */
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
	
	/**
	 * Indicates whether a values has fucked itself.
	 * @param value The value.
	 * @return If it fucked itself.
	 */
	private static boolean isValid(Float value){
		return !value.equals(Float.NaN) && !value.equals(Float.POSITIVE_INFINITY) && !value.equals(Float.NEGATIVE_INFINITY);
	}
	
	/**
	 * Permutes the shapes of this 3d representation
	 * The 3d coordinates of a n dimensional position are defined by all combinations of it's coordinates in 3 dimensions.
	 */
	private void permuteCoordinates(){
		for(int a = 0; a < location.length; a++){
			for(int b = a + 1; b < location.length; b++){
				for(int c = b + 1; c < location.length; c++){
					coordinates.add(new Position(location[a],location[b],location[c]));
				}
			}
		}
	}
	
	/**
	 * Permutes the shapes of this 3d representation
	 * A line is defined as two positions that have a dimensional difference of 1
	 */
	private void permuteLines(){
		Position ac;
		Position bc;
		for(int a = 0; a < coordinates.size(); a++){
			for(int b = a + 1; b < coordinates.size(); b++){
				ac = coordinates.get(a);
				bc = coordinates.get(b);
				if(ac.deminsionalDifference(bc) == 1){
					lines.add(new PositionTuple(ac, bc));
				}
			}
		}
	}
	
	/**
	 * Permutes the shapes of this 3d representation
	 * The face of a shape is defined by any set of positions that have a dimensional difference of 1.
	 */
	private void permuteShapes(){
		HashSet<Position> shape;
		for(Position anchor: coordinates){
			shape = new HashSet<Position>();
			shape.add(anchor);
			for(Position other: coordinates){
				if(anchor.deminsionalDifference(other) == 1){
					shape.add(other);
				}
			}
			shapes.add(shape);
		}
	}
}
