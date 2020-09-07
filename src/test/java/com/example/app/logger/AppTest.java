package com.example.app.logger;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	private String logFilePath = System.getProperty("java.io.tmpdir");
	private Settings settings = new Settings("root", "sa", "mysql", "logger_server", "3306", "db_logger");
	
	/**
	 * Muestra los mensaje en los tres niveles
	 */
	public void testShowAllThreeLevels() {
		
		try {
			CustomLogger.init(null, null, true, true, true, false, true, false);
			CustomLogger.logMessage("Mensaje nivel: Message", CustomLogger.LogType.MESSAGE);
			CustomLogger.logMessage("Mensaje nivel: Alerta", CustomLogger.LogType.WARNING);
			CustomLogger.logMessage("Mensaje nivel: Error", CustomLogger.LogType.ERROR);		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * No debe mostrar ningun mensaje
	 */
	public void testNotAllowedLevel() {
		
		try {
			CustomLogger.init(null, null, false, true, true, false, true, false);
			CustomLogger.logMessage("No se debe mostrar el mensaje", CustomLogger.LogType.MESSAGE);
			
			CustomLogger.init(null, null, true, false, true, false, true, false);
			CustomLogger.logMessage("No se debe mostrar la alerta", CustomLogger.LogType.WARNING);
			
			CustomLogger.init(null, null, true, true, false, false, true, false);
			CustomLogger.logMessage("No se debe mostrar el error", CustomLogger.LogType.ERROR);		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Muestra el mensaje en cada uno de los destinos que corresponde
	 */
	public void testPutMessageOnTarget() {
		
		try {
			CustomLogger.init(logFilePath, settings, true, true, true, true, true, true);
			CustomLogger.logMessage("1.- Mensaje: archivo, consola, BD", CustomLogger.LogType.MESSAGE);
			
			CustomLogger.init(logFilePath, settings, true, true, true, true, false, false);
			CustomLogger.logMessage("2.- Mensaje: solo archivo", CustomLogger.LogType.MESSAGE);
			
			CustomLogger.init(logFilePath, settings, true, true, true, false, true, false);
			CustomLogger.logMessage("3.- Mensaje: solo consola", CustomLogger.LogType.MESSAGE);
			
			CustomLogger.init(logFilePath, settings, true, true, true, false, false, true);
			CustomLogger.logMessage("4.- Mensaje: solo BD", CustomLogger.LogType.MESSAGE);
			
			CustomLogger.init(logFilePath, settings, true, true, true, true, true, true);
			CustomLogger.logMessage("5.- Mensaje: archivo, consola, BD", CustomLogger.LogType.MESSAGE);		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
