package com.crazygame.tankarena.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;

public class FileLog {
    private static FileLog logger;

    private final LinkedList<String> logs = new LinkedList<>();
    private BufferedWriter logWriter;

    public static void initLogger(Context context) {
        logger = new FileLog(context);
    }

    public static void log(String msg) {
        logger.logMsg(msg);
    }

    public static void flush() {
        logger.flushMsg();
    }

    private FileLog(Context context) {
        File logFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "log.txt");

        try {
            logWriter = new BufferedWriter(new FileWriter(logFile));
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void logMsg(String msg) {
        logs.add(msg);
    }

    public void flushMsg() {
        try {
            for (String s : logs) {
                logWriter.write(s);
                logWriter.newLine();
            }

            logWriter.flush();
            logs.clear();

        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
