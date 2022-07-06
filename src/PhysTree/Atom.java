package PhysTree;

public class Atom {

	public Atom(int protons, int neutrons, int electrons) {
		this.protons = protons;
		this.neutrons = neutrons;
		this.electrons = electrons;
	}

	public Atom() {
		this.protons = 0;
		this.neutrons = 0;
		this.electrons = 0;
	}

	public int protons, neutrons, electrons;

	public void setElectrons(int value) {
		this.electrons = value;
	}
	
	public void setProtons(int value) {
		this.protons = value;
	}
	
	public void setNeutrons(int value) {
		this.neutrons = value;
	}
	
}
