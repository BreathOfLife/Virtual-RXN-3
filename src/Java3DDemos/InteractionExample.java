package Java3DDemos;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.behaviors.vp.*;
import javax.swing.JFrame;


/**
* An example for the use of PickingBehaviours. Clicking the cube will make it
* rotate. Clicking the sphere will lead to shring and growing alternatingly.
*
*
* @author Frank Klawonn
* Last change 07.07.2005
* @see PickingExample
*/
public class InteractionExample extends JFrame
{

  //The canvas to be drawn upon.
  public Canvas3D myCanvas3D;



  public InteractionExample()
  {
    //Mechanism for closing the window and ending the program.
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    //Default settings for the viewer parameters.
    myCanvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());


    //Construct the SimpleUniverse:
    //First generate it using the Canvas.
    SimpleUniverse simpUniv = new SimpleUniverse(myCanvas3D);


    //Default position of the viewer.
    simpUniv.getViewingPlatform().setNominalViewingTransform();


    //The scene is generated in this method.
    createSceneGraph(simpUniv);


    //Add some light to the scene.
    addLight(simpUniv);


    //The following three lines enable navigation through the scene using the mouse.
    OrbitBehavior ob = new OrbitBehavior(myCanvas3D);
    ob.setSchedulingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE));
    simpUniv.getViewingPlatform().setViewPlatformBehavior(ob);


    //Show the canvas/window.
    setTitle("Click the objects with the left mouse button.");
    setSize(700,700);
    getContentPane().add("Center", myCanvas3D);
    setVisible(true);

  }




  public static void main(String[] args)
  {
     InteractionExample interaction = new InteractionExample();
  }





  //In this method, the objects for the scene are generated and added to 
  //the SimpleUniverse.
  public void createSceneGraph(SimpleUniverse su)
  {


//*** Generate a red cube including its transformation group ***
    Color3f ambientColourFBox = new Color3f(0.5f,0.0f,0.0f);
    Color3f emissiveColourFBox = new Color3f(0.0f,0.0f,0.0f);
    Color3f diffuseColourFBox = new Color3f(0.5f,0.0f,0.0f);
    Color3f specularColourFBox = new Color3f(0.8f,0.0f,0.0f);
    float shininessFBox = 10.0f;

    Appearance fBoxApp = new Appearance();

    fBoxApp.setMaterial(new Material(ambientColourFBox,emissiveColourFBox,
                          diffuseColourFBox,specularColourFBox,shininessFBox));

    Box fBox = new Box(0.2f,0.2f,0.2f,fBoxApp);

    //The UserData are needed in order to identify the cube when it is picked.
    fBox.setUserData("box");

    TransformGroup tgmBox = new TransformGroup();
    tgmBox.addChild(fBox);

    Transform3D rotationAxis = new Transform3D();


    //The following lines define what the cube should do when picked.
    Alpha boxAlpha = new Alpha(1,2000);

    //The starting time is first postponed until "infinity".
    boxAlpha.setStartTime(Long.MAX_VALUE);

    RotationInterpolator boxRotation = new RotationInterpolator(boxAlpha,tgmBox,
                                             rotationAxis,0.0f,(float) Math.PI*2);

    BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0),Double.MAX_VALUE);
    boxRotation.setSchedulingBounds(bounds);

    //The movement is added to the transformation group.
    tgmBox.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tgmBox.addChild(boxRotation);


    //A transformation group for positioning the cube.
    Transform3D tfFBox = new Transform3D();
    tfFBox.rotY(Math.PI/6);
    Transform3D rotationX = new Transform3D();
    rotationX.rotX(-Math.PI/5);
    tfFBox.mul(rotationX);
    TransformGroup tgFBox = new TransformGroup(tfFBox);
    tgFBox.addChild(tgmBox);


//*** The same for the sphere.
    Color3f ambientColourBSphere = new Color3f(0.0f,0.6f,0.0f);
    Color3f emissiveColourBSphere = new Color3f(0.0f,0.0f,0.0f);
    Color3f diffuseColourBSphere = new Color3f(0.0f,0.6f,0.0f);
    Color3f specularColourBSphere = new Color3f(0.0f,0.6f,0.0f);
    float shininessBSphere = 2.0f;

    Appearance bSphereApp = new Appearance();

    bSphereApp.setMaterial(new Material(ambientColourBSphere,
                                        emissiveColourBSphere,
                                        diffuseColourBSphere,
                                        specularColourBSphere,
                                        shininessBSphere));

    Sphere bSphere = new Sphere(0.4f,bSphereApp);

    //The UserData are needed in order to identify the sphere when it is picked.
    bSphere.setUserData("sphere");

    TransformGroup tgmBSphere = new TransformGroup();
    tgmBSphere.addChild(bSphere);


    //Two transformations must be defined for the sphere: One for
    //shrinking, one for growing. First the shrinking transformation 
    //is defined.
    Alpha sphereShrinkAlpha = new Alpha(1,2000);

    //The starting time is first postponed until "infinity".
    sphereShrinkAlpha.setStartTime(Long.MAX_VALUE);

    ScaleInterpolator shrinker = new ScaleInterpolator(sphereShrinkAlpha,
                                                       tgmBSphere,
                                                       new Transform3D(),
                                                       1.0f,0.5f);

    shrinker.setSchedulingBounds(bounds);

    tgmBSphere.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tgmBSphere.addChild(shrinker);


    //The same for the growing sphere.
    Alpha sphereStretchAlpha = new Alpha(1,2000);
    sphereStretchAlpha.setStartTime(Long.MAX_VALUE);

    ScaleInterpolator stretcher = new ScaleInterpolator(sphereStretchAlpha,
                                                       tgmBSphere,
                                                       new Transform3D(),
                                                       0.5f,1.0f);

    stretcher.setSchedulingBounds(bounds);

    tgmBSphere.addChild(stretcher);


    //A transformation group for positioning the sphere.
    Transform3D tfBSphere = new Transform3D();
    tfBSphere.setTranslation(new Vector3f(2.0f,0.0f,-10.5f));
    TransformGroup tgBSphere = new TransformGroup(tfBSphere);
    tgBSphere.addChild(tgmBSphere);

    //Generate the scenegraph.
    BranchGroup theScene = new BranchGroup();
    theScene.addChild(tgFBox);
    theScene.addChild(tgBSphere);


    //Definition of the parameters for picking. Generate the PickingBehaviour
    //and add it to the scene.
    Alpha[] alphas = new Alpha[3];

    alphas[0] = boxAlpha;
    alphas[1] = sphereShrinkAlpha;
    alphas[2] = sphereStretchAlpha;

    PickingExample pe = new PickingExample(myCanvas3D,theScene,bounds,alphas);

    theScene.addChild(pe);


    theScene.compile();

    //Add everything to the universe.
    su.addBranchGraph(theScene);

  }






  //Some light is added to the scene here.
  public void addLight(SimpleUniverse su)
  {

    BranchGroup bgLight = new BranchGroup();

    BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), Double.MAX_VALUE);

    //Directional light.
    Color3f lightColour1 = new Color3f(1.0f,1.0f,1.0f);
    Vector3f lightDir1  = new Vector3f(-1.0f,0.0f,-0.5f);
    DirectionalLight light1 = new DirectionalLight(lightColour1, lightDir1);
    light1.setInfluencingBounds(bounds);

    bgLight.addChild(light1);


    //Ambient light.
    Color3f lightColourAmb = new Color3f(0.5f, 0.5f, 0.5f);
    AmbientLight lightAmb = new AmbientLight(lightColourAmb);
    lightAmb.setInfluencingBounds(bounds);
    bgLight.addChild(lightAmb);


    su.addBranchGraph(bgLight);

  }

}