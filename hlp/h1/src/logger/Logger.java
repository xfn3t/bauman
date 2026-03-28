package logger;

public class Logger {

	private static Logger INSTANCE;

	private Logger() {
	}

	public static synchronized Logger getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new Logger();
		}
		return INSTANCE;
	}

	public void info(String msg) {
		System.out.println("INFO: " + msg);
	}

	public void warning(String msg) {
		System.out.println("WARNING: " + msg);
	}

	public void error(String msg) {
		System.out.println("ERROR: " + msg);
	}
}
