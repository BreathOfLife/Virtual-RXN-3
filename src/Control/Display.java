package Control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import Reference.PhysCalc;
import com.sun.j3d.utils.universe.*;

import InputHandling.ButtonHandler;
import InputHandling.KeyHandler;
import InputHandling.MouseHandler;
import PhysObjects.Nucleus;
import PhysObjects.Particle;
import PhysTree.Molecule;
import Reference.MovableOrbitBH;
import Reference.Vector3D;
import VirtualObjects.ParticleSphere;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.behaviors.vp.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.picking.*;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Node;
import javax.media.j3d.PickConeRay;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.View;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class Display extends JPanel{

	public static int WIDTH = 1000;
	public static int HEIGHT = 750;

	private Vector3D eyePosStart;
	private Vector3D gazePoint; //Gaze Point is used as an offset for all other particles, essentially instead of the camera moving around with all the other particles, the universe moves opposite to the gaze point to make it look like we are moving with the particle
	private boolean gazeChanging;
	private double gazeDiffSegment;
	private Vector3D finalGazePoint;
	private Particle gazeObj;
	private Vector3d upDir;
	
	private static Object UniverseEditingGK = new Object();

	private Canvas3D canvas;
	private SimpleUniverse universe;
	private BranchGroup root;
	private BranchGroup mainBranch;
	private MovableOrbitBH orbit;
	private PickCanvas pickCanvas;
	
	private Molecule addedByCursor;
	private TransformGroup viewingTransformGroup;
	private Transform3D viewingTransform;
	private View view;

	private MouseHandler mouseHandler;
	private ButtonHandler bh;
	private JMenuBar menuBar;
	private JMenu existPartMenu;
	
	public void init() {
		eyePosStart = new Vector3D(1.5e-10, 0, 0);
		gazePoint = new Vector3D();
		gazeChanging = false;
		finalGazePoint = new Vector3D();
		gazeObj = null;
		upDir = new Vector3d(0, 0, 1);

		addedByCursor = null;
		
		setLayout(new BorderLayout());

		bh = new ButtonHandler();
		menuBar = new JMenuBar();
		add("North", menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		JMenuItem[] fileMenuItems = {new JMenuItem("New Scene"), new JMenuItem("Open Scene"), new JMenuItem("Save Scene"), new JMenuItem("Save Scene As")};
		for (JMenuItem mI : fileMenuItems) {
			mI.setActionCommand("F-" + mI.getText());
			mI.addActionListener(bh);
			fileMenu.add(mI);
		}
		
		
		JMenu newParticleMenu = new JMenu("New Particles");
		menuBar.add(newParticleMenu);
		JMenu subPartMenu = new JMenu("Subatomic");
		JMenuItem[] subPartMenuItems = {new JMenuItem("Proton"), new JMenuItem("Electron"), new JMenuItem("Neutron")};
		newParticleMenu.add(subPartMenu);
		for (JMenuItem mI : subPartMenuItems) {
			mI.setActionCommand("P-S-" + mI.getText());
			mI.addActionListener(bh);
			subPartMenu.add(mI);
		}
		JMenu atomMenu = new JMenu("Atoms");
		newParticleMenu.add(atomMenu);
		JMenuItem[] atomMenuItems = {new JMenuItem("H"), new JMenuItem("C"), new JMenuItem("O"), new JMenuItem("N"), new JMenuItem("Custom")};
		for (JMenuItem mI : atomMenuItems) {
			mI.setActionCommand("P-A-" + mI.getText());
			mI.addActionListener(bh);
			atomMenu.add(mI);
		}
		JMenu moleculeMenu = new JMenu("Molecules");
		newParticleMenu.add(moleculeMenu);
		JMenuItem[] moleculeMenuItems = {new JMenuItem("H\u2082O"), new JMenuItem("CO\u2082"), new JMenuItem("O\u2082"), new JMenuItem("C\u2086H\u2081\u2082O\u2086"), new JMenuItem("Custom")};
		for (JMenuItem mI : moleculeMenuItems) {
			mI.setActionCommand("P-M-" + mI.getText());
			mI.addActionListener(bh);
			moleculeMenu.add(mI);
		}
		
		
		
		existPartMenu = new JMenu("Existing Particle Menu");
		menuBar.add(existPartMenu);
		
		
		JMenu toolMenu = new JMenu("Tools");
		menuBar.add(toolMenu);
		JMenuItem[] toolMenuItems = {new JMenuItem("Tips"), new JMenuItem("Turn Trails On"), new JMenuItem("Clear Trails"), new JMenuItem("Change Speed"), new JMenuItem("Change Physics Engine FPS")};
		for (JMenuItem mI : toolMenuItems) {
			mI.setActionCommand("T-" + mI.getText());
			mI.addActionListener(bh);
			toolMenu.add(mI);
		}

		canvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		canvas.setBackground(new Color(184, 204, 230));
		canvas.setDoubleBufferEnable(true);
		add("Center", canvas);
		universe = new SimpleUniverse(canvas);
		mainBranch = new BranchGroup();
		mainBranch.setCapability(BranchGroup.ALLOW_DETACH);
		mainBranch.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		mainBranch.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		mainBranch.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		mainBranch.setCapability(BranchGroup.ALLOW_BOUNDS_READ);
		mainBranch.setCapability(BranchGroup.ALLOW_BOUNDS_WRITE);

		DirectionalLight dLight = new DirectionalLight(true, new Color3f(Color.WHITE), new Vector3f(-1,-1,-1));
		dLight.setInfluencingBounds(new BoundingSphere(new Point3d(0, 0, 0), 100));
		mainBranch.addChild(dLight);

		AmbientLight aLight = new AmbientLight(true, new Color3f(0.35f,0.35f,0.35f));
		aLight.setInfluencingBounds(new BoundingSphere());
		mainBranch.addChild(aLight);

		orbit = new MovableOrbitBH(canvas, OrbitBehavior.REVERSE_ROTATE | OrbitBehavior.PROPORTIONAL_ZOOM | OrbitBehavior.STOP_ZOOM);
		orbit.setSchedulingBounds(new BoundingSphere());
		orbit.setMinRadius(1e-16);
		orbit.setZoomFactor(0.15);
		
		root = new BranchGroup();
		root.setCapability(BranchGroup.ALLOW_DETACH);
		root.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		root.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		root.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		root.setCapability(BranchGroup.ALLOW_BOUNDS_READ);
		root.setCapability(BranchGroup.ALLOW_BOUNDS_WRITE);
		view = universe.getViewer().getView();
		view.setBackClipDistance(100);
		view.setFrontClipDistance(1e-15);
		view.setWindowEyepointPolicy(View.RELATIVE_TO_FIELD_OF_VIEW);
		
		viewingTransformGroup = universe.getViewingPlatform().getViewPlatformTransform();
		
		
		
		
		viewingTransform = new Transform3D();
		viewingTransform.lookAt(eyePosStart.toJ3dPnt(), new Vector3D().toJ3dPnt(), upDir);
		viewingTransform.invert();
		viewingTransformGroup.setTransform(viewingTransform);
		orbit.setHomeTransform(viewingTransform);
		universe.getViewingPlatform().setViewPlatformBehavior(orbit);
		
		
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			  @Override
			  public void run() {
				  update();
			  }
			}, 10 * Engine.getScreenUpdateTicks(), Engine.getScreenUpdateTicks());
		
		
		setupKeybinds();
		
		setupMouseListener();
		
		
		root.addChild(mainBranch);
		universe.addBranchGraph(root);


		canvas.repaint();
	}

	public void update() {
		if (gazeObj != null) {
			if (gazeChanging) {
				Vector3D diff = finalGazePoint.sub(gazePoint);
				if (diff.getMagnitude() < 1e-13) {
					gazeChanging = false;
				} else if (diff.getMagnitude() <= gazeDiffSegment) {
					gazePoint = finalGazePoint;
				} else {
					gazePoint = gazePoint.add(diff.getDir().mult(gazeDiffSegment));
				}
				finalGazePoint = gazeObj.getPos();
			} else {
				gazePoint = gazeObj.getPos();
			}
					}
		Transform3D trans = new Transform3D();
		view.getViewPlatform().getLocalToVworld(trans);
		Vector3d pos = new Vector3d();
		trans.get(pos);
		Vector3D eyePos = new Vector3D(pos);
		if (eyePos.sub(gazePoint).getMagnitude() > 2e-10) {
			if (Engine.isLabelsSubatomic()) {
				Engine.setLabelsSubatomic(false);
			}
		} else {
			if (!Engine.isLabelsSubatomic()) {
				Engine.setLabelsSubatomic(true);
			}
		}
	}

	public SimpleUniverse getUniverse() {
		return universe;
	}

	public void addToUniv(Node node) {
		synchronized (UniverseEditingGK) {
			mainBranch.detach();
			mainBranch.addChild(node);
			root.addChild(mainBranch);
		}
	}
	
	public void setGazeObj(Particle part) {
		gazeObj = part;
		if (gazeObj != null) {
			finalGazePoint = gazeObj.getPos();
			gazeChanging = true;
			gazeDiffSegment = finalGazePoint.sub(gazePoint).getMagnitude() / Engine.getScreenUpdateTicks() / 2;
		}
	}

	public void addToExistingParticles(Particle part) {
		JMenu partMenu = new JMenu(part.toString());
		JMenuItem viewItem = new JMenuItem("View");
		JMenuItem removeItem = new JMenuItem("Remove");
		viewItem.setActionCommand("E-" + part.toString() + "-V");
		viewItem.addActionListener(bh);
		removeItem.setActionCommand("E-" + part.toString() + "-R");
		removeItem.addActionListener(bh);
		partMenu.add(viewItem);
		partMenu.add(removeItem);
		existPartMenu.add(partMenu);
	}
	
	public JMenuBar getJMenuBar() {
		return menuBar;
	}

	public Vector3D getGazePnt() {
		return gazePoint;
	}

	public void removeFromExistingParticles(Particle part) {
		for (Component partMenu : existPartMenu.getMenuComponents()) {
			if (((JMenu) partMenu).getText().equals(part.getName())) {
				existPartMenu.remove(partMenu);
				break;
			}
		}
	}

	public void removeFromUniv(Node node) {
		synchronized (UniverseEditingGK) {
			mainBranch.detach();
			mainBranch.removeChild(node);
			root.addChild(mainBranch);
		}
		
	}

	public Particle getGazeObj() {
		return gazeObj;
	}
	
	public void setParticleAddedByCursor(Molecule molecule) {
		addedByCursor = molecule;
	}
	
	public void setupMouseListener() {
		mouseHandler = new MouseHandler(canvas, mainBranch, new BoundingSphere());
		mainBranch.addChild(mouseHandler);
	}
	
	public void setupKeybinds() {
		ActionMap actionMap = getActionMap();
	    int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
	    InputMap inputMap = getInputMap(condition);
	      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESC");
	      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "SPACE");
	      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), "H");
	      actionMap.put("ESC", new KeyHandler("ESC"));
	      actionMap.put("SPACE", new KeyHandler("SPACE"));
	      actionMap.put("H", new KeyHandler("H"));
	}

	public Molecule getParticlesAddedByCursor() {
		return addedByCursor;
	}

	public Canvas3D getCanvas() {
		return canvas;
	}

	public int yesNoMsg(String string) {
		return JOptionPane.showConfirmDialog(this, string, "Virtual RXN 3 Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	}

	public void displayTips() {
		String tips = "Press the H key to toggle on and off labels on particles (H stands for hide)\nPress the ESC key to cancel/turn off addition mode after you've selected a new particle to add\nPress the SPACE key to freeze/unfreeze time\nOn some computers, it will appear as if atoms you add to the scene are not being added, try resizing your window and see if they appear";
		JOptionPane.showMessageDialog(this, tips, "Virtual RXN 3 Tips", JOptionPane.INFORMATION_MESSAGE);
	}

	private static class SliderSetter extends JPanel{
		private JSlider slider;
	    private JLabel label;
	    private int min, max, startVal;
	    private String type;

		
		private SliderSetter(String type, int min, int max, int startVal) {
			this.type = type;
			this.min = min;
			this.max = max;
			this.startVal = startVal;
	    }

		private void change() {
			switch (type) {
            case "Change Speed":
            	label.setText("10^" + String.valueOf(slider.getValue() / 10.0) + "x slower than real time");
            	Engine.setTimeMultiplier(Math.pow(10, slider.getValue() / 10.0 * -1));
            	break;
            case "Change Physics Engine FPS":
            	label.setText(slider.getValue() + " frames per second");
            	Engine.setPhysFPS(slider.getValue());
            	break;
        	default:
        		System.out.println("Unknown Slider Type");
            }
		}

		private static void createAndShowGui(String type, int min, int max, int startVal) {
	    	SliderSetter mainPanel = new SliderSetter(type, min, max, startVal);
	    	mainPanel.init();
	    	
	        JFrame frame = new JFrame(type);
	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        frame.getContentPane().add(mainPanel);
	        frame.pack();
	        frame.setLocationByPlatform(true);
	        frame.setVisible(true);
	    }

	    private void init() {
	    	slider = new JSlider(min, max, startVal);
			label = new JLabel("", SwingConstants.CENTER);
			change();
	        slider.addChangeListener(new ChangeListener() {
	            @Override
	            public void stateChanged(ChangeEvent e) {
	                change();
	            }
	        });

	        JPanel topPanel = new JPanel();
	        topPanel.add(new JLabel());
	        topPanel.add(label);

	        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	        setLayout(new BorderLayout(5, 5));
	        add(topPanel, BorderLayout.PAGE_START);
	        add(slider);
		}

		public static void start(String type, int min, int max, int startVal) {
	        SwingUtilities.invokeLater(() -> {
	            createAndShowGui(type, min, max, startVal);
	        });
	    }
	}

	public void changeSpeed() {
		SliderSetter.start("Change Speed", 12 * 10, 18 * 10, (int) (10 * Math.log10(Engine.getTimeMultiplier()) * -1));
	}
	
	public void changePhysFPS() {
		SliderSetter.start("Change Physics Engine FPS", 1, 300, Engine.getPhysFPS());
	}

	public void errorMsg(String string) {
		JOptionPane.showMessageDialog(this, string, "Virtual RXN 3 Error", JOptionPane.ERROR_MESSAGE);
	}

	public void reset() {
		eyePosStart = new Vector3D(1.5e-10, 0, 0);
		gazePoint = new Vector3D();
		gazeChanging = false;
		finalGazePoint = new Vector3D();
		gazeObj = null;
		addedByCursor = null;
		removeAllPartsFromUniv();
		existPartMenu.removeAll();
	}

	public void removeAllPartsFromUniv() {
		synchronized (UniverseEditingGK) {
			mainBranch.detach();
			ArrayList<Node> nodes = Collections.list(mainBranch.getAllChildren());
			for (int i = 0; i < nodes.size(); i++) {
				if (nodes.get(i) instanceof TransformGroup && i != 3) {
					mainBranch.removeChild(nodes.get(i));
				}
			}
			root.addChild(mainBranch);
		}
	}

}
