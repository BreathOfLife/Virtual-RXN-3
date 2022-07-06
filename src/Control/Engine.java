package Control;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.sun.j3d.utils.picking.PickTool;
import javax.media.j3d.Node;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

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

	private static int screenFPS = 60; //Different from physFPS since the sim is calculated faster than what is actually shown on screen to ensure accuracy
	private static boolean trailsOn = false;

	private static JFrame frame;
	private static boolean paused = false;
	private static boolean partLabelsOn = true;

	public static void main(String[] args) {
		allParticles = new ArrayList<>();
		running = true;
		setupDisplay();
		ParticleSphere.init();
		
		// For testing
		/*
		Molecule molecule = new Molecule();
		molecule.atoms.add(new Atom(6,6,6));
		ParticleCreation.create(molecule, new Vector3D());
		*/
	}

	private static void setupDisplay() {
		System.setProperty("sun.awt.noerasebackground", "true");
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setLightWeightPopupEnabled(false);

		disp = new Display();
		disp.init();
		frame = new JFrame("Virtual RXN 3");
		frame.setSize(Display.WIDTH, Display.HEIGHT);
		frame.getContentPane().add(disp);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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

	public static void setScreenFPS(int value) {
		screenFPS = value;
		
	}

	public static int getScreenFPS() {
		return screenFPS;
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
		if (partLabelsOn) {
			ParticleSphere.labelsOn();
		} else {
			ParticleSphere.labelsOff();
		}
	}
}
