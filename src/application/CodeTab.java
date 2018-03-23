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

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;

public class CodeTab extends Tab {

	private String path;
	private CodeArea codeArea;
	private boolean isSaved;
	
	private static final String[] KEYWORDS = new String[] {
            "box", "hbox", "vbox", "tablebox", "sidebox", "dropdownbox", "tabbedbox", "modalbox",
            "accordionbox", "item", "radiogroup", "slidehow", "radiobutton", "button", "image", "video",
            "audio", "textfield", "checkbox", "label", "progressbar", "import", "define", "include"
    };
    
    private static final String[] ATTRIBUTES = new String[] {
            "title", "description", "lang", "charset", "redirect", "author", "keywords", "pageicon",
            "type", "name", "content", "alt", "poster", "src", "autoplay", "controls", "loop", "muted",
            "preload", "onclick", "onchange", "alignment", "type", "placeholder", "header", "text-align",
            "text-decoration", "text-color", "text", "font-size", "font-family", "animation", "background-color",
            "border-color", "border-radius", "border-size", "border", "class", "effect", "elevation", "height",
            "id", "link", "margin", "padding", "slots", "tooltip", "width", "closable", "delay", "slide-controls",
            "indicators", "caption-position", "caption"
    };
    
    private static final String[] VALUES = new String[] {
            "true", "false", "red", "pink", "purple", "deep-purple", "indigo", "blue-gray", "blue", "light-blue",
            "cyan", "aqua", "teal", "green", "light-green", "lime", "sand", "khaki", "yellow", "amber", "orange",
            "deep-orange", "brown", "light-gray", "gray", "dark-gray", "pale-red", "pale-yellow", "pale-green", 
            "pale-blue", "TimesNewRoman", "Georgia", "AndaleMono", "ArialBlack", "Arial", "Impact", "TrebuchetMS",
            "Verdana", "ComicSansMS", "CourierNew", "wide", "bold", "italic", "shadowed", "underlined", "strikethrough",
            "top-right", "top-left", "bottom-right", "bottom-left", "center", "top", "bottom", "right", "left",
            "opacity-max", "opacity-min", "opacity", "grayscale-max", "grayscale-min", "grayscale", "sepia-max",
            "sepia-min", "sepia", "zoom", "fading", "spin", "move-up", "move-down", "move-right", "move-left",
            "cs-8859", "cs-ansi", "cs-ascii", "cs-utf-8", "color", "font", "tag", "attr", "dots", "numbers", "miniatures",
            "angulars-bottom", "angulars", "arrows-bottom", "arrows", "none"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String ATTR_PATTERN = "\\b(" + String.join("|", ATTRIBUTES) + ")\\b";
    private static final String VALUE_PATTERN = "\\b(" + String.join("|", VALUES) + ")\\b";
    private static final String SEMICOLON_PATTERN = "\\:"+"|"+"\\,";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "#[^\n]*";
    private static final String NUMBER_PATTERN = "\\b((0"+"|"+"([1-9][0-9]*))(.[0-9]*[1-9][0-9]*)?)((px)|%)?\\b";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<ATTRIBUTE>" + ATTR_PATTERN + ")"
            + "|(?<VALUE>" + VALUE_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
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

        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                    codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));
                    codeArea.getUndoManager().mark();
                    setUnsaved();
                });
        
        // Mapeamos el tabulador para que inserte por defecto 4 espacios
        codeArea.setOnKeyPressed(value->{
        	if(value.getCode() == KeyCode.TAB) {
        		codeArea.undo();
        		codeArea.replaceSelection("    ");
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
	
	private static StyleSpans<Collection<String>> computeHighlighting(String text) {
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
                    null; /* never happens */ assert styleClass != null;
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
