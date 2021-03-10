package com.gzqylc.docker.admin.web.logger;

import javax.websocket.Session;
import java.io.File;
import java.io.IOException;

public class LogTailSender {
    Session session;
    File file;
    java.lang.Thread thread;

    public LogTailSender(File file, Session session) {
        this.session = session;
        this.file = file;

        TailFile tail = new TailFile(file, 500, msg -> {
            sendMsg(msg);

            return msg;
        });

        thread = new java.lang.Thread(tail);
    }

    public void start() {
        thread.start();

    }

    private void sendMsg(String msg) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(msg);
            } else {
                thread.stop();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        thread.stop();
    }
}
