package VirtualObjects;

import PhysObjects.*;
import Reference.Vector3D;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.ArrayList;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.universe.*;

import Control.Engine;

public class ParticleSphere {
	private Particle part;
	private Sphere sphere;
	private OrientedShape3D label;
	private TransformGroup labelGroup;
	private Transform3D labelTrans;
	private OrientedShape3D atomLabel;
	private TransformGroup atomLabelGroup;
	private Transform3D atomLabelTrans;
	private Transform3D trans;
	private TransformGroup group;
	private ArrayList<Vector3D> trailBasePos;
	private LineArray trailGeom;
	private Shape3D trailObj;
	private static boolean haltTrails = false;
	private static RenderingAttributes labelRA = new RenderingAttributes();
	private static RenderingAttributes atomLabelRA = new RenderingAttributes();

	public static void init() {
		labelRA.setCapability(RenderingAttributes.ALLOW_VISIBLE_WRITE);
		atomLabelRA.setCapability(RenderingAttributes.ALLOW_VISIBLE_WRITE);
		updateLabelVisibility();
	}
	
	public ParticleSphere(Particle part) {
		this.part = part;
		this.sphere = new Sphere((float) part.getRadius());
		trailBasePos = new ArrayList<Vector3D>();

		Appearance ap = new Appearance();
		ap.setColoringAttributes(new ColoringAttributes(new Color3f(Color.CYAN), ColoringAttributes.NICEST));
		Color3f black = new Color3f(0.0f, 0.0f, 0.0f);

		Color3f mainColor = new Color3f(Color.GRAY);
		Color3f labelColor = new Color3f(Color.GRAY);
		if (part.getCharge() < 0) {
			labelColor = new Color3f(Color.CYAN);
			mainColor = new Color3f(new Color(
					(int)(((double)Color.CYAN.getRed())/((Electron) part).getEProbPartitions()),
					(int)(((double)Color.CYAN.getGreen())/((Electron) part).getEProbPartitions()),
					(int)(((double)Color.CYAN.getBlue())/((Electron) part).getEProbPartitions())
			));

		} else if (part.getCharge() > 0) {
			labelColor = new Color3f(Color.RED);
			mainColor = new Color3f(Color.RED);
		}
		ap.setMaterial(new Material(mainColor, black, mainColor, black, 1.0f));
		Font3D labelFont = new Font3D(new Font(Font.DIALOG, Font.BOLD, 20), new FontExtrusion());
		Text3D labelGeom = new Text3D(labelFont, part.getName());
		labelGeom.setAlignment(Text3D.ALIGN_CENTER);
		Appearance labelAp = new Appearance();
		labelAp.setRenderingAttributes(labelRA);
		labelAp.setMaterial(new Material(labelColor, black, labelColor, black, 1.0f));
		label = new OrientedShape3D(labelGeom, labelAp, OrientedShape3D.ROTATE_ABOUT_POINT, new Point3f(), true, 8e-3);
		labelGroup = new TransformGroup();
		labelGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		labelGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		labelGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		labelTrans = new Transform3D();
		labelTrans.setTranslation(part.getPos().sub(Engine.getDisp().getGazePnt()).add(0,0,part.getRadius()*2).toJ3dVec());
		labelGroup.setTransform(labelTrans);
		labelGroup.addChild(label);
		Engine.getDisp().addToUniv(labelGroup);

		if (part instanceof Nucleus) {
			Font3D atomLabelFont = new Font3D(new Font(Font.DIALOG, Font.BOLD, 50), new FontExtrusion());
			String atomLabelStringWithParentheses = part.getName().substring(part.getName().indexOf('('));
			String atomLabelString = atomLabelStringWithParentheses.substring(1,atomLabelStringWithParentheses.length() - 1);
			Text3D atomLabelGeom = new Text3D(atomLabelFont, atomLabelString);
			atomLabelGeom.setAlignment(Text3D.ALIGN_CENTER);
			Appearance atomLabelAp = new Appearance();
			atomLabelAp.setRenderingAttributes(atomLabelRA);
			atomLabelAp.setMaterial(new Material(mainColor, black, mainColor, black, 1.0f));
			atomLabel = new OrientedShape3D(atomLabelGeom, atomLabelAp, OrientedShape3D.ROTATE_ABOUT_POINT, new Point3f(), true, 8e-3);
			atomLabelGroup = new TransformGroup();
			atomLabelGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			atomLabelGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
			atomLabelGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
			atomLabelTrans = new Transform3D();
			atomLabelTrans.setTranslation(part.getPos().sub(Engine.getDisp().getGazePnt()).add(0,0,part.getRadius()*2).toJ3dVec());
			atomLabelGroup.setTransform(atomLabelTrans);
			atomLabelGroup.addChild(atomLabel);
			Engine.getDisp().addToUniv(atomLabelGroup);
		}

		sphere.setAppearance(ap);
		this.trans = new Transform3D();
		this.group = new TransformGroup();
		group.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		group.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		group.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		trans.setTranslation(part.getPos().toJ3dVec());
		group.setTransform(trans);
		group.addChild(sphere);
		Engine.getDisp().addToUniv(group);

		trailGeom = new LineArray(Engine.getMaxTrailLength()*2 - 2, 1);
		trailGeom.setCapability(LineArray.ALLOW_COORDINATE_WRITE);
		trailGeom.setCapability(LineArray.ALLOW_COUNT_WRITE);
		trailObj = new Shape3D(trailGeom, ap);
		Engine.getDisp().addToUniv(trailObj);
	}
	
	public Transform3D getTrans3D() {
		return trans;
	}

	public Sphere getActualSphere() {
		return sphere;
	}

	public void updatePos(Vector3D pos) {
		trans.setTranslation(pos.sub(Engine.getDisp().getGazePnt()).toJ3dVec());
		group.setTransform(trans);
		
		labelTrans.setTranslation(pos.sub(Engine.getDisp().getGazePnt()).add(0,0,part.getRadius()*2).toJ3dVec());
		labelGroup.setTransform(labelTrans);

		if (part instanceof Nucleus) {
			atomLabelTrans.setTranslation(pos.sub(Engine.getDisp().getGazePnt()).add(0,0,part.getRadius()*2).toJ3dVec());
			atomLabelGroup.setTransform(atomLabelTrans);
		}



		if (Engine.trailsOn() && !haltTrails) {
			trailBasePos.add(part.getPos());
			if (trailBasePos.size() > Engine.getMaxTrailLength()) trailBasePos.remove(0);
		}
		
		//Update all trails according to viewPoint
		trailGeom.setValidVertexCount(trailBasePos.size());
		for (int i = 1; i < trailBasePos.size() - 1; i++) {
			Vector3D currTrailPos = trailBasePos.get(i);
			Vector3D nextTrailPos = trailBasePos.get(i+1);
			trailGeom.setCoordinate(i*2 - 2, currTrailPos.sub(Engine.getDisp().getGazePnt()).toJ3dPntF());
			trailGeom.setCoordinate(i*2-1, nextTrailPos.sub(Engine.getDisp().getGazePnt()).toJ3dPntF());
		}
	}
	
	public void delete() {
		deleteTrails();
		Engine.getDisp().removeFromUniv(group);
		Engine.getDisp().removeFromUniv(trailObj);
		Engine.getDisp().removeFromUniv(labelGroup);
		if (part instanceof Nucleus) {Engine.getDisp().removeFromUniv(atomLabelGroup);}
	}

	public static boolean trailsHalted() {
		return haltTrails;
	}

	public static void setHaltTrails(boolean haltTrails) {
		ParticleSphere.haltTrails = haltTrails;
	}

	public void deleteTrails() {
		trailBasePos.clear();
	}

	public TransformGroup getTransGroup() {
		return group;
	}

	public static void updateLabelVisibility() {
		if (Engine.isLabelsSubatomic()) {
			labelRA.setVisible(Engine.isPartLabelsOn());
			atomLabelRA.setVisible(false);
		} else {
			labelRA.setVisible(false);
			atomLabelRA.setVisible(Engine.isPartLabelsOn());
		}
	}
}
