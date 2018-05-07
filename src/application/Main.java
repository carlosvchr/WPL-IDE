package application;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import mainpackage.CompResult;
import mainpackage.WebPL;


public class Main extends Application {
	
	public static final String SEPARATOR = System.getProperty("file.separator");
	
	public static final String WPL_EXT = ".wepl";
	public static final String WPLPJ_FILE = "project.wpj";
	
	public static final String PJ_DATE_KEY = "#PJDATE#";
	public static final String PJ_NAME_KEY = "#PJNAME#";
	
	public static final String PJ_PATH_KEY = "#PJPATH#";
	public static final String PJ_TABS_KEY = "#PJTABS#";
	public static final String PJ_SELTAB_KEY = "#PJSELTAB#";
	
	public static final String FOLDER_WPL = "wpl";
	public static final String FOLDER_MEDIA = "media";
	public static final String FOLDER_STYLES = "styles";
	public static final String FOLDER_SCRIPTS = "scripts";
	public static final String FOLDER_OTHER = "other";
	public static final String FOLDER_OUTPUT = "output";
	public static final String LOG = "log.txt";
	public static final String INDEX = "index.wepl";
	public static final String OUTPUTNAME = "index.html";
	
	private static Stage stage;
	private static String projectPath;
	private static Hierarchy hierarchy;
	private static RichTextCode editor;
	private static String pjname = "";
	private static String date = "";
	private static ConsoleOutput console;
	
	private static WebPL compiler;
	
	private double xOffset, yOffset;
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.initStyle(StageStyle.UNDECORATED);
		Platform.setImplicitExit(false);
		stage = primaryStage;
		stage.getIcons().add(new Image(getClass().getResourceAsStream("logo16x16.png")));
		stage.getIcons().add(new Image(getClass().getResourceAsStream("logo32x32.png")));
		stage.getIcons().add(new Image(getClass().getResourceAsStream("logo48x48.png")));
		stage.getIcons().add(new Image(getClass().getResourceAsStream("logo64x64.png")));
		
		// Consumimos el evento de salir para que se ejecute nuestro codigo por defecto
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent event) {
		        event.consume();
		        beforeExit();
		    }
		});
		stage.setFullScreenExitHint("");
		stage.setMinHeight(140);
		stage.setMinWidth(200);
		
		compiler = new WebPL();
		
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,800,600);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("IDE WPL");
			primaryStage.show();
			
			BorderPane topContainer = new BorderPane();
			topContainer.setId("menubarcontainer");
			MenuPane menu = new MenuPane();
			HBox windowButtons = new HBox();
			Image closefile = new Image(getClass().getResourceAsStream("close.png"));
			Button closebt = new Button();
			closebt.setGraphic(new ImageView(closefile));
			closebt.getStyleClass().add("windowbtn");
			closebt.setCursor(Cursor.HAND);
			closebt.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					Main.beforeExit();
				}
			});
			Image minimizefile = new Image(getClass().getResourceAsStream("minimize.png"));
			Button minimizebt = new Button();
			minimizebt.setGraphic(new ImageView(minimizefile));
			minimizebt.getStyleClass().add("windowbtn");
			minimizebt.setCursor(Cursor.HAND);
			minimizebt.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					stage.setIconified(true);
				}
			});
			Image maximizefile = new Image(getClass().getResourceAsStream("maximize.png"));
			Button maximizebt = new Button();
			maximizebt.setGraphic(new ImageView(maximizefile));
			maximizebt.getStyleClass().add("windowbtn");
			maximizebt.setCursor(Cursor.HAND);
			maximizebt.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event) {
					if(stage.isFullScreen())
						stage.setFullScreen(false);
					else
						stage.setFullScreen(true);
				}
			});
			windowButtons.setOnMousePressed(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	            	windowButtons.setCursor(Cursor.MOVE);
	                xOffset = primaryStage.getX() - event.getScreenX();
	                yOffset = primaryStage.getY() - event.getScreenY();
	            }
	        });
			
			windowButtons.setOnMouseDragged(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	            	if(stage.isFullScreen()) {
	            		stage.setFullScreen(false);
	            	}
	                primaryStage.setX(event.getScreenX() + xOffset);
	                primaryStage.setY(event.getScreenY() + yOffset);
	            }
	        });
			
			windowButtons.setOnMouseReleased(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					windowButtons.setCursor(Cursor.DEFAULT);
				}
			});
			
			windowButtons.setAlignment(Pos.CENTER_RIGHT);
			windowButtons.getChildren().addAll(minimizebt, maximizebt, closebt);
			topContainer.setCenter(menu.getMenu());
			topContainer.setTop(windowButtons);
			root.setTop(topContainer);
			
			BorderPane toolMidPane = new BorderPane();
			Toolbar tb = new Toolbar();
			toolMidPane.setTop(tb.getToolbar());
			
			SplitPane hsp = new SplitPane();
			hsp.setId("middlepane");
			hsp.setOrientation(Orientation.HORIZONTAL);
			hsp.setDividerPositions(0.25);
			
			
			SplitPane codeConsole = new SplitPane();
			
			editor = new RichTextCode();
			
			codeConsole.getItems().addAll(editor.getCodeEditor(), loadConsole());
			codeConsole.setOrientation(Orientation.VERTICAL);
			codeConsole.setDividerPositions(0.7);
			
			hierarchy = new Hierarchy();
			
			hsp.getItems().addAll(hierarchy.getHierarchy(), codeConsole);
			
			toolMidPane.setCenter(hsp);	
			
			root.setCenter(toolMidPane);
			root.setBottom(loadFooter());
						
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		launch(args);
	}
	

	/** Carga la consola de errores */
	public static StackPane loadConsole() {
		console = new ConsoleOutput(projectPath+LOG);
		CodeArea ca = console.getConsole();
		StackPane sp = new StackPane(new VirtualizedScrollPane<>(ca));
		ca.prefWidthProperty().bind(sp.widthProperty());
		//ca.prefHeightProperty().bind(p.heightProperty());
		sp.setId("consolepane");
		SplitPane.setResizableWithParent(sp, false);
		
		return sp;
	}
	
	/** Carga la barra inferior que contiene la barra de progreso */
	public BorderPane loadFooter() {
		BorderPane footer = new BorderPane();
		
		Label lversion = new Label("Version 1.0");
		lversion.setId("lblversion");
		Image resizeim = new Image(getClass().getResourceAsStream("resize.png"));
		Button resizebt = new Button();
		resizebt.setGraphic(new ImageView(resizeim));
		resizebt.getStyleClass().add("resizebtn");
		resizebt.setCursor(Cursor.HAND);

		resizebt.setOnMouseDragged(new EventHandler<MouseEvent>() {
	        public void handle(MouseEvent event) {
				stage.setWidth(stage.getWidth()+event.getX()-16);
				stage.setHeight(stage.getHeight()+event.getY()-16);
	        }
	    });
		
		resizebt.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				resizebt.setCursor(Cursor.SE_RESIZE);
			}
		});
		
		resizebt.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				resizebt.setCursor(Cursor.HAND);
			}
		});
		
		
		footer.setLeft(lversion);
		footer.setRight(resizebt);
		
		footer.setId("footer");
		
		return footer;
	}
	
	public static void toggleFullScreen() {		
		stage.setFullScreen(!stage.isFullScreen());
	}
	
	public static Stage getStage() {
		return stage;
	}
	
	public static void setProjectPath(String path) {
		projectPath = path;
	}
	
	public static String getProjectPath() {
		return projectPath;
	}
	
	public static void openTab(String path) {
		editor.addTab(path);
	}
	
	public static void saveCurrentTab() {
		editor.saveCurrentTab();
	}
	
	public static void saveAllTabs() {
		editor.saveAllTabs();
	}
	
	public static void loadWorkspace() {
		IOManager reader = new IOManager();
		try {
			reader.open(getProjectPath()+WPLPJ_FILE, true, false);
			String line;
			while((line = reader.getLine()) != null) {
				if(line.startsWith(PJ_TABS_KEY)) {
					line = line.replaceAll(PJ_TABS_KEY, "");
					// Si existe el fichero
					if(new File(line).exists()) {
						openTab(line);
					}
				}
				if(line.startsWith(PJ_SELTAB_KEY)) {
					line = line.replaceAll(PJ_SELTAB_KEY, "");
					// Si existe el fichero
					if(new File(line).exists()) {
						editor.selectTab(line);
					}
				}
				if(line.startsWith(PJ_NAME_KEY)) {
					line = line.replaceAll(PJ_NAME_KEY, "");
					pjname = line;			
				}
				if(line.startsWith(PJ_DATE_KEY)) {
					line = line.replaceAll(PJ_DATE_KEY, "");
					date = line;			
				}
			}
			// Y refrescamos
			hierarchy.reload();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void saveWorkspace() {
		IOManager writer = new IOManager();
		try {
			writer.open(getProjectPath()+WPLPJ_FILE, false, false);
			if(pjname.length() == 0) {
				if(projectPath != null) {
					String[] pathdiv = projectPath.split(SEPARATOR);
					if(pathdiv != null) {
						pjname = pathdiv[pathdiv.length-1];
					}
				}
			}
			if(date.length() == 0) {
				date = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			}
			writer.putLine(PJ_NAME_KEY+pjname);
			writer.putLine(PJ_DATE_KEY+date);
			ArrayList<CodeTab> tabs = editor.getCodeTabs();
			for(int i=0; i<tabs.size(); i++) {
				writer.putLine(PJ_TABS_KEY+tabs.get(i).getPath());
			}
			String selTab = editor.getSelectedTabPath();
			if(selTab != null) {
				writer.putLine(PJ_SELTAB_KEY+selTab);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void reload() {
		saveWorkspace();
		editor.closeAllTabs();
		loadWorkspace();
	}
	
	public static void importResources() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import files");
        List<File> list =
                fileChooser.showOpenMultipleDialog(Main.getStage());
        if (list != null) {
            for (File file : list) {
            	String folder;
                if(file.getAbsolutePath().endsWith(WPL_EXT)) {
                	folder = "wpl";
                }else if(file.getAbsolutePath().endsWith(".css")) {
                	folder = "styles";
                }else if(file.getAbsolutePath().endsWith(".js")) {
                	folder = "scripts";
                }else if(file.getAbsolutePath().toLowerCase().endsWith(".png") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".jpg") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".jpeg") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".bmp") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".gif") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".svg") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".wave") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".pcm") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".webm") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".vorbis") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".vp8") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".theora") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".ogg") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".mp3") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".mp4") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".aac") ||
                		file.getAbsolutePath().toLowerCase().endsWith(".h.264")) {
                	folder = "media";
                }else {
                	folder = "other";
                }
                try {
					Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(projectPath+folder+SEPARATOR+file.getName()),
							REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
        hierarchy.reload();
	}
	
	
	public static void editorCopy() {
		editor.copy();
	}
	
	public static void editorSelectAll() {
		editor.selectAll();
	}
	
	public static void editorPaste() {
		editor.paste();
	}
	
	public static void editorCut() {
		editor.cut();
	}
	
	public static void editorUndo() {
		editor.undo();
	}
	
	public static void editorRedo() {
		editor.redo();
	}
	
	public static void editorReplace(String val, String nval) {
		editor.replaceAll(val, nval);
	}
	
	public static void editorFind(String val, int index) {
		editor.find(val, index);
	}
	
	public static void beforeExit() {
		if(!editor.isEverythingSaved()) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Exit action");
			alert.setHeaderText("Some files are not saved.");
			alert.setContentText("Do you want to save your progress before exit?");
			
			ButtonType btYes = new ButtonType("Save and exit");
			ButtonType btNo = new ButtonType("Exit without saving");
			ButtonType btCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

			alert.getButtonTypes().setAll(btYes, btNo, btCancel);
			alert.getButtonTypes().stream()
				.map(alert.getDialogPane()::lookupButton)
				.forEach(n -> ButtonBar.setButtonUniformSize(n, false));
	
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == btYes){
				saveAllTabs();
			} else if (result.get() == btCancel) {
				return;
			}
		}
		saveWorkspace();
		Platform.exit();
		System.exit(0);
	}
	
	
	public static void closeCurrentProject() {
		// TODO
	}
	
	public static CompResult compile() {
		File f = new File(projectPath+LOG);
		if(f.exists())f.delete();
		System.out.println("COMPILA");
		CompResult res = compiler.compile(projectPath+FOLDER_WPL+SEPARATOR+INDEX, projectPath+FOLDER_OUTPUT+SEPARATOR+OUTPUTNAME);
		console.printErrors(res.output());
		return res;
	}
	
	
}
