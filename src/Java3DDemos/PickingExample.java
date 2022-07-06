package Java3DDemos;
import javax.media.j3d.*;
import com.sun.j3d.utils.picking.*;
import com.sun.j3d.utils.picking.behaviors.*;
import com.sun.j3d.utils.geometry.*;

/**
* A simple example for the use of a picking behaviour. Different transformations 
* are initiated by clicking objects with the mouse button.
*
* @author Frank Klawonn
* Last change 07.07.2005
* @see InteractionExample
*/
public class PickingExample extends PickMouseBehavior
{

  //This array contains the Alphas for the transformations to be 
  //initiated by mouse clicks.
  public Alpha[] alphas;

  //This boolean value is needed to decide which transformation  
  //(shrinking or growing) shoulc be applied to the sphere.
  public boolean shrink;


  //Constructor
  public PickingExample(Canvas3D pCanvas, BranchGroup root, Bounds pBounds,
                        Alpha[] alphs)
  {
    super(pCanvas,root,pBounds);
    setSchedulingBounds(pBounds);
    alphas = alphs;
    shrink = true;
  }


  public void updateScene(int xpos, int ypos)
  {
    Primitive pickedShape = null;
    pickCanvas.setShapeLocation(xpos,ypos);
    PickResult pResult = pickCanvas.pickClosest();
    if (pResult != null)
    {
      pickedShape = (Primitive) pResult.getNode(PickResult.PRIMITIVE);
    }

    if (pickedShape != null)
    {
      if (pickedShape.getUserData()=="box")
      {
        //The cube has been picked. The starting time of its rotation
        //must be set to now.
        alphas[0].setStartTime(System.currentTimeMillis()-alphas[0].getTriggerTime());
      }
      else
      {
        if (pickedShape.getUserData()=="sphere")
        {
          //The cube has been picked. The correct transformation
          //(shrinking or growing) should be chosen. The starting time of 
          //the corresponding transformation must be set to now.
          if (shrink)
          {
            alphas[1].setStartTime(System.currentTimeMillis()-alphas[1].getTriggerTime());
            shrink = false; //Next time the sphere should grow again.
          }
          else
          {
            alphas[2].setStartTime(System.currentTimeMillis()-alphas[2].getTriggerTime());
            shrink = true; //Next time the sphere should shrink again.
          }
        }
      }
    }

  }



}