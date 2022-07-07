package InputHandling;

import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PickConeRay;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.picking.behaviors.*;

import Control.Display;
import Control.Engine;
import Control.ParticleCreation;
import PhysTree.Atom;
import PhysTree.Molecule;
import Reference.PhysCalc;
import Reference.Vector3D;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;

public class MouseHandler extends PickMouseBehavior {

        public MouseHandler(Canvas3D canvas, BranchGroup bg, Bounds bounds)
        {
            super(canvas, bg, bounds);
            setSchedulingBounds(bounds);

            pickCanvas.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
            // allows PickIntersection objects to be returned
        }
        
        public void updateScene(int xpos, int ypos)
        {
        	
            if (Engine.getDisp().getParticlesAddedByCursor() != null) {
            	pickCanvas.setShapeLocation(xpos, ypos);
            	Transform3D viewTrans = new Transform3D();
            	Vector3d eyePosJ3d = new Vector3d();
            	TransformGroup viewTG = Engine.getDisp().getUniverse().getViewingPlatform().getViewPlatformTransform();
            	viewTG.getTransform(viewTrans);
            	viewTrans.get(eyePosJ3d);
            	Vector3D eyePos = new Vector3D(eyePosJ3d);
            	Vector3D gazePos = Engine.getDisp().getGazePnt();
                Vector3D eyeToGaze = eyePos.sub(gazePos);
                
                Vector3d eyeToPickJ3d = new Vector3d();
                ((PickConeRay) pickCanvas.getPickShape()).getDirection(eyeToPickJ3d);
                Vector3D eyeToPickDir = new Vector3D(eyeToPickJ3d);
                
                double eyeToPickMag = eyeToGaze.getMagnitude() / Math.cos(Math.toRadians(eyeToGaze.angle(eyeToPickDir.mult(-1))));
                Vector3D eyeToPick = eyeToPickDir.mult(eyeToPickMag).add(gazePos); //Adds the gazePos to change in from j3d coordinates that are moved around to assist with centering the gaze object, to instead be real coords
                Vector3D pickPos = eyeToPick.add(eyePos);
            	ParticleCreation.create(Engine.getDisp().getParticlesAddedByCursor(), pickPos);
            }
            
            
            
            
            
            //Vector3D sightVector = pickCanvas
        } // end of updateScene(  )
        
        
        @Override
        public void processStimulus (Enumeration criteria) {
        	  WakeupCriterion wakeup;
              AWTEvent[] evt = null;
              int xpos = 0, ypos = 0;

              while(criteria.hasMoreElements()) {
                wakeup = (WakeupCriterion)criteria.nextElement();
                if (wakeup instanceof WakeupOnAWTEvent)
          	evt = ((WakeupOnAWTEvent)wakeup).getAWTEvent();
              }

              if (evt[0] instanceof MouseEvent){
                mevent = (MouseEvent) evt[0];

                if (debug)
          	System.out.println("got mouse event");
                processMouseEvent((MouseEvent)evt[0]);
                xpos = mevent.getPoint().x;
                ypos = mevent.getPoint().y;
              }

              if (debug)
                System.out.println("mouse position " + xpos + " " + ypos);

              if (buttonPress){
                updateScene(xpos, ypos);
              }
              wakeupOn (new WakeupOnAWTEvent( MouseEvent.MOUSE_PRESSED ));
        }
        
        private void processMouseEvent(MouseEvent evt) {
            buttonPress = false;

            if (evt.getID()==MouseEvent.MOUSE_PRESSED |
        	evt.getID()==MouseEvent.MOUSE_CLICKED) {
              buttonPress = true;
              return;
            }
            else if (evt.getID() == MouseEvent.MOUSE_MOVED) {
              // Process mouse move event
            }
          }
    }