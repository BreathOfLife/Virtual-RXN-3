package VirtualObjects;

import PhysObjects.*;
import Reference.Vector3D;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Geometry;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.OrientedShape3D;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
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
	private ArrayList<TransformGroup> trailGroups;
	private ArrayList<Vector3D> trailBasePos;
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
		trailGroups = new ArrayList<TransformGroup>();
		trailBasePos = new ArrayList<Vector3D>();
		
		this.sphere.setCapability(Node.ENABLE_PICK_REPORTING);
		//PickTool.setCapabilities(sphere, PickTool.INTERSECT_FULL);
		
		
		
		
		Appearance ap = new Appearance();
		ap.setColoringAttributes(new ColoringAttributes(new Color3f(Color.CYAN), ColoringAttributes.NICEST));
		Color3f black = new Color3f(0.0f, 0.0f, 0.0f);

		Color3f mainColor = new Color3f(Color.GRAY);
		if (part.getCharge() < 0) {
			mainColor = new Color3f(Color.CYAN);
		} else if (part.getCharge() > 0) {
			mainColor = new Color3f(Color.RED);
		}
		ap.setMaterial(new Material(mainColor, black, mainColor, black, 1.0f));
		
		Font3D labelFont = new Font3D(new Font(Font.DIALOG, Font.BOLD, 20), new FontExtrusion());
		Text3D labelGeom = new Text3D(labelFont, part.getName());
		labelGeom.setAlignment(Text3D.ALIGN_CENTER);
		Appearance labelAp = new Appearance();
		labelAp.setRenderingAttributes(labelRA);
		labelAp.setMaterial(new Material(mainColor, black, mainColor, black, 1.0f));
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
			System.out.println(atomLabelString);
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
			//Create new trail sphere
			Sphere trailSphere = new Sphere((float) part.getRadius());
			Appearance ap = new Appearance();
			Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
			Color3f mainColor = new Color3f(Color.GRAY);
			if (part.getCharge() < 0) {
				mainColor = new Color3f(Color.CYAN);
			} else if (part.getCharge() > 0) {
				mainColor = new Color3f(Color.RED);
			}
			ap.setMaterial(new Material(mainColor, black, mainColor, black, 1.0f));
			trailSphere.setAppearance(ap);
			Transform3D trailTrans = new Transform3D();
			TransformGroup trailGroup = new TransformGroup();
			trailGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			trailGroup.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
			trailGroup.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
			trailTrans.setTranslation(pos.sub(Engine.getDisp().getGazePnt()).toJ3dVec());
			trailGroup.setTransform(trailTrans);
			trailGroup.addChild(trailSphere);
			trailGroups.add(trailGroup);
			trailBasePos.add(part.getPos());
			Engine.getDisp().addToUniv(trailGroup);
		}
		
		//Update all trails according to viewPoint
		for (int i = 0; i < trailGroups.size(); i++) {
			Vector3D currTrailPos = trailBasePos.get(i);
			TransformGroup currTrailGroup = trailGroups.get(i);
			Transform3D currTrailTrans = new Transform3D();
			currTrailTrans.setTranslation(currTrailPos.sub(Engine.getDisp().getGazePnt()).toJ3dVec());
			currTrailGroup.setTransform(currTrailTrans);
		}
	}
	
	public void delete() {
		Engine.getDisp().removeFromUniv(group);
	}

	public static boolean trailsHalted() {
		return haltTrails;
	}

	public static void setHaltTrails(boolean haltTrails) {
		ParticleSphere.haltTrails = haltTrails;
	}

	public void deleteTrails() {
		for (TransformGroup currTrailGroup : trailGroups) {
			Engine.getDisp().removeFromUniv(currTrailGroup);
		}
		trailGroups.clear();
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
