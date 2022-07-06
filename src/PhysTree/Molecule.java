package PhysTree;

import java.util.ArrayList;

public class Molecule {

	public ArrayList<Atom> atoms = new ArrayList<>();
	public String config; //Linear, Tetra, Bipyramidal, etc.
	
	public void setConfiguration(String config) {
		this.config = config;
	}
}
