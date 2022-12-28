package PhysObjects;

import Control.Engine;
import Reference.PhysCalc;
import Reference.Vector3D;

public class Electron extends Particle{

	private static double radius = 3e-13;
	private int eProbPartitions;
	
	public Electron() {
		super(radius);
		eProbPartitions = Engine.getEProbPartitions();
		Particle.numElectrons++;
		start();

	}

	public Electron(Vector3D pos, Vector3D vel) {
		super(pos, vel, radius);
		eProbPartitions = Engine.getEProbPartitions();
		Particle.numElectrons++;
		start();

	}
	
	public Electron(Vector3D pos, Vector3D vel, String name) {
		super(pos, vel, radius, name);
		eProbPartitions = Engine.getEProbPartitions();
		Particle.numElectrons++;
		start();

	}

	public double getCharge() {
		return -1 * PhysCalc.e / eProbPartitions;
	}

	public double getMass() {
		return PhysCalc.electronMass / eProbPartitions;
	}

	@Override
	public String defaultName() {
		return "E" + (1 + Particle.numElectrons);
	}

	public int getEProbPartitions() {
		return eProbPartitions;
	}
}
