package LLD.CS.loggingFramework;

// funtional:
// type: Debug, Info, Error, warning
// timestamp, logLevel, message
// console, file, db

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

enum LogLevel {
    DEBUG, INFO, ERROR, WARNING
}

enum LogDestination{
    CONSOLE, FILE, DB
}

@Getter
class LogEntry{
    private LocalDateTime timestamp;
    private LogLevel level;
    private String message;

    public LogEntry(LocalDateTime timestamp, LogLevel level, String message) {
        this.timestamp =timestamp;
        this.level = level;
        this.message = message;
    }

    public String toString(){
        return String.format("%s %s %s", timestamp, level, message);
    }

    public LogEntry parse(String log){
        String[] parts = log.split(" ");
        LocalDateTime timestamp = LocalDateTime.parse(parts[0]);
        LogLevel level = LogLevel.valueOf(parts[1]);
        String content = String.join(" ", parts[2]);
        return new LogEntry(timestamp, level, content);
    }
}

interface Appender {
    void append(String content, LogLevel level);
}

class ConsoleAppender implements Appender {
    ConsoleAppender(){
        System.out.println("ConsoleAppender created");
    }


    @Override
    public void append(String message, LogLevel level) {
        System.out.println(message);
    }
}

class FileAppender implements Appender {
    private String filename;

    public FileAppender(String filename) {
        this.filename = filename;
    }

    @Override
    public void append(String message, LogLevel level) {
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//class DBAppender implements Appender {
//}

class LoggerFactory {
    public static LoggerImpl getConsoleLogger() {
        LoggerImpl logger = LoggerImpl.getInstance();
        logger.addAppender(new ConsoleAppender());
        return logger;
    }

    public static LoggerImpl getFileLogger(String filename) {
        LoggerImpl logger = LoggerImpl.getInstance();
        logger.addAppender(new FileAppender(filename));
        return logger;
    }

    public static LoggerImpl getCombinedLogger(String filename) {
        LoggerImpl logger = LoggerImpl.getInstance();
        logger.addAppender(new ConsoleAppender());
        logger.addAppender(new FileAppender(filename));
        return logger;
    }
}

// ----- Log Repository (for in-memory storage + search) -----
class LogRepository {
    private List<LogEntry> logs = Collections.synchronizedList(new ArrayList<>());

    public void addLog(LogEntry entry) {
        logs.add(entry);
    }

    // Search by level
    public List<LogEntry> searchByLevel(LogLevel level) {
        List<LogEntry> result = new ArrayList<>();
        for (LogEntry entry : logs) {
            if (entry.getLevel() == level) result.add(entry);
        }
        return result;
    }

    // Search by keyword
    public List<LogEntry> searchByKeyword(String keyword) {
        List<LogEntry> result = new ArrayList<>();
        for (LogEntry entry : logs) {
            if (entry.getMessage().contains(keyword)) result.add(entry);
        }
        return result;
    }

    // Search by time range
    public List<LogEntry> searchByTimeRange(LocalDateTime from, LocalDateTime to) {
        List<LogEntry> result = new ArrayList<>();
        for (LogEntry entry : logs) {
            if (!entry.getTimestamp().isBefore(from) && !entry.getTimestamp().isAfter(to)) {
                result.add(entry);
            }
        }
        return result;
    }


    public List<LogEntry> search(LogSearchCriteria criteria) {
        List<LogEntry> result = new ArrayList<>();
        for (LogEntry entry : logs) {
            if (criteria.getLevel() != null && entry.getLevel() != criteria.getLevel()) continue;
            if (criteria.getKeyword() != null && !entry.getMessage().contains(criteria.getKeyword())) continue;
            if (criteria.getFrom() != null && entry.getTimestamp().isBefore(criteria.getFrom())) continue;
            if (criteria.getTo() != null && entry.getTimestamp().isAfter(criteria.getTo())) continue;
            result.add(entry);
        }
        return result;
    }
}



interface Logger {
    void addAppender(Appender appender);
    void removeAppender(Appender appender);
    void info(String content);
    void error(String content);
    void debug(String content);
    void warning(String content);
}

@Slf4j
class LoggerImpl implements Logger{
    private LogLevel minLogLevel;
    private final List<Appender> appenders;
    private final ExecutorService executorService;
    private LogRepository repository;

    // singleton pattern
    private static LoggerImpl instance;
    private LoggerImpl() {
        this.minLogLevel = LogLevel.INFO;
        this.appenders = new ArrayList<>();
        this.executorService = Executors.newFixedThreadPool(5);
        this.repository = new LogRepository();
    }

    // Singleton instance
    public static LoggerImpl getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new LoggerImpl();
                }
            }
        }
        return instance;
    }

    public void addAppender(Appender appender) {
        this.appenders.add(appender);
    }

    public void removeAppender(Appender appender) {
        this.appenders.remove(appender);
    }

    public void setMinLogLevel(LogLevel minLogLevel) {
        this.minLogLevel = minLogLevel;
    }

    private void log(LogLevel level, String message) {
        LocalDateTime timestamp = LocalDateTime.now();
        if (level.ordinal() >= minLogLevel.ordinal()) {
            String logEntryStr = timestamp + " [" + level + "] " + message;
            LogEntry entry = new LogEntry(timestamp, level, message);

            // Store in repository
            repository.addLog(entry);

            // Write asynchronously
            executorService.submit(() -> {
                for (Appender appender : appenders) {
                    appender.append(logEntryStr, level);
                }
            });
        }
    }

    @Override
    public void info(String content) {
        log(LogLevel.INFO, content);
    }

    @Override
    public void error(String content) {
        log(LogLevel.ERROR, content);
    }

    @Override
    public void debug(String content) {
        log(LogLevel.DEBUG, content);
    }

    @Override
    public void warning(String content) {
        log(LogLevel.WARNING, content);
    }

    // Expose repository search methods
    public LogRepository getRepository() {
        return repository;
    }
}


class LogSearchCriteria {
    private LogLevel level;              // optional
    private String keyword;              // optional
    private LocalDateTime from;          // optional
    private LocalDateTime to;            // optional

    public LogLevel getLevel() { return level; }
    public String getKeyword() { return keyword; }
    public LocalDateTime getFrom() { return from; }
    public LocalDateTime getTo() { return to; }

    // Builder pattern for flexible creation
    public static class Builder {
        private LogLevel level;
        private String keyword;
        private LocalDateTime from;
        private LocalDateTime to;

        public Builder level(LogLevel level) { this.level = level; return this; }
        public Builder keyword(String keyword) { this.keyword = keyword; return this; }
        public Builder from(LocalDateTime from) { this.from = from; return this; }
        public Builder to(LocalDateTime to) { this.to = to; return this; }

        public LogSearchCriteria build() {
            LogSearchCriteria criteria = new LogSearchCriteria();
            criteria.level = this.level;
            criteria.keyword = this.keyword;
            criteria.from = this.from;
            criteria.to = this.to;
            return criteria;
        }
    }
}


class Runner{
    public static void main(String[] args) {
        LoggerImpl logger = LoggerFactory.getCombinedLogger("app.log");
        logger.setMinLogLevel(LogLevel.DEBUG);

        logger.info("Application started");
        logger.debug("Loading config...");
        logger.error("Null pointer exception");

        try {
            Thread.sleep(1000); // allow async logs to flush
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 🔍 Search examples
        LogRepository repo = logger.getRepository();
        System.out.println("\n-- ERROR Logs --");
        repo.searchByLevel(LogLevel.ERROR).forEach(System.out::println);

        System.out.println("\n-- Logs containing 'config' --");
        repo.searchByKeyword("config").forEach(System.out::println);

        System.out.println("\n-- Logs in last 2 minutes --");
        repo.searchByTimeRange(LocalDateTime.now().minusMinutes(2), LocalDateTime.now())
                .forEach(System.out::println);


        // ----- Unified Search -----
        LogSearchCriteria criteria = new LogSearchCriteria.Builder()
                .level(LogLevel.ERROR) // optional
                .keyword("exception")  // optional
                .from(LocalDateTime.now().minusMinutes(5)) // optional
                .to(LocalDateTime.now())                   // optional
                .build();

        System.out.println("\n-- In-Memory Filtered Logs --");
        logger.getRepository().search(criteria).forEach(System.out::println);

//        logger.shutdown();
    }
}





