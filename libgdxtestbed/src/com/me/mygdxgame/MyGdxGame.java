package com.me.mygdxgame;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import multidlib.elemental.Coordinate;
import multidlib.elemental.CoordinateTuple;
import multidlib.elemental.Particle;
import multidlib.universe.Universe;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class MyGdxGame implements ApplicationListener {
	// important note: add multidlib.jar to android's libs folder
	// check to make sure in private dependencies, multidlib is addded along with libgdx stuff
	// make sure it is not added to the build path in android.
	
    public PerspectiveCamera cam;
    public ShapeRenderer renderer;
    public LinkedList<Color> colors;
    public Universe universe;
    public Thread engine;
    
    public BitmapFont font;
    public SpriteBatch batch;

    @Override
    public void create() {
    	font = new BitmapFont();
    	batch = new SpriteBatch();
    	
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(5f,5f,5f);
        cam.near = 0.1f;
        cam.far = 300f;
        cam.update();
        
        renderer = new ShapeRenderer();
    	renderer.setProjectionMatrix(cam.combined);
        
        int threads = 4;
        int particles = 4;
        int dimensions = 4;
        float scale = .0001f;
        boolean sync = true;
        universe = new Universe(scale, threads, sync);
        Random random = new Random();
        colors = new LinkedList<Color>();
        LinkedList<Float> location;
        for(int count = 0; count < particles; count++){
        	location = new LinkedList<Float>();;
        	for(int dim = 0; dim < dimensions; dim++){
        		location.add(random.nextFloat() * 10.0f);
        	}
            universe.addParticle(new StandardParticle(location));
            colors.add(new Color(random.nextInt()));
        }
        engine = new Thread(universe);
        engine.start();
    }

    @Override
    public void render() {
    	
	    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	    
		renderer.begin(ShapeType.Line);;
		//lines
		int color = 0;
	    for(List<CoordinateTuple> tuples : universe.lines){
	    	renderer.setColor(colors.get(color++));
	    	for(CoordinateTuple t : tuples){
	    		renderer.line(t.one.a.v, t.one.b.v, t.one.c.v, t.two.a.v, t.two.b.v, t.two.c.v);
	    	}
	    }
	    //points
	    color = 0;
	    for(List<Coordinate> coordinates : universe.coordinates){
	    	renderer.setColor(colors.get(color++));
	    	for(Coordinate c: coordinates){
	    		renderer.box(c.a.v, c.b.v, c.c.v, 0.25f, 0.25f, 0.25f);
	    	}
	    }
	    renderer.end();
	    
	    batch.begin();
	    font.draw(batch,"Affected: "+universe.affected+", Collected: "+universe.collected, 10.0f, 20.0f);
	    batch.end();
    }
    
    @Override
    public void dispose() {
        renderer.dispose();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}

class StandardParticle extends Particle{
	public StandardParticle(List<Float> alocation){
		super(alocation);
	}

	@Override
	public void affect(Particle other) {
		if(!inRange(Float.MAX_VALUE, other)){
			return;
		}
		
		List<Float> vector = vectorTo(other);
		float sum = 0.0f;
		for(Float f : vector){
			sum += Math.abs(f);
		}

		other.force(1f / sum * (6f * 6f / distanceTo(other) - 6f) , vectorTo(other));
	}
}
