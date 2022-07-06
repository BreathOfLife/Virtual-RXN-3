package PhysObjects;

import Reference.PhysCalc;
import Reference.Vector3D;

public class Electron extends Particle{

	private static double radius = 3e-13;
	
	public Electron() {
		super(radius);
		Particle.numElectrons++;
		start();

	}

	public Electron(Vector3D pos, Vector3D vel) {
		super(pos, vel, radius);
		Particle.numElectrons++;
		start();

	}
	
	public Electron(Vector3D pos, Vector3D vel, String name) {
		super(pos, vel, radius, name);
		Particle.numElectrons++;
		start();

	}

	public double getCharge() {
		return -1 * PhysCalc.e;
	}

	public double getMass() {
		return PhysCalc.electronMass;
	}

	@Override
	public String defaultName() {
		return "E" + (1 + Particle.numElectrons);
	}
}
