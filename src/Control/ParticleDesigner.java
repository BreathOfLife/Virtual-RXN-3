package Control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import PhysTree.Atom;
import PhysTree.Molecule;

public class ParticleDesigner {

	private static Molecule moleculeAdded;
	private static int atomUnderConstruction;

	private static boolean atomWinOpen, molWinOpen;

	private static JSpinner protonSpin, electronSpin, neutronSpin;
	private static JPanel pTable;

	private static JScrollPane linkedAtomPane;
	
	public static final String[] atomSymbols = new String[] {"H","He","Li","Be","B","C","N","O","F","Ne","Na","Mg","Al","Si","P","S","Cl","Ar","K","Ca","Sc","Ti","V","Cr","Mn","Fe","Co","Ni","Cu","Zn","Ga","Ge","As","Se","Br","Kr","Rb","Sr","Y","Zr","Nb","Mo","Tc","Ru","Rh","Pd","Ag","Cd","In","Sn","Sb","Te","I","Xe","Cs","Ba","La","Ce","Pr","Nd","Pm","Sm","Eu","Gd","Tb","Dy","Ho","Er","Tm","Yb","Lu","Hf","Ta","W","Re","Os","Ir","Pt","Au","Hg","Tl","Pb","Bi","Po","At","Rn","Fr","Ra","Ac","Th","Pa","U","Np","Pu","Am","Cm","Bk","Cf","Es","Fm","Md","No","Lr","Rf","Db","Sg","Bh","Hs","Mt","Ds","Rg","Cn","Nh","Fl","Mc","Lv","Ts","Og"};
	public static final String[] atomSymbolsWithPTableSpacing = new String[] {"H",null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,"He","Li","Be",null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,"B","C","N","O","F","Ne","Na","Mg",null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,"Al","Si","P","S","Cl","Ar","K","Ca",null,null,null,null,null,null,null,null,null,null,null,null,null,null,"Sc","Ti","V","Cr","Mn","Fe","Co","Ni","Cu","Zn","Ga","Ge","As","Se","Br","Kr","Rb","Sr",null,null,null,null,null,null,null,null,null,null,null,null,null,null,"Y","Zr","Nb","Mo","Tc","Ru","Rh","Pd","Ag","Cd","In","Sn","Sb","Te","I","Xe","Cs","Ba","La","Ce","Pr","Nd","Pm","Sm","Eu","Gd","Tb","Dy","Ho","Er","Tm","Yb","Lu","Hf","Ta","W","Re","Os","Ir","Pt","Au","Hg","Tl","Pb","Bi","Po","At","Rn","Fr","Ra","Ac","Th","Pa","U","Np","Pu","Am","Cm","Bk","Cf","Es","Fm","Md","No","Lr","Rf","Db","Sg","Bh","Hs","Mt","Ds","Rg","Cn","Nh","Fl","Mc","Lv","Ts","Og"};
	
	public static void singleAtom() {
		moleculeAdded = new Molecule();
		moleculeAdded.atoms.add(new Atom());
		atomUnderConstruction = 0;
		customAtom();
	}
	
	private static void customAtom() {
		if (atomWinOpen) {
			return;
		}
		if (molWinOpen) {
			reset();
		}
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.PAGE_AXIS));
		JLabel title = new JLabel("Atom Designer");
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setFont(new Font(Font.DIALOG,Font.BOLD, 30));
		titlePanel.add(title);
		JLabel currAtom = new JLabel("Current Atom: ");
		currAtom.setHorizontalAlignment(JLabel.CENTER);
		currAtom.setFont(new Font(Font.DIALOG,Font.BOLD, 15));
		titlePanel.add(currAtom);
		mainPanel.add(titlePanel, BorderLayout.NORTH);

		pTable = new JPanel();
		mainPanel.add(pTable, BorderLayout.CENTER);
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
		mainPanel.add(detailPanel, BorderLayout.SOUTH);
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

		Engine.getDisp().getCustomParticlePanel().add(mainPanel);
		Engine.getDisp().getCustomParticlePanel().setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
		SwingUtilities.updateComponentTreeUI(Engine.getDisp());
		atomWinOpen = true;
	}

	public static void customMolecule() {
		if (molWinOpen) {
			return;
		}
		if (atomWinOpen) {
			reset();
		}
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.PAGE_AXIS));
		JLabel title = new JLabel("Molecule Designer");
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setFont(new Font(Font.DIALOG,Font.BOLD, 30));
		titlePanel.add(title);
		JLabel currAtom = new JLabel("Current Atom: ");
		currAtom.setHorizontalAlignment(JLabel.CENTER);
		currAtom.setFont(new Font(Font.DIALOG,Font.BOLD, 15));
		titlePanel.add(currAtom);
		mainPanel.add(titlePanel, BorderLayout.NORTH);

		ActionListener linkedAtomListener;
		JPanel linkedAtomCol = new JPanel();
		linkedAtomCol.setLayout(new BoxLayout(linkedAtomCol, BoxLayout.PAGE_AXIS));
		JLabel linkedAtomLabel = new JLabel("Bonded to:");
		linkedAtomCol.add(linkedAtomLabel);
		linkedAtomPane = new JScrollPane();
		linkedAtomCol.add(linkedAtomPane);
		mainPanel.add(linkedAtomCol,BorderLayout.CENTER);

		ActionListener btnListener = null;
		JPanel actionsCol = new JPanel();
		actionsCol.setLayout(new BoxLayout(actionsCol, BoxLayout.PAGE_AXIS));
		JLabel actionsLabel = new JLabel("Actions:");
		actionsCol.add(actionsLabel);
		JButton addAtomToBTN = new JButton("Add atom to current atom");
		addAtomToBTN.addActionListener(btnListener);
		JButton deleteAtomBTN = new JButton("Delete current atom");
		deleteAtomBTN.addActionListener(btnListener);
		actionsCol.add(addAtomToBTN);
		actionsCol.add(deleteAtomBTN);
		mainPanel.add(actionsCol,BorderLayout.EAST);

		Engine.getDisp().getCustomParticlePanel().add(mainPanel);
		Engine.getDisp().getCustomParticlePanel().setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
		SwingUtilities.updateComponentTreeUI(Engine.getDisp());
		molWinOpen = true;
	}

	protected static void updateAddedMolecule() {
		moleculeAdded.atoms.get(atomUnderConstruction).setProtons((int) protonSpin.getValue());
		moleculeAdded.atoms.get(atomUnderConstruction).setNeutrons((int) neutronSpin.getValue());
		moleculeAdded.atoms.get(atomUnderConstruction).setElectrons((int) electronSpin.getValue());
		
		Engine.getDisp().setParticleAddedByCursor(moleculeAdded);
	}
	
	public static void reset() {
		Engine.getDisp().getCustomParticlePanel().removeAll();
		Engine.getDisp().getCustomParticlePanel().setBorder(BorderFactory.createEmptyBorder());
		SwingUtilities.updateComponentTreeUI(Engine.getDisp());
		moleculeAdded = null;
		atomUnderConstruction = 0;
		atomWinOpen = false;
		molWinOpen = false;
	}
}
