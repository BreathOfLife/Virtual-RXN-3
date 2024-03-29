package InputHandling;

import java.awt.Cursor;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import Control.Engine;
import Control.ParticleDesigner;

public class KeyHandler extends AbstractAction {
    public KeyHandler(String actionCommand) {
       putValue(ACTION_COMMAND_KEY, actionCommand);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       switch (e.getActionCommand()) {
       	case "ESC":
       		Engine.getDisp().setParticleAddedByCursor(null);
       		Engine.getDisp().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
       		ParticleDesigner.reset();
       		break;
       	case "SPACE":
       		Engine.togglePause();
			   /*
			System.out.println(Engine.getFrame().getWidth() + " Y");
			Engine.getFrame().setSize(Engine.getFrame().getWidth() + 1, Engine.getFrame().getHeight());

			    */
       		break;
       	case "H":
       		Engine.toggleShowPartLabels();
			   /*
			System.out.println(Engine.getFrame().getWidth() + " N");
			Engine.getFrame().setSize(Engine.getFrame().getWidth() + 1, Engine.getFrame().getHeight());
			*/
       		break;
       	default:
       		System.out.println("Error: Unregistered Action Command: " + e.getActionCommand());
       }
    }
 }
