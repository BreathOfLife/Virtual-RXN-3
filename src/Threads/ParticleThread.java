package Threads;

import Control.Engine;
import PhysObjects.*;
import Reference.*;

public class ParticleThread extends Thread {

	Particle particle;

	public ParticleThread(Particle particle) {
		this.particle = particle;
	}

	public void run() {
		long nextGameTick = System.currentTimeMillis();
		long sleepTime = 0;
		while (Engine.isRunning()) {
			if (sleepTime >= 0) {
				try {
					ParticleThread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				if (Engine.isRunning()) {
					System.out.printf("%s running behind by %d milliseconds\n", particle.getName(), -1 * sleepTime);
				}
			}
			if (!Engine.isPaused()) update(Engine.getPhysSkipTicks());
			nextGameTick += Engine.getPhysSkipTicks();
			sleepTime = nextGameTick - System.currentTimeMillis();
		}
	}

	private void update(long realTimeSinceLastUpdate) {
		double simTime = realTimeSinceLastUpdate * Engine.getTimeMultiplier();
		Vector3D[] newStatus = PhysCalc.calcNewStatus(simTime, particle);
		particle.setPos(newStatus[0]);
		particle.setVel(newStatus[1]);
		particle.setAcc(newStatus[2]);
	}

}
