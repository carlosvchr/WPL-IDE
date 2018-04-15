package application;

import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class WebViewDialog{

	public WebViewDialog(String url) {
		Scene scene;
		WebView wv = new WebView();
		WebEngine engine = wv.getEngine();
		engine.load(url);
		
		scene = new Scene(wv, 600, 400);
		
		Stage stage = new Stage();
		stage.setMaximized(true);
		stage.setScene(scene);
		stage.show();
		stage.toFront();
	}

	
}
