package application;

import java.io.File;
import java.util.Optional;

import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Hierarchy {

	private static final String HIERARCHY_IT_CLASS = "hierarchy-item" ;
	private static final String HIERARCHY_TAB_CLASS = "hierarchy-tab" ;
	private TitledPane wplTpPane, styleTpPane, scriptTpPane, mediaTpPane, otherTpPane;
	private VBox wplContent, styleContent, scriptContent, mediaContent, otherContent;
	private Accordion h;
	private ScrollPane sp;
	
		
	public Hierarchy() {		
		// Create panes
		wplTpPane = new TitledPane();
	    wplTpPane.setText("WPL");
 
	    styleTpPane = new TitledPane();
	    styleTpPane.setText("Stylesheet");
	 
	    scriptTpPane = new TitledPane();
	    scriptTpPane.setText("Script");
	       
	    mediaTpPane = new TitledPane();
	    mediaTpPane.setText("Media");
	       
	    otherTpPane = new TitledPane();
	    otherTpPane.setText("Other");
	    
	}
	
	public ScrollPane getHierarchy() {  
	       
	    // Create Root Pane.
	    h = new Accordion();
	    h.getPanes().addAll(wplTpPane, styleTpPane, scriptTpPane, mediaTpPane, otherTpPane);
	        
	    sp  = new ScrollPane();
	    sp.setHbarPolicy(ScrollBarPolicy.NEVER);
	    sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
	    
	    sp.setContent(h);
	    sp.setFitToHeight(true);
	    sp.setFitToWidth(true);
	    
	    SplitPane.setResizableWithParent(sp, false);
	    
	    return sp;
	}
	
	public void reload() {		
		
		Image imdelete = new Image(getClass().getResourceAsStream("file.png"));
		Button bdelete = new Button();
		bdelete.setTooltip(new Tooltip("New file"));
		bdelete.setGraphic(new ImageView(imdelete));
		
		String[] wplFiles = new File(Main.getProjectPath()+"wpl").list();
		wplContent = new VBox();
		wplContent.getStyleClass().add(HIERARCHY_TAB_CLASS);
		drawItems(wplContent, wplFiles, "wpl");
		wplTpPane.setContent(wplContent);
		
		String[] mediaFiles = new File(Main.getProjectPath()+"media").list();
		mediaContent = new VBox();
		mediaContent.getStyleClass().add(HIERARCHY_TAB_CLASS);
		drawItems(mediaContent, mediaFiles, "media");
		mediaTpPane.setContent(mediaContent);
		
		String[] stylesFiles = new File(Main.getProjectPath()+"styles").list();
		styleContent = new VBox();
		styleContent.getStyleClass().add(HIERARCHY_TAB_CLASS);
		drawItems(styleContent, stylesFiles, "styles");
		styleTpPane.setContent(styleContent);
		
		String[] scriptsFiles = new File(Main.getProjectPath()+"scripts").list();
		scriptContent = new VBox();
		scriptContent.getStyleClass().add(HIERARCHY_TAB_CLASS);
		drawItems(scriptContent, scriptsFiles, "scripts");
		scriptTpPane.setContent(scriptContent);
		
		String[] otherFiles = new File(Main.getProjectPath()+"other").list();
		otherContent = new VBox();
		otherContent.getStyleClass().add(HIERARCHY_TAB_CLASS);
		drawItems(otherContent, otherFiles, "other");
		otherTpPane.setContent(otherContent);
		
	}
	
	
	private void drawItems(VBox container, String[] files, String foldername) {
		if(files == null) {
			return;
		}
		for(String s : files) {
			HBox bp = new HBox();
			Label l = new Label(s);
			l.setTooltip(new Tooltip(s));
			String fullpath = Main.getProjectPath()+foldername+Main.SEPARATOR+s;
			l.prefWidthProperty().bind(container.widthProperty());
			l.setOnMouseClicked(value->{				
				Main.openTab(fullpath);
			});
			
			Image imdel = new Image(getClass().getResourceAsStream("del.png"));
			Button bdel = new Button();
			bdel.setOnMouseClicked(value->{ 
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Remove action");
				alert.setHeaderText("This action cannot be undone.");
				alert.setContentText("Are you sure you want to remove this file\n"
						+ "from the project folder?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					IOManager deleter = new IOManager();
					deleter.deleteFile(Main.getProjectPath()+foldername+Main.SEPARATOR+s);
					Main.reload();
				}	
			});
			bdel.setTooltip(new Tooltip("Remove file"));
			bdel.setGraphic(new ImageView(imdel));
			bdel.setVisible(false);
			
			bp.getChildren().addAll(l, bdel);
			bp.getStyleClass().add(HIERARCHY_IT_CLASS);
			
			bp.setOnMouseEntered(value->{
				bdel.setVisible(true);
			});
			
			bp.setOnMouseExited(value->{
				bdel.setVisible(false);
			});
			
			container.getChildren().add(bp);
		}
	}
	
	
}
