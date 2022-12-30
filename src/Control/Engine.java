package Control;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.sun.j3d.utils.picking.PickTool;
import javax.media.j3d.Node;
import javax.swing.*;

import com.sun.j3d.utils.geometry.*;

import PhysObjects.*;
import PhysTree.Atom;
import PhysTree.Molecule;
import Reference.Vector3D;
import VirtualObjects.ParticleSphere;

public class Engine {

	private static final double spacerDist = 1e-11; // If the particles are exactly on each others location, give a tiny

	// bit of separation just to get the ball rolling
	private static ArrayList<Particle> allParticles;
	private static double timeMultiplier = 1e-15; // The amount of time that should be processed in-sim for every one
													// second in the real world (Should be 1e-8?) 1e-15 seems to work well though
	private static boolean running = false;
	private static Display disp;
	private static int physFPS = 60; //Usually 60
	private static int eProbPartitions = 1; //Number of different virtual electrons that are simulated for each real electron in an effort to mimic probability fields

	private static int screenFPS = 60; //Different from physFPS since the sim is calculated faster than what is actually shown on screen to ensure accuracy
	private static boolean trailsOn = false;

	private static JFrame frame;
	private static boolean paused = false;
	private static boolean partLabelsOn = true;
	private static boolean labelsSubatomic = true;

	private static boolean debugRunningBehindOn = false;
	public static boolean vectorsOn = false;
	private static int maxTrailLength = 100; //Adjust this if trails are closing off too early
	private static int trailCollectionDelay = -1;
	//1 indicates collecting a trail point at every frame, 2 indicates every other frame, and so on
	//-1 indicates dynamic collection which starts at 1 and increases as the trail reaches max lengths

	public static void main(String[] args) {
		allParticles = new ArrayList<>();
		running = true;
		setupDisplay();
		ParticleSphere.init();
	}

	private static void setupDisplay() {
		FlatDarculaLaf.setup();

		System.setProperty("sun.awt.noerasebackground", "true");
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setLightWeightPopupEnabled(false);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		disp = new Display();
		disp.init();
		frame = new JFrame("Virtual RXN 3");
		Display.WIDTH = (int) (screenSize.getWidth() * 0.9);
		Display.HEIGHT = (int) (screenSize.getWidth() * 0.5);
		frame.setSize(Display.WIDTH, Display.HEIGHT);
		frame.getContentPane().add(disp);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		WindowAdapter exitListener = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int confirm = JOptionPane.showOptionDialog(null, "Are You Sure to Close Application?",
						"Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (confirm == 0) {
					close();
				}
			}
		};
		frame.addWindowListener(exitListener);
		frame.setSize(Display.WIDTH+2,Display.HEIGHT);
	}

	public static double getTimeMultiplier() {
		return timeMultiplier;
	}

	public static void setTimeMultiplier(double timeMultiplier) {
		Engine.timeMultiplier = timeMultiplier;
	}

	public static ArrayList<Particle> getAllParticles() {
		return allParticles;
	}

	public static double getSpacerdist() {
		return spacerDist;
	}

	public static boolean isRunning() {
		return running;
	}

	public static int getPhysSkipTicks() {
		return 1000 / physFPS;
	}

	public static void close() {
		frame.dispose();
		running = false;
		for (Particle particle : allParticles) {
			try {
				particle.getThread().join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

	public static Display getDisp() {
		return disp;
	}

	public static JFrame getFrame() {
		return frame;
	}
	
	public static boolean trailsOn() {
		return trailsOn;
	}

	public static int getScreenUpdateTicks() {
		return 1000 / screenFPS;
	}
	
	public static Particle findParticleByName(String name) {
		for (Particle part : allParticles) {
			if (part.getName().equals(name)) {
				return part;
			}
		}
		return null;
	}

	public static void setTrailsOn(boolean trailsOn) {
		Engine.trailsOn = trailsOn;
		
	}

	public static void clearParticleTrails() {
		ParticleSphere.setHaltTrails(true);
		for (Particle part : allParticles) {
			part.getSphere().deleteTrails();
		}
		ParticleSphere.setHaltTrails(false);
	}

	public static void setPhysFPS(int value) {
		physFPS = value;
	}

	public static int getPhysFPS() {
		return physFPS;
	}
	
	public static void resetDisp() {
		disp.reset();
	}

	public static void resetParticles() {
		allParticles.clear();
	}

	public static void setAllParticles(ArrayList<Particle> readFromFile) {
		allParticles = readFromFile;
	}

	public static void startAllParticles() {
		for (Particle particle : allParticles) {
			particle.start();
		}
	}

	public static void togglePause() {
		paused = !paused;
	}

	public static boolean isPaused() {
		return paused;
	}
	
	public static void toggleShowPartLabels() {
		partLabelsOn = !partLabelsOn;
		ParticleSphere.updateLabelVisibility();
	}

	public static boolean isLabelsSubatomic() {
		return labelsSubatomic;
	}

	public static boolean isPartLabelsOn() {
		return partLabelsOn;
	}

	public static void setLabelsSubatomic(boolean b) {
		labelsSubatomic = b;
		ParticleSphere.updateLabelVisibility();
	}

	public static boolean isDebugRunningBehindOn() {
		return debugRunningBehindOn;
	}

	public static int getEProbPartitions() {
		return eProbPartitions;
	}

	public static void setEProbPartitions(int value) {
		eProbPartitions = value;
	}

	public static int getMaxTrailLength() {
		return maxTrailLength;
	}

	public static int getTrailCollectionDelay() {
		return trailCollectionDelay;
	}
}
