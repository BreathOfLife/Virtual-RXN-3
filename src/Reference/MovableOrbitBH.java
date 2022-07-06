package Reference;

import javax.media.j3d.Canvas3D;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;

public class MovableOrbitBH extends OrbitBehavior {

	public MovableOrbitBH() {
		// TODO Auto-generated constructor stub
	}

	public MovableOrbitBH(Canvas3D arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public MovableOrbitBH(Canvas3D arg0, int arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	
	public void update() {
		integrateTransforms();
	}
	
}
