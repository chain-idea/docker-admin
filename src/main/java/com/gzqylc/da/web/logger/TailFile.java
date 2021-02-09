package com.gzqylc.da.web.logger;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.function.Function;

public class TailFile implements Runnable {

    private boolean debug = false;

    private int sleepTime;
    private long lastFilePosition = 0;
    private boolean shouldIRun = true;
    private File crunchifyFile = null;
    private static int crunchifyCounter = 0;

    Function<String, String> callback;

    public TailFile(File myFile, int myInterval, Function<String, String> callback) {
        crunchifyFile = myFile;
        this.sleepTime = myInterval;
        this.callback = callback;
    }

    private void printLine(String message) {
        if (callback != null) {
            try {
                message = new String(message.getBytes("ISO-8859-1"),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            callback.apply(message);
        }
    }

    public void stopRunning() {
        shouldIRun = false;
    }

    public void run() {
        try {
            while (shouldIRun) {
                Thread.sleep(sleepTime);
                long fileLength = crunchifyFile.length();
                if (fileLength > lastFilePosition) {

                    // Reading and writing file
                    RandomAccessFile accessFile = new RandomAccessFile(crunchifyFile, "r");
                    accessFile.seek(lastFilePosition);
                    String crunchifyLine = null;
                    while ((crunchifyLine = accessFile.readLine()) != null) {
                        this.printLine(crunchifyLine);
                        crunchifyCounter++;
                    }
                    lastFilePosition = accessFile.getFilePointer();
                    accessFile.close();
                } else {
                    if (debug)
                        this.printLine("Hmm.. Couldn't found new line after line # " + crunchifyCounter);
                }
            }
        } catch (Exception e) {
            stopRunning();
        }
        if (debug)
            this.printLine("Exit the program...");
    }




}