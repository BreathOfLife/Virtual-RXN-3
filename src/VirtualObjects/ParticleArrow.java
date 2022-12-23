package VirtualObjects;

import java.awt.Color;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;

import com.sun.j3d.utils.geometry.Cone;

import Control.Engine;
import PhysObjects.Particle;
import Reference.Vector3D;
import com.sun.j3d.utils.geometry.Cylinder;

public class ParticleArrow {

	private Particle part;
	private Cone cone;
	private Cylinder cylinder;
	private Transform3D transl;
	private Transform3D rot;
	private TransformGroup groupT;
	private TransformGroup groupR;

	public ParticleArrow(Particle part, char type) {
		this.part = part;
		this.cone = new Cone((float) part.getRadius() / 2, (float) part.getRadius() * 3);
		this.cylinder = new Cylinder((float) ((float) part.getRadius() / 3), (float) part.getRadius() * 3);
		Appearance ap = new Appearance();
		ap.setColoringAttributes(new ColoringAttributes(new Color3f(Color.CYAN), ColoringAttributes.NICEST));
		Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
		Color3f mainColor = (type=='v')?(new Color3f(0.0f, 1f, 0.0f)):(new Color3f(0.0f, 1f, 1f));
		ap.setMaterial(new Material(mainColor, black, mainColor, black, 1.0f));
		cone.setAppearance(ap);
		cylinder.setAppearance(ap);
		this.transl = new Transform3D();
		this.rot = new Transform3D();
		this.groupT = new TransformGroup();
		this.groupR = new TransformGroup();
		groupR.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		groupR.setTransform(rot);
		groupR.addChild(cone);
		groupR.addChild(cylinder);
		groupT.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		transl.setTranslation(part.getPos().sub(Engine.getDisp().getGazePnt()).toJ3dVec());
		groupT.setTransform(transl);
		groupT.addChild(groupR);
		Engine.getDisp().addToUniv(groupT);
	}
	
	public Transform3D getTrans3DT() {
		return transl;
	}

	public Cone getCone() {
		return cone;
	}

	public void updatePos(Vector3D vec) {
		Vector3D innerVecSection = vec.getDir().mult(part.getRadius());
		Vector3D fullVec = part.getPos().add(innerVecSection);
		transl.setTranslation(fullVec.sub(Engine.getDisp().getGazePnt()).toJ3dVec());
		rot.setRotation(vec.toJ3dRotQuat());
		groupT.setTransform(transl);
		groupR.setTransform(rot);
		
	}

}
