package Control;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import PhysObjects.Electron;
import PhysObjects.Nucleus;
import PhysObjects.Particle;
import PhysTree.Atom;
import PhysTree.Molecule;
import Reference.PhysCalc;
import Reference.Vector3D;
import VirtualObjects.ParticleSphere;

public class ParticleCreation {
	
	public static final String[] atomSymbols = new String[] {"H","He","Li","Be","B","C","N","O","F","Ne","Na","Mg","Al","Si","P","S","Cl","Ar","K","Ca","Sc","Ti","V","Cr","Mn","Fe","Co","Ni","Cu","Zn","Ga","Ge","As","Se","Br","Kr","Rb","Sr","Y","Zr","Nb","Mo","Tc","Ru","Rh","Pd","Ag","Cd","In","Sn","Sb","Te","I","Xe","Cs","Ba","La","Ce","Pr","Nd","Pm","Sm","Eu","Gd","Tb","Dy","Ho","Er","Tm","Yb","Lu","Hf","Ta","W","Re","Os","Ir","Pt","Au","Hg","Tl","Pb","Bi","Po","At","Rn","Fr","Ra","Ac","Th","Pa","U","Np","Pu","Am","Cm","Bk","Cf","Es","Fm","Md","No","Lr","Rf","Db","Sg","Bh","Hs","Mt","Ds","Rg","Cn","Nh","Fl","Mc","Lv","Ts","Og"};
	
	public static void create(Molecule molecule, Vector3D originPoint){ //All positions already in partGroup are relative to the origin point given
		ParticleSphere.setHaltTrails(true);
		int focusPartIndex = Engine.getAllParticles().size();
		double atomOffset = 1.4e-10; //Averagish bond length
		for (int i = 0; i < molecule.atoms.size(); i++) {
			if (i == 0) {
				create(molecule.atoms.get(i), originPoint);
			} else {
				create(molecule.atoms.get(i), originPoint.add(Vector3D.random(atomOffset)));
			}
		}
		Engine.getDisp().setGazeObj(Engine.getAllParticles().get(focusPartIndex));
		ParticleSphere.setHaltTrails(false);
	}

	public static void create(Atom atom, Vector3D originPoint) {
		String atomName;
		if (atom.name == null) {
			if (atom.protons > 0) {
				atomName = " (" + atomSymbols[atom.protons - 1] + ")";
			} else {
				atomName = "eutron()";
			}
		} else {
			atomName = "(" + atom.name + ")";
		}
		if (atom.protons > 0 || atom.neutrons > 0) {
			Nucleus n = new Nucleus(atom.neutrons, atom.protons, originPoint, new Vector3D(), "N" + atomName);
			Engine.getAllParticles().add(n);
		}
		for (int i = 1; i <= atom.electrons; i++) {
			for (int j = 1; j <= Engine.getEProbPartitions(); j++) {
				int energyLevel = (int) Math.ceil(Math.sqrt(i / 2.0)); //Energy level (Bohr)
				double hydrogenRadius = PhysCalc.bohrRadius * Math.pow(energyLevel, 2.25);//^2.25 was found to approximately get to average hydrogen radius //Hydrogen Radius assumes 1 proton
				double radius = hydrogenRadius / atom.protons;
				Vector3D posRelToOrigin;
				Vector3D vel;
				if (Double.isNaN(radius) || Double.isInfinite(radius)) {
					posRelToOrigin = new Vector3D();
					vel = new Vector3D();
				} else {
					posRelToOrigin = Vector3D.random(radius);
					vel = posRelToOrigin.crossProd(Vector3D.random(1));
				}

				Electron e = new Electron(posRelToOrigin.add(originPoint), vel, "E" + i + atomName);
				Engine.getAllParticles().add(e);
			}
		}
	}
}
