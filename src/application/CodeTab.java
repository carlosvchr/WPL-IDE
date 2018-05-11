package application;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.richtext.model.TwoDimensional.Position;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class CodeTab extends Tab {

	private String path;
	private CodeArea codeArea;
	private boolean isSaved;
	
	private final String[] KEYWORDS = new String[] {
            "define", "import", "include", "accordion", "dropdown", "hbox", "html", "modal", "row-header", "row",
            "sidebar", "table", "vbox", "audio", "button", "checkbox", "image", "label", "radiobutton", "textfield",
            "video"
    };
    
    private final String[] ATTRIBUTES = new String[] {
            "align", "alt", "animation", "author", "autoplay", "bgcolor", "border-color", "border-radius", "border",
            "charset", "class", "controls", "description", "dropdown-type", "effect", "elevation", "filter-dropdown",
            "filter-table", "fixed-position", "font-family", "font-size", "height", "id", "keywords", "lang", "link", "loop", "margin",
            "muted", "onchange", "onclick", "padding", "pageicon", "placeholder", "poster", "preload", "radiogroup", 
            "redirect", "selected", "src", "table-attrs", "text-align", "text-color", "text-decoration", "title", 
            "tooltip", "width"
    };
    
    private final String[] VALUES = new String[] {
    		"zoom", "fading", "fade-in", "spin", "move-up", "move-right", "move-down", "move-left", "true", "false",
    		"clickable", "hoverable", "opacity-min", "opacity-max", "opacity", "sepia-min", "sepia-max", "sepia", 
    		"grayscale-min", "grayscale-max", "grayscale", "bordered", "centered", "hoverable", "striped", "right", "left", "center",
    		"bold", "italic", "underline", "overline", "strikethrough", "top-left", "top-right", "bottom-left", "bottom-right"
    };

    private final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private final String ATTR_PATTERN = "\\b(" + String.join("|", ATTRIBUTES) + ")\\b";
    private final String VALUE_PATTERN = "\\b(" + String.join("|", VALUES) + ")\\b";
    private final String SEMICOLON_PATTERN = "\\:"+"|"+"\\,";
    private final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private final String COMMENT_PATTERN = "--[^\n]*";
    private final String NUMBER_PATTERN = "\\b((0"+"|"+"([1-9][0-9]*))(.[0-9]*[1-9][0-9]*)?)((px)|(%))?\\b";
    private final String COLOR_PATTERN = "\\#[a-fA-F0-9]{6}\\b";
    private final String VAR_PATTERN = "\\$[a-zA-Z_][a-zA-Z0-9_]*";
    private final String REST_PATTERN = "[^ \n]+ ";

    private final Pattern PATTERN = Pattern.compile(
    		"(?<VALUE>" + VALUE_PATTERN + ")"
    		+ "|(?<ATTRIBUTE>" + ATTR_PATTERN + ")"
            + "|(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
            + "|(?<COLOR>" + COLOR_PATTERN + ")"
            + "|(?<VAR>" + VAR_PATTERN + ")"
            + "|(?<REST>" + REST_PATTERN + ")"
    );
    
	public CodeTab(String title, String path, String content, RichTextCode parent) {
		this.path = path;
		codeArea = new CodeArea();
		
		setText(title);
		setOnClosed(value->{
			if(!isSaved) {
				Alert closesave = new Alert(AlertType.WARNING);
				closesave.setTitle("Warning!");
				closesave.setHeaderText("The progress didn't be saved.");
				closesave.setContentText("If you don't save your progress now, you will\nnot be able to recover."
						+ "\n\nDo you want to save your progress now?");
				
				ButtonType buttonTypeYes = new ButtonType("Yes");
				ButtonType buttonTypeNo = new ButtonType("No");
				
				closesave.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
	
				Optional<ButtonType> result = closesave.showAndWait();
				if (result.get() == buttonTypeYes){
				    saveTab();
				}
			}
			
			parent.getCodeTabs().remove(this);
		});
		
		codeArea = new CodeArea();
        codeArea.setId("editorContainer");
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setStyle("-fx-font-family: 'Roboto Mono'; -fx-font-size: 10pt;");

        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) 
                .subscribe(change -> {
                	if(codeArea.getText().length()>0) {
                		codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
                	}
                    codeArea.getUndoManager().mark();
                    setUnsaved();
                });
        
        codeArea.setOnKeyPressed(value->{
        	// Mapeamos el tabulador para que inserte por defecto 4 espacios
        	if(value.getCode() == KeyCode.TAB) {
        		codeArea.undo();
        		codeArea.replaceSelection("    ");
        	}
        	// Mapeamos la tecla ENTER para la autotabulación
        	if(value.getCode() == KeyCode.ENTER) {
        		Position pos = codeArea.offsetToPosition(codeArea.getCaretPosition(), null);
        		char prevLine[] = codeArea.getText().split("\n")[pos.getMajor()-1].toCharArray();
        		String initialBlanks = "";
        		for(int i=0; i<prevLine.length; i++) {
        			if(prevLine[i] == ' ') initialBlanks+=" ";
        			else i=prevLine.length;
        		}
        		codeArea.insertText(codeArea.getCaretPosition(), initialBlanks);
        	}
        });
        
        // Sustituimos todas las tabulaciones por conjuntos de 4 espacios
        content = content.replaceAll("\t", "    ");
        codeArea.insertText(0, content);

        StackPane sp = new StackPane(new VirtualizedScrollPane<>(codeArea));
        
        if(path.endsWith(Main.WPL_EXT)) {
        	sp.getStylesheets().add(getClass().getResource("wpl-keywords.css").toExternalForm());
        }
              
        setContent(sp);
        
        // Asi eliminamos cuando el fichero estaba vacio antes de escribirlo
        // de este modo no podemos hacer un CTRL-Z y quedarnos con el documento en blanco.
        codeArea.getUndoManager().forgetHistory(); 
        isSaved = true;	// Para que por defecto cuando abrimos un archivo aparezca como guardado
	}
	
	public CodeTab(String title, String content, RichTextCode parent) {
		Label l = new Label("CTRL+N -> New Project\nCTRL+O -> Open Project\nCTRL+SHIFT+R -> Reload");
		l.setId("infoEditorLbl");
    	l.setStyle("-fx-text-fill: #9e9e9e; -fx-font-size: 16px;");
    	setStyle("");
		setClosable(false);
		setText(title);
		BorderPane bp = new BorderPane();
		bp.setCenter(l);
		setContent(bp);
	}
	
	public void setUnsaved() {
		isSaved = false;
		if(!getText().startsWith("*")) {
			setText("*"+getText());
		}
	}
	
	public void saveTab() {
		IOManager writer = new IOManager();
		String ttw = codeArea.getText();
		try {
			writer.open(path, false, false);
			writer.putString(ttw);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		isSaved = true;
		if(getText().startsWith("*")) {
			setText(getText().substring(1, getText().length()));
		}
	}
	
	public boolean saved() {
		return isSaved;
	}
	
	private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("ATTRIBUTE") != null ? "attribute" :
                    matcher.group("VALUE") != null ? "value" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("NUMBER") != null ? "number" :
                    matcher.group("COMMENT") != null ? "comment" :
                    matcher.group("COLOR") != null ? "color" :
                    matcher.group("VAR") != null ? "var" : 
                    matcher.group("REST") != null ? "wrongText" :
                    null;	assert styleClass!=null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
	
	public boolean selected() {	
		return this.isSelected();
	}
	
	public String getPath() {
		return path;
	}
	
	public void selectAll() {
		codeArea.selectAll();
	}
	
	public void copy() {
		codeArea.copy();
	}
	
	public void paste() {
		codeArea.paste();
	}
	
	public void cut() {
		codeArea.cut();
	}
	
	public void undo() {
		if(codeArea.isUndoAvailable()) {
			codeArea.undo();
		}
	}
	
	public void redo() {
		if(codeArea.isRedoAvailable()) {
			codeArea.redo();
		}
	}
	
	public void replaceAll(String val, String nval) {
		codeArea.replaceText(codeArea.getText().replaceAll(val, nval));
	}
	
	public void findNext(String val, int index) {
		String[] spitedtxt = codeArea.getText().split(val);
		int pos = 0;
		for(int i=0; i<index; i++) {
			pos += spitedtxt[i].length()+val.length();
		}
		codeArea.selectRange(pos, pos+val.length());
	}
	
}
