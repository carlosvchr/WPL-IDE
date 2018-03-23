package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;


public class IOManager {

	private File file = null;
	private FileReader fr = null;
	private BufferedReader br = null;
	private FileWriter fw = null;
	private PrintWriter pw = null;
	private long lineNumber = 0;
	
	/** Abre un fichero en modo de lectura o escritura
	 * Si el fichero se abre en escritura y éste existe, entonces
	 * se sobreescribe. */
	public void open(String path, boolean read, boolean internalFile) throws IOException {
		lineNumber = 0;
		if(internalFile) {
			file = new File(getClass().getResource(path).getFile());
		}else {
			file = new File(path);
		}
		if(file != null) {
			if(read) {
				fr = new FileReader(file);
				br = new BufferedReader(fr);
			}else {
				fw = new FileWriter(file);
				pw = new PrintWriter(fw);
			}
		}
	}
	
	/** Lee una línea de un fichero abierto para lectura */
	public String getLine() throws IOException {
		if(br != null) {
			lineNumber++;
			return br.readLine();
		}else {
			return null;
		}
	}
	
	/** Escribe una línea en un fichero abierto para escritura */
	public void putLine(String val) {
		if(pw != null) {
			lineNumber++;
			pw.println(val);
		}
	}
	
	/** Escribe una cadena de texto en un fichero abierto para escritura */
	public void putString(String val) {
		if(pw != null) {
			lineNumber++;
			pw.print(val);
		}
	}
	
	/** Devuleve el número de línea del fichero que se está leyendo o escribiendo */
	public long getLineNumber() {
		return lineNumber;
	}
	
	/** Cierra los ficheros que se hayan abierto */
	public void close() throws IOException {
		if(fr != null) {
			fr.close();
		}
		if(fw != null) {
			fw.close();
		}
		if(pw != null) {
			pw.close();
		}
	}
	
	
	/** Elimina un fichero */
	public void deleteFile(String path) {
		File fichero = new File(path);
		fichero.delete();
	}
	
	/** Comprueba si un fichero existe */
	public boolean exists(boolean internal, String path) {
		File f;
		if(internal) {
			URL url = Main.class.getClassLoader().getResource(path);
			if(url != null) {
				f = new File(url.getFile());
				return f.exists();
			}else {
				System.out.println("Imposible cargar ruta");
				return false;
			}
		}else {
			f = new File(path);
			return f.exists();
		}
	}
	
}
