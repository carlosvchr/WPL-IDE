package application;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MenuPane {

	public MenuBar getMenu() {
		final Menu menuFile = new Menu("File");
		final Menu menuEdit = new Menu("Edit");
		final Menu menuHelp = new Menu("Help");
		 
		ModalDialogManager mdm = new ModalDialogManager();
		
		final Menu miNew = new Menu("  New           ", null);
		final MenuItem minProject = new MenuItem("  WPL Project   ");
		minProject.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		minProject.setOnAction(value->{ mdm.showNewProjectDialog(); });
		
		final MenuItem minWpl = new MenuItem("  WPL File      ");
		minWpl.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		minWpl.setOnAction(value->{ mdm.showNewFileDialog(ModalDialogManager.WPL_FILE); });
		
		final MenuItem minCss = new MenuItem("  CSS File      ");
		minCss.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		minCss.setOnAction(value->{ mdm.showNewFileDialog(ModalDialogManager.CSS_FILE); });
		
		final MenuItem minJs = new MenuItem("  JS File       ");
		minJs.setAccelerator(new KeyCodeCombination(KeyCode.J, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		minJs.setOnAction(value->{ mdm.showNewFileDialog(ModalDialogManager.JS_FILE); });
		
		final MenuItem minFile = new MenuItem("  Empty File   ");
		minFile.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		minFile.setOnAction(value->{ mdm.showNewFileDialog(ModalDialogManager.EMPTY_FILE); });
		
		miNew.getItems().setAll(minProject, minWpl, minCss, minJs, minFile);
		
		
		final MenuItem miOpen = new MenuItem("  Open          ", null);
		miOpen.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open project");
				fileChooser.getExtensionFilters().addAll(
					    new ExtensionFilter("WPL Project", Main.WPLPJ_FILE));
				File file = fileChooser.showOpenDialog(Main.getStage());
                if (file != null) {
                    Main.setProjectPath(file.toString().replaceAll(Main.WPLPJ_FILE, ""));
                    Main.reload();
                }
			}		 
		});
		
		final MenuItem miSave = new MenuItem("  Save          ", null);
		miSave.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Main.saveCurrentTab();
			}		 
		});
		
		final MenuItem miSaveAll = new MenuItem("  Save all      ", null);
		miSaveAll.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Main.saveAllTabs();
			}		 
		});
		
		final MenuItem miImport = new MenuItem("  Import        ", null);
		miImport.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Main.importResources();
			}		 
		});
		
		final MenuItem miRefresh = new MenuItem("  Reload       ", null);
		miRefresh.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Main.reload();
			}		 
		});
		 
		final MenuItem miProjectFolder = new MenuItem("  Project folder", null);
		miProjectFolder.setOnAction(value->{
			new Thread(() -> {
		           try {
		               Desktop.getDesktop().open( new File( Main.getProjectPath() ) );
		           } catch (IOException e1) {
		               e1.printStackTrace();
		           }
		       }).start();
		});
		
		final MenuItem miExit = new MenuItem("  Exit          ", null);
		miExit.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Main.beforeExit();
			}		 
		});
		
		final MenuItem meCopy = new MenuItem("  Copy          ", null);
		meCopy.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Main.editorCopy();
			}		 
		});
		
		final MenuItem mePaste = new MenuItem("  Paste         ", null);
		mePaste.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Main.editorPaste();
			}		 
		});
		
		final MenuItem meUndo = new MenuItem("  Undo          ", null);
		meUndo.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Main.editorUndo();
			}		 
		});
		
		final MenuItem meRedo = new MenuItem("  Redo          ", null);
		meRedo.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Main.editorRedo();
			}		 
		});
		
		final MenuItem meCut = new MenuItem("  Cut           ", null);
		meCut.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Main.editorCut();
			}		 
		});
		
		final MenuItem meSelectAll = new MenuItem("  Select all    ", null);
		meSelectAll.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Main.editorSelectAll();
			}		 
		});
		
		final MenuItem meFind = new MenuItem("  Find        ", null);
		meFind.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				// Abrir un dialogo
			}		 
		});
		
		final MenuItem meReplace = new MenuItem("  Replace     ", null);
		meReplace.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				// Abrir un dialogo
			}		 
		});
		 
		final MenuItem mhAbout = new MenuItem("  About               ", null);
		mhAbout.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Program information");
				alert.setHeaderText(null);
				Label txt = new Label("WPLIDE is an IDE created specifically for WPL language. This\n"+
									  "is IDE is not finished yet, many features will be implemented\n"+
									  "in the future, and it may content some errors.");
				txt.setMaxWidth(300);
				alert.getDialogPane().setContent(txt);
				alert.show();
			}		 
		});
		 
		final MenuItem mhAuthor = new MenuItem("  Author              ", null);
		mhAuthor.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Program Author");
				alert.setHeaderText(null);
				alert.getDialogPane().setContent(new Label("Carlos Vicente Charco is the creator of the "+
						"WPL language and compiler as well as the creator of this IDE.\n" +
						"This IDE and the language is completely free to use and "+
						"everything you create with this is completely yours."));
				alert.show();
			}		 
		});
		 
		final MenuItem mhVersion = new MenuItem("  Version             ", null);
		mhVersion.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Program Version");
				alert.setHeaderText(null);
				alert.getDialogPane().setContent(new Label("Current version is 1.0.\nPhase alpha."));
				alert.show();
			}		 
		});
		 
		final MenuItem mhAck = new MenuItem("  Acknowledgments     ", null);
		mhAck.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Acknowledgments");
				alert.setHeaderText("Special thanks to");
				alert.getDialogPane().setContent(new Label("Fontawesome for allowing use of their icon set:\n"+
				"https://fontawesome.com/license\n\n"+
				"RichTextFX for provide the neccesary resources to create the editor panel:\n"+
				"https://github.com/FXMisc/RichTextFX"));
				alert.show();
			}		 
		});
		
		
		menuFile.getItems().add(miNew);
		menuFile.getItems().add(miOpen);
		miOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		menuFile.getItems().add(miSave);
		miSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		menuFile.getItems().add(miSaveAll);
		miSaveAll.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		menuFile.getItems().add(miImport);
		miImport.setAccelerator(new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN));
		menuFile.getItems().add(miRefresh);
		miRefresh.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		menuFile.getItems().add(miProjectFolder);
		menuFile.getItems().add(new SeparatorMenuItem());
		menuFile.getItems().add(miExit);
		
		menuEdit.getItems().add(meUndo);
		meUndo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
		menuEdit.getItems().add(meRedo);
		meRedo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
		menuEdit.getItems().add(new SeparatorMenuItem());
		menuEdit.getItems().add(meCopy);
		meCopy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
		menuEdit.getItems().add(mePaste);
		mePaste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
		menuEdit.getItems().add(meCut);
		meCut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
		menuEdit.getItems().add(meSelectAll);
		meSelectAll.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN));
		menuEdit.getItems().add(new SeparatorMenuItem());
		menuEdit.getItems().add(meFind);
		meFind.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
		menuEdit.getItems().add(meReplace);
		meReplace.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
		
		menuHelp.getItems().add(mhAbout);
		menuHelp.getItems().add(mhAuthor);
		menuHelp.getItems().add(mhVersion);
		mhVersion.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		menuHelp.getItems().add(mhAck);
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(menuFile, menuEdit, menuHelp);
		menuBar.setId("menubar");
		
		return menuBar;
	}
	
}
