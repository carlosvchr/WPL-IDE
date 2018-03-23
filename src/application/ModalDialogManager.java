package application;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.filechooser.FileSystemView;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ModalDialogManager{

	public static final int WPL_FILE = 0;
	public static final int CSS_FILE = 1;
	public static final int JS_FILE = 2;
	public static final int EMPTY_FILE = 3;
	
	public static final String DIALOG_BTN_CLASS = "dialogbtn";
	public static final String DIALOG_LBLSC_CLASS = "dialoglblsc";
	
	/** Abre un nuevo dialogo para crear un archivo */
	public void showNewFileDialog(int type) {
		VBox rootpane = new VBox();
		rootpane.setSpacing(12);
		rootpane.setId("newfile-modal");
		Scene scene = new Scene(rootpane);
		scene.getStylesheets().add(getClass().getResource("modals.css").toExternalForm());
		
		Stage s = new Stage();
		s.setScene(scene);
		
		String titleName = "";
		final String fileExt;
		
		switch (type) {
		case WPL_FILE:
			titleName = "WPL file";
			fileExt = Main.WPL_EXT;
			break;
		case CSS_FILE:
			titleName = "CSS file";
			fileExt = ".css";
			break;
		case JS_FILE:
			titleName = "JavaScript file";
			fileExt = ".js";
			break;
		default:
			titleName = "Empty file";
			fileExt = "";
			break;
		}
		
		s.setTitle(titleName);
		
		Label nameextti = new Label("Output: ");
		Label nameext = new Label();
		Label info = new Label("Please, set the name of the file.");
		TextField tfName = new TextField();
		tfName.textProperty().addListener((observable, oldValue, newValue) -> {
			String name = tfName.getText().trim();
			if(name.length()>0) {
				nameext.setText(name+fileExt);
			}else {
				nameext.setText("");
			}
		});
		

		Button bCreate = new Button("Create");
		bCreate.getStyleClass().add(DIALOG_BTN_CLASS);
		bCreate.setOnMouseClicked(value->{
			IOManager writer = new IOManager();
			String filename = tfName.getText().trim() + fileExt;
			String folder = "other";
			if(filename.endsWith(Main.WPL_EXT)) { folder = "wpl"; }
			if(filename.endsWith(".css")) { folder = "styles"; }
			if(filename.endsWith(".js")) { folder = "scripts"; }
			try {
				writer.open(Main.getProjectPath()+folder+Main.SEPARATOR+filename, false, false);
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Main.reload();
			s.close();
		});
		
		Button bCancel = new Button("Cancel");
		bCancel.getStyleClass().add(DIALOG_BTN_CLASS);
		bCancel.setOnMouseClicked(value->{
			s.close();
		});
		
		HBox btnbox = new HBox();
		btnbox.setAlignment(Pos.BASELINE_RIGHT);
		btnbox.getChildren().addAll(bCreate, bCancel);
		
		BorderPane nameextCont = new BorderPane();
		ScrollPane scp = new ScrollPane();
		scp.getStyleClass().add(DIALOG_LBLSC_CLASS);
		scp.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scp.setVbarPolicy(ScrollBarPolicy.NEVER);
		scp.setContent(nameext);
		nameextCont.setLeft(nameextti);
		nameextCont.setCenter(scp);
		
		rootpane.getChildren().addAll(info, tfName, nameextCont, btnbox);
		
		s.initOwner(null);
		s.setAlwaysOnTop(true);
		s.initModality(Modality.APPLICATION_MODAL); 
		s.setMinHeight(150);
		s.setMinWidth(220);
		s.showAndWait();
	}
	
	
	/** Abre un dialogo para crear un proyecto */
	public void showNewProjectDialog() {
		VBox rootpane = new VBox();
		rootpane.setId("newpj-modal");
		rootpane.setSpacing(12);
		Scene scene = new Scene(rootpane);
		scene.getStylesheets().add(getClass().getResource("modals.css").toExternalForm());
		Stage s = new Stage();
		s.setScene(scene);
		s.setTitle("New project");
		
		HBox chooserCont = new HBox();
		TextField pathTf = new TextField("");
		pathTf.setEditable(false);
		
		Button chooseFolder = new Button("Examine");
		chooseFolder.setOnAction(value->{
			DirectoryChooser dch = new DirectoryChooser();
			dch.setInitialDirectory(FileSystemView.getFileSystemView().getHomeDirectory());
			dch.setTitle("Project location");
			File selectedDir = dch.showDialog(s);
			if(selectedDir != null) {
				pathTf.setText(selectedDir.getAbsolutePath());
			}
		});
		chooserCont.getChildren().addAll(pathTf, chooseFolder);

		Label infopath = new Label("Please, choose the project location.");
		
		Label nameextti = new Label("Output: ");
		Label nameext = new Label();
		Label info = new Label("Please, set the name of the project.");
		TextField tfName = new TextField();
		tfName.textProperty().addListener((observable, oldValue, newValue) -> {
			String name = tfName.getText().trim();
			if(name.length()>0) {
				nameext.setText(pathTf.getText() +  Main.SEPARATOR + name);
			}else {
				nameext.setText("");
			}
		});
		

		Button bCreate = new Button("Create");
		bCreate.getStyleClass().add(DIALOG_BTN_CLASS);
		bCreate.setOnMouseClicked(value->{
			String pjname = tfName.getText().trim();
			String pjpath = pathTf.getText().trim();
			
			// Si se han seteado el nombre del proyecto y la ruta creamos el proyecto
			if(pjname != null && pjpath != null) {
				if(pjname.length() > 0 && pjpath.length() > 0) {
					String pjfolder = pjpath + Main.SEPARATOR + pjname;
					File mainDir = new File(pjfolder);
					mainDir.mkdir();
					String mainDirPath = pjfolder + Main.SEPARATOR;
					File wplDir = new File(mainDirPath+Main.FOLDER_WPL);
					wplDir.mkdir();
					File mediaDir = new File(mainDirPath+Main.FOLDER_MEDIA);
					mediaDir.mkdir();
					File stylesDir = new File(mainDirPath+Main.FOLDER_STYLES);
					stylesDir.mkdir();
					File scriptsDir = new File(mainDirPath+Main.FOLDER_SCRIPTS);
					scriptsDir.mkdir();
					File otherDir = new File(mainDirPath+Main.FOLDER_OTHER);
					otherDir.mkdir();
					File outputDir = new File(mainDirPath+Main.FOLDER_OUTPUT);
					outputDir.mkdir();
					IOManager writer = new IOManager();
					try {
						writer.open(mainDirPath+Main.WPLPJ_FILE, false, false);
						writer.putLine(Main.PJ_NAME_KEY+pjname);
						String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
						writer.putLine(Main.PJ_DATE_KEY+timeStamp);
					} catch (IOException e) {
						e.printStackTrace();
					}finally {
						try {
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					Main.closeCurrentProject();
					Main.setProjectPath(mainDirPath);
				}
			}
			
			Main.reload();
			s.close();
		});
		
		Button bCancel = new Button("Cancel");
		bCancel.getStyleClass().add(DIALOG_BTN_CLASS);
		bCancel.setOnMouseClicked(value->{
			s.close();
		});
		
		HBox btnbox = new HBox();
		btnbox.setAlignment(Pos.BASELINE_RIGHT);
		btnbox.getChildren().addAll(bCreate, bCancel);
		
		BorderPane nameextCont = new BorderPane();
		ScrollPane scp = new ScrollPane();
		scp.getStyleClass().add(DIALOG_LBLSC_CLASS);
		scp.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scp.setVbarPolicy(ScrollBarPolicy.NEVER);
		scp.setContent(nameext);
		nameextCont.setLeft(nameextti);
		nameextCont.setCenter(scp);
		
		rootpane.getChildren().addAll(infopath, chooserCont, info, tfName, nameextCont, btnbox);
		
		s.initOwner(null);
		s.setAlwaysOnTop(true);
		s.initModality(Modality.APPLICATION_MODAL); 
		s.setMinHeight(150);
		s.setMinWidth(220);
		s.showAndWait();
		
	}
	
}
