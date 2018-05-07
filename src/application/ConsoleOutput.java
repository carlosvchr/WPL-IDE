package application;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

public class ConsoleOutput {

	String logPath;
	CodeArea ca;
	
	private final String ERROR_PATTERN = "Error[^\n]*";
	private final String TEXT_PATTERN = "TEXT";
    private final String SUCCESS_PATTERN = "\\b((0"+"|"+"([1-9][0-9]*))(.[0-9]*[1-9][0-9]*)?)((px)|(%))?\\b";

    private final Pattern CONSOLE_PATTERN = Pattern.compile(
    		"(?<ERROR>" + ERROR_PATTERN + ")"
    		+ "|(?<TEXT>" + TEXT_PATTERN + ")"
            + "|(?<SUCCESS>" + SUCCESS_PATTERN + ")"
    );
	
	public ConsoleOutput(String log) {
		this.logPath = log;
		ca = new CodeArea();
		ca.setEditable(false);
		ca.setId("consoleEditor");
		ca.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		ca.richChanges()
	        .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
	        .subscribe(change -> {
	            ca.setStyleSpans(0, computeHighlighting(ca.getText()));
	        });
	}
	
	public void printErrors(String s) {
		ca.replaceText(0, ca.getText().length(), s);
	}
	
	public CodeArea getConsole() {
		return ca;
	}
	
	private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = CONSOLE_PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("ERROR") != null ? "error" :
                    matcher.group("TEXT") != null ? "text" :
                    matcher.group("SUCCESS") != null ? "success" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
	
}
