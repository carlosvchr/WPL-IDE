package application;

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class Toolbar {

	
	public HBox getToolbar() {
		
		HBox cont = new HBox();
		cont.setId("toolbar");
		
		Image imfile = new Image(getClass().getResourceAsStream("file.png"));
		Button bfile = new Button();
		bfile.setTooltip(new Tooltip("New file"));
		bfile.setGraphic(new ImageView(imfile));
		bfile.setCursor(Cursor.HAND);
		bfile.setOnMouseClicked(value->{
			ModalDialogManager mdm = new ModalDialogManager();
			mdm.showNewFileDialog(ModalDialogManager.EMPTY_FILE);
		});
		
		Image imsave = new Image(getClass().getResourceAsStream("save.png"));
		Button bsave = new Button();
		bsave.setOnMouseClicked(value->{ Main.saveCurrentTab(); });
		bsave.setTooltip(new Tooltip("Save"));
		bsave.setCursor(Cursor.HAND);
		bsave.setGraphic(new ImageView(imsave));
		
		Image imsaveall = new Image(getClass().getResourceAsStream("saveall.png"));
		Button bsaveall = new Button();
		bsaveall.setOnMouseClicked(value->{ Main.saveAllTabs(); });
		bsaveall.setTooltip(new Tooltip("Save all"));
		bsaveall.setCursor(Cursor.HAND);
		bsaveall.setGraphic(new ImageView(imsaveall));
		
		Image imimport = new Image(getClass().getResourceAsStream("import.png"));
		Button bimport = new Button();
		bimport.setCursor(Cursor.HAND);
		bimport.setOnMouseClicked(value->{
			Main.importResources();
		});
		bimport.setTooltip(new Tooltip("Import"));
		bimport.setGraphic(new ImageView(imimport));
		
		Image imrun = new Image(getClass().getResourceAsStream("run.png"));
		Button brun = new Button();
		brun.setCursor(Cursor.HAND);
		brun.setOnMouseClicked(value->{
			Main.saveAllTabs();
			if(Main.compile().isCompilationSuccessfully())
				new WebViewDialog("file://"+Main.getProjectPath() + Main.FOLDER_OUTPUT + Main.SEPARATOR + Main.OUTPUTNAME);
		});
		brun.setTooltip(new Tooltip("Compile and run"));
		brun.setGraphic(new ImageView(imrun));
		
		
		cont.getChildren().addAll(bfile, bsave, bsaveall, bimport, brun);
		
		return cont;
	}
	
}
