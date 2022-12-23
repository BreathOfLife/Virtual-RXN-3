package PhysObjects;

import java.io.Serializable;

import Control.*;
import Reference.*;
import Threads.ParticleThread;
import VirtualObjects.*;

public abstract class Particle implements Serializable{

	private Vector3D pos, vel, acc; // m, m/s, m/s/s
	private transient ParticleThread thread;
	private transient ParticleSphere sphere;
	private transient ParticleArrow velCone, accCone;
	private double radius;
	private String name;

	public static int numElectrons;
	public static int numNuclei;
	
	public Particle(double radius) {
		this.pos = new Vector3D();
		this.vel = new Vector3D();
		this.acc = new Vector3D();
		this.radius = radius;
		this.name = defaultName();
	}

	public Particle(Vector3D pos, Vector3D vel, double radius) {
		this.pos = pos.clone();
		this.vel = vel.clone();
		this.acc = new Vector3D();
		this.radius = radius;
		this.name = defaultName();
	}
	
	public Particle(Vector3D pos, Vector3D vel, double radius, String name) {
		this.pos = pos.clone();
		this.vel = vel.clone();
		this.acc = new Vector3D();
		this.radius = radius;
		this.name = name;
	}
	
	protected abstract String defaultName();
	
	public void start() {
		Engine.getDisp().addToExistingParticles(this);
		sphere = new ParticleSphere(this);
		if (Engine.vectorsOn) {
			velCone = new ParticleArrow(this, 'v');
			accCone = new ParticleArrow(this, 'a');
		}
		thread = new ParticleThread(this);
		thread.start();
	}

	
	public Vector3D getPos() {
		return pos;
	}

	public void setPos(Vector3D pos) {
		this.pos = pos.clone();
		sphere.updatePos(pos);
		if (Engine.vectorsOn) {
			velCone.updatePos(vel);
			accCone.updatePos(acc);
		}
	}

	public Vector3D getVel() {
		return vel;
	}

	public void setVel(Vector3D vel) {
		this.vel = vel.clone();
	}

	public Vector3D getAcc() {
		return acc;
	}

	public void setAcc(Vector3D acc) {
		this.acc = acc.clone();
	}

	public abstract double getCharge();

	public abstract double getMass();

	public ParticleThread getThread() {
		return thread;
	}

	public ParticleSphere getSphere() {
		return sphere;
	}

	public double getRadius() {
		return radius;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}

	public void removeFromUniv() {
		Engine.getAllParticles().remove(this);
		sphere.delete();
		Engine.getDisp().removeFromExistingParticles(this);
		if (Engine.getDisp().getGazeObj() == this) {
			Engine.getDisp().setGazeObj(null);
		}
	}
}
