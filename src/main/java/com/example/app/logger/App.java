package com.example.app.logger;

public class App {
	
	public static void main(String[] args) {
		
		String logFilePath = System.getProperty("java.io.tmpdir");
		Settings settings = new Settings("root", "sa", "mysql", "logger_server", "3306", "db_logger");
		
		try {
			CustomLogger.init(null, null, true, true, true, false, true, false);
			CustomLogger.logMessage("Mensaje nivel: Message", CustomLogger.LogType.MESSAGE);
			CustomLogger.logMessage("Mensaje nivel: Alerta", CustomLogger.LogType.WARNING);
			CustomLogger.logMessage("Mensaje nivel: Error", CustomLogger.LogType.ERROR);
			
			CustomLogger.init(null, null, false, true, true, false, true, false);
			CustomLogger.logMessage("No se muestra el mensaje", CustomLogger.LogType.MESSAGE);
			
			CustomLogger.init(null, null, true, false, true, false, true, false);
			CustomLogger.logMessage("No se muestra la alerta", CustomLogger.LogType.WARNING);
			
			CustomLogger.init(null, null, true, true, false, false, true, false);
			CustomLogger.logMessage("No se muestra el error", CustomLogger.LogType.ERROR);
			
			
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
