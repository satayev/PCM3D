package pcm.gui;


import java.io.IOException;
import java.io.InputStream;

/**
 * Runs the jar for pcm.gui.Main.java
 * 
 * Can be scrapped if use another way later
 */
public class JarRunnable {

  public static void main(String[] args) throws IOException {
	  Process proc;
	  if (System.getProperty("sun.arch.data.model").equals("64"))
		  proc = Runtime.getRuntime().exec("java -Xms256m -Xmx512m -Djava.library.path=src/data/dlls64 -jar pcm.gui.main.jar");
	  else 
		  proc = Runtime.getRuntime().exec("java -Xms256m -Xmx512m -Djava.library.path=src/data/dlls -jar pcm.gui.main.jar");
	  
  }

}