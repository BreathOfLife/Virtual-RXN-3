package Control;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import PhysTree.Atom;
import PhysTree.Molecule;

public class ParticleDesigner {

	private static Molecule moleculeAdded;
	private static int atomUnderConstruction;
	
	private static JFrame frame;
	private static JSpinner protonSpin, electronSpin, neutronSpin;
	private static JPanel pTable;
	
	public static final String[] atomSymbols = new String[] {"H","He","Li","Be","B","C","N","O","F","Ne","Na","Mg","Al","Si","P","S","Cl","Ar","K","Ca","Sc","Ti","V","Cr","Mn","Fe","Co","Ni","Cu","Zn","Ga","Ge","As","Se","Br","Kr","Rb","Sr","Y","Zr","Nb","Mo","Tc","Ru","Rh","Pd","Ag","Cd","In","Sn","Sb","Te","I","Xe","Cs","Ba","La","Ce","Pr","Nd","Pm","Sm","Eu","Gd","Tb","Dy","Ho","Er","Tm","Yb","Lu","Hf","Ta","W","Re","Os","Ir","Pt","Au","Hg","Tl","Pb","Bi","Po","At","Rn","Fr","Ra","Ac","Th","Pa","U","Np","Pu","Am","Cm","Bk","Cf","Es","Fm","Md","No","Lr","Rf","Db","Sg","Bh","Hs","Mt","Ds","Rg","Cn","Nh","Fl","Mc","Lv","Ts","Og"};
	public static final String[] atomSymbolsWithPTableSpacing = new String[] {"H",null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,"He","Li","Be",null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,"B","C","N","O","F","Ne","Na","Mg",null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,"Al","Si","P","S","Cl","Ar","K","Ca",null,null,null,null,null,null,null,null,null,null,null,null,null,null,"Sc","Ti","V","Cr","Mn","Fe","Co","Ni","Cu","Zn","Ga","Ge","As","Se","Br","Kr","Rb","Sr",null,null,null,null,null,null,null,null,null,null,null,null,null,null,"Y","Zr","Nb","Mo","Tc","Ru","Rh","Pd","Ag","Cd","In","Sn","Sb","Te","I","Xe","Cs","Ba","La","Ce","Pr","Nd","Pm","Sm","Eu","Gd","Tb","Dy","Ho","Er","Tm","Yb","Lu","Hf","Ta","W","Re","Os","Ir","Pt","Au","Hg","Tl","Pb","Bi","Po","At","Rn","Fr","Ra","Ac","Th","Pa","U","Np","Pu","Am","Cm","Bk","Cf","Es","Fm","Md","No","Lr","Rf","Db","Sg","Bh","Hs","Mt","Ds","Rg","Cn","Nh","Fl","Mc","Lv","Ts","Og"};
	
	public static void singleAtom() {
		moleculeAdded = new Molecule();
		moleculeAdded.atoms.add(new Atom());
		atomUnderConstruction = 0;
		customAtom();
	}
	
	private static void customAtom() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		pTable = new JPanel();
		mainPanel.add(pTable);
		pTable.setLayout(new GridLayout(7, 32));
		ActionListener listenerA = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String symb = ((JButton) e.getSource()).getText();
				for (int i = 0; i < atomSymbols.length; i++) {
					if (atomSymbols[i].equals(symb)) {
						protonSpin.setValue(i+1);
						neutronSpin.setValue(i+1);
						electronSpin.setValue(i+1);
						break;
					}
				}
				updateAddedMolecule();
			}
			
		};
		ChangeListener listenerC = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				updateAddedMolecule();
			}
			
		};
		
		
		for (String symb : atomSymbolsWithPTableSpacing) {
			if (symb != null) {
				JButton butt = new JButton(symb);
				butt.setMargin(new Insets(0,0,0,0));
				butt.addActionListener(listenerA);
				pTable.add(butt);
			} else {
				pTable.add(new JLabel());
			}
		}
		
		JPanel detailPanel = new JPanel();
		mainPanel.add(detailPanel);
		detailPanel.setLayout(new GridLayout(1,3));
		JPanel protonCol = new JPanel();
		detailPanel.add(protonCol);
		protonCol.add(new JLabel("Proton"));
		protonSpin = new JSpinner(new SpinnerNumberModel(0,0,1000,1));
		protonSpin.addChangeListener(listenerC);
		protonCol.add(protonSpin);
		JPanel electronCol = new JPanel();
		detailPanel.add(electronCol);
		electronCol.add(new JLabel("Electron"));
		electronSpin = new JSpinner(new SpinnerNumberModel(0,0,1000,1));
		electronSpin.addChangeListener(listenerC);
		electronCol.add(electronSpin);
		JPanel neutronCol = new JPanel();
		detailPanel.add(neutronCol);
		neutronCol.add(new JLabel("Neutron"));
		neutronSpin = new JSpinner(new SpinnerNumberModel(0,0,1000,1));
		neutronSpin.addChangeListener(listenerC);
		neutronCol.add(neutronSpin);
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(mainPanel);
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);
	}

	protected static void updateAddedMolecule() {
		moleculeAdded.atoms.get(atomUnderConstruction).setProtons((int) protonSpin.getValue());
		moleculeAdded.atoms.get(atomUnderConstruction).setNeutrons((int) neutronSpin.getValue());
		moleculeAdded.atoms.get(atomUnderConstruction).setElectrons((int) electronSpin.getValue());
		
		Engine.getDisp().setParticleAddedByCursor(moleculeAdded);
	}
	
	public static void reset() {
		moleculeAdded = null;
		atomUnderConstruction = 0;
		if (frame != null) {
			frame.setVisible(false);
		}
	}
}
