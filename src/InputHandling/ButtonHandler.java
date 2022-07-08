package InputHandling;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import Control.Engine;
import Control.FileIO;
import Control.ParticleCreation;
import Control.ParticleDesigner;
import PhysObjects.Nucleus;
import PhysObjects.Particle;
import PhysTree.Atom;
import PhysTree.Molecule;

public class ButtonHandler implements ActionListener {


	@Override
	public void actionPerformed(ActionEvent e) {
		String[] splitCommand = e.getActionCommand().split("-");
		switch(splitCommand[0]) {
			case "F":
				switch (splitCommand[1]) {
					case "New Scene":
						//Create a new scene
						FileIO.newScene();
						break;
					case "Open Scene":
						//Open a particular scene
						FileIO.openScene();
						break;
					case "Save Scene":
						//Save the current scene
						FileIO.saveScene();
						break;
					case "Save Scene As":
						//Save the current scene to a particular name
						FileIO.saveSceneAs();
						break;
					default:
						System.out.println("Error: Unregistered Action Command: " + e.getActionCommand());
				}
				break;
			case "P":
				//Common for all particle creation
				Molecule molecule = new Molecule();
				switch (splitCommand[1]) {
					case "S":
						switch (splitCommand[2]) {
							case "Proton":
								//Create a new proton
								molecule.atoms.add(new Atom(1,0,0));
								break;
							case "Electron":
								//Create a new Electron
								molecule.atoms.add(new Atom(0,0,1));
								break;
							case "Neutron":
								//Create a new neutron
								molecule.atoms.add(new Atom(0,1,0));
								break;
							default:
								System.out.println("Error: Unregistered Action Command: " + e.getActionCommand());
						}
						break;
					case "A":
						switch (splitCommand[2]) {
							case "H":
								//Create a new Hydrogen
								molecule.atoms.add(new Atom(1,0,0));
								break;
							case "C":
								//Create a new Carbon
								molecule.atoms.add(new Atom(6,6,6));
								break;
							case "O":
								//Create a new Oxygen
								molecule.atoms.add(new Atom(8,8,8));
								break;
							case "N":
								//Create a new Nitrogen
								molecule.atoms.add(new Atom(7,7,7));
								break;
							case "Custom":
								//Create a custom atom
								SwingUtilities.invokeLater(() -> ParticleDesigner.singleAtom());
								break;
							default:
								System.out.println("Error: Unregistered Action Command: " + e.getActionCommand());
						}
						break;
					case "M":
						switch (splitCommand[2]) {
							case "H\u2082O":
								//Create a new water
								molecule.atoms.add(new Atom(8,8,8));
								molecule.atoms.add(new Atom(1,0,0));
								molecule.atoms.add(new Atom(1,0,0));
								break;
							case "CO\u2082":
								//Create a new carbon dioxide
								molecule.atoms.add(new Atom(6,6,6));
								molecule.atoms.add(new Atom(8,8,8));
								molecule.atoms.add(new Atom(8,8,8));
								break;
							case "O\u2082":
								//Create a new molecular oxygen
								molecule.atoms.add(new Atom(8,8,8));
								molecule.atoms.add(new Atom(8,8,8));
								break;
							case "C\u2086H\u2081\u2082O\u2086":
								//Create a new glucose
								molecule.atoms.add(new Atom(6,6,6));
								molecule.atoms.add(new Atom(6,6,6));
								molecule.atoms.add(new Atom(6,6,6));
								molecule.atoms.add(new Atom(6,6,6));
								molecule.atoms.add(new Atom(6,6,6));
								molecule.atoms.add(new Atom(6,6,6));
								molecule.atoms.add(new Atom(8,8,8));
								molecule.atoms.add(new Atom(8,8,8));
								molecule.atoms.add(new Atom(8,8,8));
								molecule.atoms.add(new Atom(8,8,8));
								molecule.atoms.add(new Atom(8,8,8));
								molecule.atoms.add(new Atom(8,8,8));
								molecule.atoms.add(new Atom(1,0,0));
								molecule.atoms.add(new Atom(1,0,0));
								molecule.atoms.add(new Atom(1,0,0));
								molecule.atoms.add(new Atom(1,0,0));
								molecule.atoms.add(new Atom(1,0,0));
								molecule.atoms.add(new Atom(1,0,0));
								molecule.atoms.add(new Atom(1,0,0));
								molecule.atoms.add(new Atom(1,0,0));
								molecule.atoms.add(new Atom(1,0,0));
								molecule.atoms.add(new Atom(1,0,0));
								molecule.atoms.add(new Atom(1,0,0));
								molecule.atoms.add(new Atom(1,0,0));
								break;
							case "Custom":
								//Create a custom molecule
								ParticleDesigner.customMolecule();
								break;
							default:
								System.out.println("Error: Unregistered Action Command: " + e.getActionCommand());
						}
						break;
					default:
						System.out.println("Error: Unregistered Action Command: " + e.getActionCommand());
				}
				Engine.getDisp().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR)); 
				if (molecule.atoms.size() > 0) {
					Engine.getDisp().setParticleAddedByCursor(molecule);
				}
				break;
			case "E":
				switch (splitCommand[2]) {
					case "V":
						Engine.getDisp().setGazeObj(Engine.findParticleByName(splitCommand[1]));
						break;
					case "R":
						//Remove a particular particle
						Engine.findParticleByName(splitCommand[1]).removeFromUniv();
						break;
					default:
						System.out.println("Error: Unregistered Action Command: " + e.getActionCommand());
				}
				break;	
			case "T":
				switch (splitCommand[1]) {
					case "Tips":
						Engine.getDisp().displayTips();
						break;
					case "Turn Trails On":
						//Turn particle trails on
						int result = Engine.getDisp().yesNoMsg("It is recommended to reduce engine FPS and speed before turning on trails to due high processing load, are you sure you want to continue?");
						if (result == JOptionPane.YES_OPTION) {
							Engine.setTrailsOn(true);
							((JMenuItem) e.getSource()).setText("Turn Trails Off"); 
							((JMenuItem) e.getSource()).setActionCommand("T-Turn Trails Off"); 
						}
						break;
					case "Turn Trails Off":
						//Turn particle trails off
						Engine.setTrailsOn(false);
						((JMenuItem) e.getSource()).setText("Turn Trails On"); 
						((JMenuItem) e.getSource()).setActionCommand("T-Turn Trails On"); 
						break;
					case "Clear Trails":
						//Deletes all particle trails
						Engine.clearParticleTrails();
						break;
					case "Change Speed":
						//Changes time multiplier by slider
						Engine.getDisp().changeSpeed();
						break;
					case "Change Physics Engine FPS":
						//Changes Phys Engine FPS
						Engine.getDisp().changePhysFPS();
						break;
					default:
						System.out.println("Error: Unregistered Action Command: " + e.getActionCommand());
				}
				break;
			default:
				System.out.println("Error: Unregistered Action Command: " + e.getActionCommand());
		}
	}
}
