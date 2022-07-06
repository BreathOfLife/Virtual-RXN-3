package Reference;

import java.util.ArrayList;

import Control.Engine;
import PhysObjects.*;

public class PhysCalc {

	public static final double elecK = 8.988e9;
	public static final double magK = 1e-7;
	public static final double gravK = 6.6743e-11;
	public static final double e = 1.60218e-19;// C
	public static final double protonMass = 1.6726e-27;// kg
	public static final double neutronMass = 1.6749e-27;// kg
	public static final double electronMass = 9.1094e-31;// kg
	public static final double bohrRadius = 5.291772E-11; //m

	/**
	 * Calculates magnitude of distance from A to B squared
	 */
	public static double calcDist2(Particle partA, Particle partB) {
		double dist2 = Math.pow(partB.getPos().x - partA.getPos().x, 2)
				+ Math.pow(partB.getPos().y - partA.getPos().y, 2) + Math.pow(partB.getPos().z - partA.getPos().z, 2);
		return (dist2 > 0) ? dist2 : Math.pow(Engine.getSpacerdist(), 2); // If the particles are exactly on each others
																			// location, give a tiny bit of seperation
																			// just to get the ball rolling
	}

	/**
	 * Calculates position vector from A to B
	 */
	public static Vector3D calcRVector(Particle partA, Particle partB) {
		Vector3D r = partB.getPos().sub(partA.getPos());
		return (!r.equals(new Vector3D())) ? r : new Vector3D(Engine.getSpacerdist(), 0, 0); // If the particles are
																								// exactly on each
																								// others location, give
																								// a tiny bit of
																								// seperation just to
																								// get the ball rolling
	}

	/**
	 * Calculates electric field of particleA on particleB
	 */
	public static Vector3D calcEField(Particle partA, Particle partB) {
		if (Math.pow(calcDist2(partA, partB), 0.5) < (partA.getRadius() + partB.getRadius())) {
			return new Vector3D();
		}
		return calcRVector(partA, partB).mult(elecK * partA.getCharge() / calcDist2(partA, partB));
	}

	/**
	 * Calculates magnetic field of particleA on particleB
	 */
	public static Vector3D calcMField(Particle partA, Particle partB) {
		if (Math.pow(calcDist2(partA, partB), 0.5) < (partA.getRadius() + partB.getRadius())) {
			return new Vector3D();
		}
		return partA.getVel().crossProd(calcRVector(partA, partB))
				.mult(magK * partA.getCharge() / calcDist2(partA, partB));
	}

	/**
	 * Calculates gravitational field of particleA on particleB
	 */
	public static Vector3D calcGField(Particle partA, Particle partB) {
		if (Math.pow(calcDist2(partA, partB), 0.5) < (partA.getRadius() + partB.getRadius())) {
			return new Vector3D();
		}
		return calcRVector(partA, partB).mult(-1 * gravK * partA.getMass() / calcDist2(partA, partB));
	}

	/**
	 * Sums every electric field strength of other particles on this one to
	 * determine net field
	 */
	public static Vector3D sumEFields(Particle obsPart) {
		Vector3D netField = new Vector3D();
		ArrayList<Particle> allParts = (ArrayList<Particle>) Engine.getAllParticles().clone();
		for (Particle actingPart : allParts) {
			if (actingPart != obsPart) {
				netField = netField.add(calcEField(actingPart, obsPart));
			}
		}
		return netField;
	}

	/**
	 * Sums every magnetic field strength of other particles on this one to
	 * determine net field
	 */
	public static Vector3D sumMFields(Particle obsPart) {
		Vector3D netField = new Vector3D();
		ArrayList<Particle> allParts = (ArrayList<Particle>) Engine.getAllParticles().clone();
		for (Particle actingPart : allParts) {
			if (actingPart != obsPart) {
				netField = netField.add(calcMField(actingPart, obsPart));
			}
		}
		return netField;
	}

	/**
	 * Sums every gravitational field strength of other particles on this one to
	 * determine net field
	 */
	public static Vector3D sumGFields(Particle obsPart) {
		Vector3D netField = new Vector3D();
		ArrayList<Particle> allParts = (ArrayList<Particle>) Engine.getAllParticles().clone();
		for (Particle actingPart : allParts) {
			if (actingPart != obsPart) {
				netField = netField.add(calcGField(actingPart, obsPart));
			}
		}
		return netField;
	}

	/**
	 * Calculates forces from electric, magnetic, and gravitational sources on a
	 * particular particle
	 */
	public static Vector3D calcNetForce(Particle part) {
		Vector3D eForce = sumEFields(part).mult(part.getCharge());
		Vector3D mForce = part.getVel().mult(part.getCharge()).crossProd(sumMFields(part));
		Vector3D gForce = sumGFields(part).mult(part.getMass());
		return eForce.add(mForce).add(gForce);
	}

	public static Vector3D[] calcNewStatus(double time, Particle part) {
		Vector3D newPos = part.getPos().add(part.getVel().mult(time)).add(part.getAcc().mult(Math.pow(time, 2)));
		Vector3D newVel = part.getVel().add(part.getAcc().mult(time));
		Vector3D newAcc = calcNetForce(part).div(part.getMass());
		return new Vector3D[] { newPos, newVel, newAcc };
	}
}
