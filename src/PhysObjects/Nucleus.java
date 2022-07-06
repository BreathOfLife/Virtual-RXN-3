package PhysObjects;

import Reference.PhysCalc;
import Reference.Vector3D;

public class Nucleus extends Particle {

	private int numNeutron;
	private int numProton;
	private static double radius = 2e-12;

	public Nucleus(int numNeutron, int numProton) {
		super(radius);
		this.numNeutron = numNeutron;
		this.numProton = numProton;
		Particle.numNuclei++;
		start();

	}

	public Nucleus(int numNeutron, int numProton, Vector3D pos, Vector3D vel) {
		super(pos, vel, radius);
		this.numNeutron = numNeutron;
		this.numProton = numProton;
		Particle.numNuclei++;
		start();

	}
	
	public Nucleus(int numNeutron, int numProton, Vector3D pos, Vector3D vel, String name) {
		super(pos, vel, radius, name);
		this.numNeutron = numNeutron;
		this.numProton = numProton;
		Particle.numNuclei++;
		start();

	}

	public double getCharge() {
		return numProton * PhysCalc.e;
	}

	public double getMass() {
		return numNeutron * PhysCalc.neutronMass + numProton * PhysCalc.protonMass;
	}

	@Override
	public String defaultName() {
		return "N" + (1 + Particle.numNuclei);
	}

}
