package application;

import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.control.TabPane;

public class RichTextCode {

	//private CodeArea codeArea;
	private TabPane tabPane;
	private ArrayList<CodeTab> tabs;
	
	public RichTextCode() {
		tabs = new ArrayList<>();
        tabPane = new TabPane();
        CodeTab ct = new CodeTab("Information", "", this);
        ct.setStyle("background-color:#ff0000");
        tabPane.getTabs().add(ct);
	}
	
    /** Retorna el componente editor de codigo */
    public TabPane getCodeEditor() {       
        return tabPane;
    }
     
    /** Agrega un nuevo tab, si no esta abierto ya */
    public void addTab(String path) {
    	boolean alreadyActive = false;
    	// Comprobamos si ya esta activo el tab
    	for(int i=0; i<tabs.size(); i++) {
    		if(tabs.get(i).getPath().compareTo(path) == 0) {
    			alreadyActive = true;
    		}
    	}
    	// Si no esta abierto, lo abrimos
    	if(!alreadyActive) {
	    	String[] pathElems = path.split(Main.SEPARATOR);
	    	if(pathElems != null) {
	    		String title = pathElems[pathElems.length-1]; 
	        	String content = getFileContent(path);
	        	CodeTab tab = new CodeTab(title, path, content, this);
	        	tabs.add(tab);
	            tabPane.getTabs().add(tab);
	            tab.saveTab();
	    	}	
    	}
    	// Activamos el tab que hemos abierto, o que tenemos abierto
    	selectTab(path);
    }
    
    /** Comprueba que todos los archivos han sido guardados */
    public boolean isEverythingSaved() {
    	for(int i=0; i<tabs.size(); i++) {
    		if(!tabs.get(i).saved()) {
    			return false;
    		}
    	}
    	return true;
    }
    
    /** Retorna los tabs desplegados */
    public ArrayList<CodeTab> getCodeTabs() {
    	return tabs;
    }
    
    /** Retorna el contenedor de tabs */
    public TabPane getTabs(){
    	return tabPane;
    }
    
    /** Guarda el fichero del tab activo */
    public void saveCurrentTab() {
    	for(CodeTab t : tabs) {
    		if(t.isSelected()) {
    			t.saveTab();
    		}
    	}	
    }
    
    /** Guarda el contenido de los ficheros de todas los tabs abiertos */
    public void saveAllTabs() {
    	for(CodeTab t : tabs) {
    		t.saveTab();
    	}
    }
    
    /** Obtiene la ruta del fichero asociado al tab activo */
    public String getSelectedTabPath() {
    	CodeTab ctab = getSelectedTab();
    	if(ctab != null) {
    		return ctab.getPath();
    	}
    	return null;
    }
    
    /** Obtiene la ruta del fichero asociado al tab activo */
    public CodeTab getSelectedTab() {
    	for(int i=0; i<tabs.size(); i++) {
    		if(tabs.get(i).isSelected()) {
    			return tabs.get(i);
    		}
    	}
    	return null;
    }
    
    /** Permite seleccionar una tab */
    public void selectTab(String path) {
    	for(int i=0; i<tabs.size(); i++) {
    		if(tabs.get(i).getPath().compareTo(path) == 0) {
    			tabPane.getSelectionModel().select(tabs.get(i));
    		}
    	}
    }
    
    /** Cierra todas las tabs */
    public void closeAllTabs() {
    	tabs.clear();
    	tabPane.getTabs().clear();
    }
    
    /** Obtiene el contenido de un fichero dada la ruta */
    private String getFileContent(String path) {
		String cont = "";
		IOManager reader = new IOManager();
		try {
			reader.open(path, true, false);
			String aux = "";
			while((aux = reader.getLine()) != null) {
				cont += aux + "\n";
			}
			
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return cont;
	}
    
    /** CTRL-C */
    public void copy() {
    	CodeTab ctab = getSelectedTab();
    	if(ctab != null) {
    		ctab.copy();
    	}
    }
    
    /** CTRL-V */
    public void paste() {
    	CodeTab ctab = getSelectedTab();
    	if(ctab != null) {
    		ctab.paste();
    	}
    }
    
    /** CTRL-X */
    public void cut() {
    	CodeTab ctab = getSelectedTab();
    	if(ctab != null) {
    		ctab.cut();
    	}
    }
    
    /** CTRL-Z */
    public void undo() {
    	CodeTab ctab = getSelectedTab();
    	if(ctab != null) {
    		ctab.undo();
    	}
    }
    
    /** CTRL-SHIFT-Z */
    public void redo() {
    	CodeTab ctab = getSelectedTab();
    	if(ctab != null) {
    		ctab.redo();
    	}
    }
    
    /** Reemplaza una cadena por otra */
    public void replaceAll(String val, String nval) {
    	CodeTab ctab = getSelectedTab();
    	if(ctab != null) {
    		ctab.replaceAll(val, nval);
    	}
    }
    
    /** Busca una cadena en el fichero */
    public void find(String val, int index) {
    	CodeTab ctab = getSelectedTab();
    	if(ctab != null) {
    		ctab.findNext(val, index);
    	}
    }
    
    /** Selecciona todo el texto del archivo activo */
    public void selectAll() {
    	CodeTab ctab = getSelectedTab();
    	if(ctab != null) {
    		ctab.selectAll();
    	}
    }
}
