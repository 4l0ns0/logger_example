package com.example.app.logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CustomLogger {
	
	public static enum LogType {MESSAGE, WARNING, ERROR}
	
	/**
	 * Metodo para inicializar los parametros de forma global.
	 * 
	 * @param logFilePath ruta en donde se guardara el log. Obligatorio si logToDatabase = true.
	 * @param settings configuraciones de conexion a base de datos. Obligatorio si logToFile = true.
	 * @param logMessage true si se quiere capturar mensajes. Caso contrario false.
	 * @param logWarning true si se quiere capturar alertas. Caso contrario false.
	 * @param logError true si se quiere capturar errores. Caso contrario false.
	 * @param logToFile true si se quiere guardar a un archivo de texto los mensajes. Caso contrario false.
	 * @param logToConsole true si se quiere mostrar en consola los mensajes. Caso contrario false.
	 * @param logToDatabase true si se quiere guardar en una base de datos los mensajes. Caso contrario false.
	 */
	public static void init(String logFilePath, Settings settings,
			boolean logMessage, boolean logWarning, boolean logError, 
			boolean logToFile, boolean logToConsole, boolean logToDatabase) throws Exception {
		
		if (logToFile && (logFilePath == null || logFilePath.trim().isEmpty()))
			throw new Exception("You need to set the loggin file path.");
		
		if (logToDatabase && settings == null)
			throw new Exception("Configuration object (settings) cannot be null.");
		
		if (!logToFile && !logToConsole && !logToDatabase)
            throw new Exception("At least one loggin target is required.");
		
		if (!logMessage && !logWarning && !logError)
            throw new Exception("Message or Warning or Error must be specified.");
		
		CustomLogger instance = CustomLogger.getInstance();
		
		instance.logToFile = logToFile;
		instance.logToConsole = logToConsole;
		instance.logToDatabase = logToDatabase;
		
		instance.logMessage = logMessage;
		instance.logWarning = logWarning;
		instance.logError = logError;
		
		instance.logger = Logger.getLogger("MyLog");
		instance.logger.setUseParentHandlers(false);
		
		// Obtenido de: 'https://www.logicbig.com/tutorials/core-java-tutorial/logging/customizing-default-format.html'
		SimpleFormatter sf = new SimpleFormatter() {
			private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

			@Override
			public synchronized String format(LogRecord lr) {
				return String.format(format, new Date(lr.getMillis()), lr.getLevel().getLocalizedName(),
						lr.getMessage());
			}
		};
		
		configFileHandler(logFilePath, sf);
		configConsoleHandler(sf);
		cofigDbConnection(settings);
		
		instance.initialized = true;
	}
	
	/**
	 * Configura o quita el manejador del log.
	 * 
	 * @param logFilePath ruta del archivo
	 * @param sf formato
	 * @throws Exception
	 */
	private static void configFileHandler(String logFilePath, SimpleFormatter sf) throws Exception {
		
		if (instance.logToFile) {
			
			if (instance.fh != null) // si ya existe, no se añade uno nuevo
				return;
			
			/*
			 * Se configura el logger para que tenga una rotacion de 5 archivos, cada 
			 * uno de hasta 5mb.
			 */
			instance.fh = new FileHandler(logFilePath + "/logFile.log", 5242880, 5, true);
			instance.fh.setFormatter(sf);
			
			instance.logger.addHandler(instance.fh);
		} else {
			if (instance.fh != null) { // si fue seteado anteriormente, quitamos el manejador.
				instance.logger.removeHandler(instance.fh);
				instance.fh = null;
			}
		}
	}
	
	/**
	 * Configura o quita el manejador del log.
	 * 
	 * @param sf formato
	 * @throws Exception
	 */
	private static void configConsoleHandler(SimpleFormatter sf) throws Exception {
		
		if (instance.logToConsole) {
			
			if (instance.ch != null) // si ya existe, no se añade uno nuevo
				return;
			
			instance.ch = new ConsoleHandler();
			instance.ch.setFormatter(sf);
			
			instance.logger.addHandler(instance.ch);
		} else {
			if (instance.ch != null) { // si fue seteado anteriormente, quitamos el manejador.
				instance.logger.removeHandler(instance.ch);
				instance.ch = null;
			}
		}
	}
	
	/**
	 * Configura o cierra la conexiona  base de datos.
	 * 
	 * @param settings archivos de configuracion.
	 * @throws Exception
	 */
	private static void cofigDbConnection(Settings settings) throws Exception {
		
		if (instance.logToDatabase) {
			
			boolean config = false;
			
			if (instance.cn == null) {
				config = true;
			} else {
				if (!instance.settings.equals(settings))
					config = true;
			}
			
			/*
			 * Si no hay una conexion activa o los parametros de configuracion
			 * son distintos, genera una conexion a la bd. 
			 */
			if (config) {
				String otherSettings = "serverTimezone=America/Lima&useSSL=false";
				
				instance.cn = DriverManager.getConnection("jdbc:" + settings.getDbms() + "://" + settings.getHost()
					+ ":" + settings.getPort() + "/" + settings.getScheme() + "?" + otherSettings, 
				settings.getUser(), settings.getPassword());
				
				instance.settings = settings;
			}
		} else {
			// si fue seteado anteriormente, cerramos la conexion
			if (instance.cn != null) {
				try {
					instance.cn.close();
					instance.cn = null;
					instance.settings = null;
				} catch (Exception e) {
					System.out.println("Close connection error: " + e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Metodo para el loggin de los mensajes.
	 * 
	 * @param messageText mesaje a ser logeado
	 * @param messageType tipo de mensaje
	 * @throws Exception
	 */
	public static void logMessage(String messageText, LogType messageType) throws Exception {
		
		/*
		 * En vez de añadir un parametro por cada uno de los tipos de mensaje, se
		 * opta por un enum que detalle cada uno de los tipos. Esta es una mejor
		 * aproximacion, ya que en caso se añadan mas tipos, no sería necesario añadir
		 * más parametros y por ende, las clases en donde se utiliza este metodo no 
		 * se tendrian que actualizar.
		 */
		
		CustomLogger instance = CustomLogger.getInstance();
		
		if (!instance.initialized)
			throw new Exception("Parameters have not been initialized.");
		
		if (messageText == null || messageText.trim().isEmpty())
			throw new Exception("Message cannot be null or empty.");
		
		instance.log(messageText.trim(), messageType);
	}
	
	
	private static CustomLogger instance;
	
	private boolean logMessage;
    private boolean logWarning;
    private boolean logError;
    
	private boolean logToFile;
    private boolean logToConsole;
    private boolean logToDatabase;
    
    private Logger logger;
    private boolean initialized;
    
    private FileHandler fh;
    private ConsoleHandler ch;
    
    private Settings settings;
    private Connection cn;
    
    
    /**
     * Constructor privado
     */
	private CustomLogger() { }
	
	/**
	 * Metodo para obtener la instancia de clase.
	 * 
	 * @return instancia de clase.
	 */
	private static synchronized CustomLogger getInstance() {
		
		/*
		 * Se utiliza synchronized para garantizar que se respete el patron 
		 * singleton incluso si la obtencion de la instancia se realiza dentro
		 * de metodo que se ejecuta en un hilo independiente. 
		 */
		if (instance == null)
			instance = new CustomLogger();
		
		return instance;
	}
	
	/**
	 * Metodo encargado de registrar los mensaje.
	 * 
	 * @param message mensaje a registrar
	 * @param messageType tipo de mensaje
	 * @throws Exception
	 */
	public void log(String message, LogType messageType) throws Exception {
		
		String msg = instance.getLevelMessage(message, messageType);
		
		if (msg == null) // no se permite el nivel de mensaje indicado
			return;
		
		// Menejamos los nivel del log de forma estandar.
		switch (messageType) {
			case MESSAGE:
				instance.logger.log(Level.INFO, message);
				break;
			case WARNING:
				instance.logger.log(Level.WARNING, message);
				break;
			case ERROR:
				instance.logger.log(Level.SEVERE, message);
				break;
		}
		
		instance.logToDB(msg, messageType);
	}
	
	/**
	 * Registra el mensaje en base de datos.
	 * 
	 * @param message mensaje
	 * @param messageType tipo de mensaje
	 * @throws Exception
	 */
	private void logToDB(String message, LogType messageType) throws Exception {
		
		if (!logToDatabase)
			return;
		
		int t = 0;
        
		if (messageType == LogType.MESSAGE && logMessage)
            t = 1;
		
        if (messageType == LogType.ERROR && logError)
            t = 2;

        if (messageType == LogType.WARNING && logWarning)
            t = 3;
        
        PreparedStatement ps = null;
        
        try {
        	ps = cn.prepareStatement("insert into log (message, level) values(?, ?)");
            ps.setString(1, message);
            ps.setInt(2, t);
            ps.execute();
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e2) {
				System.out.println("Close statement error: " + e2.getMessage());
			}
		}
	}
	
	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Devuelve el mensaje formateado. Null si el nivel del mensaje no esta permitido.
	 * 
	 * @param message mensaje
	 * @param messageType tipo de mensaje
	 * @return mesaje formateado.
	 */
	private String getLevelMessage (String message, LogType messageType) {
		
		String dateTime = simpleDateFormat.format(new Date());
		
		if (messageType == LogType.MESSAGE && logMessage)
			return "[message] " + dateTime + ": " + message;
		
        if (messageType == LogType.WARNING && logWarning)
        	return "[warning] " + dateTime + ": " + message;
        
        if (messageType == LogType.ERROR && logError)
        	return "[ error ] " + dateTime + ": " + message;
        
        return null;
	}
}
