package multidlib.universe;

import java.util.LinkedList;

import multidlib.elemental.Particle;

public class Collector implements Runnable{
	private int id;
	private Universe universe;
	private float scale;
	private LinkedList<Particle> particles;
	public Collector(int id, Universe u, float s){
		this.id = id;
		universe = u;
		scale = s;
		particles = new LinkedList<Particle>();
	}
	
	public void add(Particle p){
		synchronized(this){
			particles.add(p);
		}
	}
	
	@Override
	public void run() {	
		while(true){
			for(Particle p : particles){
				p.collect(scale);
			}
			universe.collected(id);
		}
	}
}
