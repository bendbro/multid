package multidlib.universe;

import java.util.LinkedList;

import multidlib.elemental.Particle;

public class Affector implements Runnable {
	private int id;
	private Universe universe;
	private LinkedList<Particle> particles;

	public Affector(int id, Universe u){
		this.id = id;
		universe = u;
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
				for(Particle o : universe.particles){
					p.affect(o);
				}
			}
			universe.affected(id);
		}
	}
}
