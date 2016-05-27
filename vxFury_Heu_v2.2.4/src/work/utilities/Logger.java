package work.utilities;

import java.io.File;

import eon.general.FileOutput;

/**
 * @author vxFury
 *
 */
public class Logger {
	private File file;
	private Level level = Level.ALL;
	private FileOutput logger = new FileOutput();
	
	private static int outMask = Level.FATAL.getMask();
	
	public Logger(String loggerFile,Level level) {
		try {
			file = new File(loggerFile);
			this.level = level;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Logger(String loggerFile) {
		try {
			file = new File(loggerFile);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String getName() {
		return file.getName();
	}
	
	public File getFile() {
		return file;
	}
	
	public static void logln(Logger logger,String format,Object...args) {
		String msg = String.format(format, args);
		if(logger != null) {
			if((logger.getLevel().getMask() & outMask) != 0) {
				logger.writeLn(msg);
			}
		} else {
			System.out.println(msg);
		}
	}
	
	public static void log(Logger logger,String format,Object...args) {
		String msg = String.format(format, args);
		if(logger != null) {
			if((logger.getLevel().getMask() & outMask) != 0) {
				logger.write(msg);
			}
		} else {
			System.out.print(msg);
		}
	}
	
	public static void logln(String msg,Logger logger) {
		if(logger != null) {
			if((logger.getLevel().getMask() & outMask) != 0) {
				logger.writeLn(msg);
			}
		} else {
			System.out.println(msg);
		}
	}
	
	public static void log(String msg,Logger logger) {
		if(logger != null) {
			if((logger.getLevel().getMask() & outMask) != 0) {
				logger.write(msg);
			}
		} else {
			System.out.print(msg);
		}
	}

	public synchronized void write(String msg) {
		this.logger.write(file.getAbsolutePath(), msg);
	}

	public synchronized void writeLn(String msg) {
		this.logger.writeLn(file.getAbsolutePath(), msg);
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}
	
	public void addFilter(Level level) {
		outMask |= level.getMask();
	}
	
	public void removeFilter(Level level) {
		outMask &= ~(level.getMask());
	}
	
	public boolean isEnabled(Level level) {
		return (outMask & level.getMask()) != 0;
	}

	public enum Level {
		NONE("NONE", 0), FATAL("FATAL", 0x0001 << 0), ERROR("ERROR", 0x0001 << 1), WARNING("WARNING", 0x0001 << 2), INFO("INFO", 0x0001 << 3), DEBUG("DEBUG", 0x0001 << 4), ALL("ALL", 0xFFFF);
		private String Name;
		private int Mask;

		private Level(String Name, int Mask) {
			this.Name = Name;
			this.Mask = Mask;
		}

		public String getName() {
			return Name;
		}

		public int getMask() {
			return Mask;
		}
	}
}
