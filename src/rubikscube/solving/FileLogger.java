package rubikscube.solving;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class FileLogger {

    private final Logger fileLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final Formatter defaultFormatter = new Formatter() {
        @Override
        public String format(LogRecord logRecord) {
            return "[" + logRecord.getLevel() + "] " + new SimpleDateFormat("dd.MM.yyyy@HH:mm:ss").format(new Date(logRecord.getMillis())) + ": " + this.formatMessage(logRecord) + "\n";
        }
    };
    private final Formatter netFormatter = new Formatter() {
        @Override
        public String format(LogRecord logRecord) {
            return this.formatMessage(logRecord) + "\n";
        }
    };
    private StringBuilder buffer = new StringBuilder(30);
    private int bufferMoveCount = 0;
    public FileLogger(String name) {
        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler("logs/" + name + " " + new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date(System.currentTimeMillis())) + ".txt");
            fileHandler.setFormatter(defaultFormatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        fileLogger.setUseParentHandlers(false);
        fileLogger.setLevel(Level.FINE);
        fileLogger.addHandler(fileHandler);
    }

    public void logBuffer(String msg) {
        buffer.append(msg + " ");
        bufferMoveCount++;
    }

    public void forceLog(String[] net) {
        logNet(Level.FINE, buffer.toString(), net);
        buffer.delete(0, 30);
    }

    public void logNet(Level level, String moveNotation, String[] net) {
        fileLogger.log(level, moveNotation);
        fileLogger.getHandlers()[0].setFormatter(netFormatter);
        for (int i = 0; i < 9; i++) {
            if (i != 3 && i != 4 && i != 5) {
                fileLogger.log(level, "   " + net[i]);
            } else {
                fileLogger.log(level, net[i]);
            }
        }
        fileLogger.getHandlers()[0].setFormatter(defaultFormatter);
    }

    public void info(String msg) {
        fileLogger.info(msg);
    }

    public void config(String msg) {
        fileLogger.config(msg);
    }

    public void warning(String msg) {
        fileLogger.warning(msg);
        System.out.println("[WARNING] " + msg);
    }

    public void fine(String msg) {
        fileLogger.fine(msg);
    }

    public void finer(String msg) {
        fileLogger.finer(msg);
    }

    public void finest(String msg) {
        fileLogger.finest(msg);
    }

    public void severe(String msg) {
        fileLogger.severe(msg);
        System.out.println("[SEVERE] " + msg);
    }
}