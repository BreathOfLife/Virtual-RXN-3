package Control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import PhysObjects.Particle;

public class FileIO {

	public static String mostRecentSavePath = null;
	
	public static void newScene() {
		int result = Engine.getDisp().yesNoMsg("Do you want to save this scene first?");
		if (result == JOptionPane.YES_OPTION) {
			saveScene();
			clearScene();
		} else if (result == JOptionPane.NO_OPTION) {
			clearScene();
		}
	}
	
	public static void saveScene() {
		if (mostRecentSavePath != null) {
			saveScene(mostRecentSavePath);
		} else {
			saveSceneAs();
		}
	}
	
	public static void saveScene(String path) {
		String[] splitPath = path.split(".");
		if (splitPath.length == 0) {
			path = path + ".VRXN3";
		} else if (splitPath.length != 2 || !splitPath[1].equals("VRXN3")) {
			path = splitPath[0] + ".VRXN3";
		}
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
			oos.writeObject(Engine.getAllParticles());
		} catch (FileNotFoundException e) {
			Engine.getDisp().errorMsg("File not found thus file is NOT saved!");
		} catch (IOException e) {
			e.printStackTrace();
			Engine.getDisp().errorMsg("Program does not have permission to add file in this folder");
		}
	}
	
	public static void clearScene() {
		Engine.resetParticles();
		Engine.resetDisp();
		mostRecentSavePath = null;
	}
	
	public static void saveSceneAs() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save As");
		int userSelection = fileChooser.showSaveDialog(Engine.getDisp());
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File fileToSave = fileChooser.getSelectedFile();
		    String path = fileToSave.getAbsolutePath();
		    saveScene(path);
		} else {
			Engine.getDisp().errorMsg("File NOT saved!");
		}
	}
	
	public static void openScene() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Open VRXN3 file");
		int userSelection = fileChooser.showOpenDialog(Engine.getDisp());
		if (userSelection == JFileChooser.APPROVE_OPTION) {
		    File fileToSave = fileChooser.getSelectedFile();
		    String path = fileToSave.getAbsolutePath();
		    openScene(path);
		}
	}
	
	public static void openScene(String path) {
		newScene();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
			Engine.setAllParticles((ArrayList<Particle>) ois.readObject());
			Engine.startAllParticles();
		} catch (FileNotFoundException e) {
			Engine.getDisp().errorMsg("File not found");
		} catch (IOException e) {
			Engine.getDisp().errorMsg("Program does not have permission to read file in this folder");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		mostRecentSavePath = path;
	}
}
