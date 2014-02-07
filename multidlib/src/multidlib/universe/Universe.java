package multidlib.universe;

import multidlib.elemental.Coordinate;
import multidlib.elemental.CoordinateTuple;
import multidlib.elemental.Particle;

import java.util.List;
import java.util.LinkedList;

public class Universe implements Runnable {
	// particles in system
	public final LinkedList<Particle> particles;
	public final LinkedList<List<Coordinate>> coordinates;
	public final LinkedList<List<CoordinateTuple>> lines;
	
	// threading
	private int aid;
	private int cid;
	boolean sync;
	private Affector[] affectors;
	private Collector[] collectors;
	private Object await;
	public int affected;
	private Object cwait;
	public int collected;
	private boolean[] arun;
	private boolean[] crun;
	private LinkedList<Thread> threads;
	
	public Universe(float s, int t, boolean sc){
		// initialize system
		particles = new LinkedList<Particle>();
		coordinates = new LinkedList<List<Coordinate>>();
		lines = new LinkedList<List<CoordinateTuple>>();
		// threading
		aid = 0;
		cid = 0;
		sync = sc;
		affectors = new Affector[t];
		collectors = new Collector[(int)Math.sqrt(t)];
		await = new Object();
		affected = 0;
		cwait = new Object();
		collected = 0;
		arun = new boolean[affectors.length];
		crun = new boolean[collectors.length];
		threads = new LinkedList<Thread>();
		for(int index = 0; index < affectors.length; index++){
			affectors[index] = new Affector(index, this);
			threads.add(new Thread(affectors[index]));
		}
		for(int index = 0; index < collectors.length; index++){
			collectors[index] = new Collector(index, this, s);
			threads.add(new Thread(collectors[index]));
		}
	}
	
	public void addParticle(Particle p){
			// add to system
			particles.addLast(p);
			coordinates.addLast(p.coordinates);
			lines.addLast(p.lines);
			// allocate threads
			getAffector().add(p);
			getCollector().add(p);
	}
	
	public Affector getAffector(){
		Affector a = affectors[aid++];
		if(aid == affectors.length){
			aid = 0;
		}
		return a;
	}
	
	public Collector getCollector(){
		Collector c = collectors[cid++];
		if(cid == collectors.length){
			cid = 0;
		}
		return c;
	}
	
	// called by an affector to indicate it has completed
	public void affected(int id){
		synchronized(await){
			while(sync && !aresume(id)){
				try {
					await.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			affected++;
		}
		synchronized(cwait){
			if(affected == affectors.length){
				collected = 0;
				csetall();
				cwait.notifyAll();
			}
		}
	}
	
	// called by a collector to indicate it has completed
	public void collected(int id){
		synchronized(cwait){
			while(sync && !cresume(id)){
				try {
					cwait.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			collected++;
		}
		synchronized(await){
			if(collected == collectors.length){
				affected = 0;
				asetall();
				await.notifyAll();
			}
		}
	}
	
	private void asetall(){
		for(int index = 0; index < arun.length; index++){
			arun[index] = true;
		}
	}
	
	private boolean aresume(int id){
		if(arun[id]){
			arun[id] = false;
			return true;
		}
		return false;
	}
	
	private void csetall(){
		for(int index = 0; index < crun.length; index++){
			crun[index] = true;
		}
	}
	
	private boolean cresume(int id){
		if(crun[id]){
			crun[id] = false;
			return true;
		}
		return false;
	}
	
	@Override
	public void run() {
		asetall();
		for(Thread t : threads){
			t.start();
		}
	}
}