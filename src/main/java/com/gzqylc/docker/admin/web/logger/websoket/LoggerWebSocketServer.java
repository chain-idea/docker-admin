package com.gzqylc.docker.admin.web.logger.websoket;

import com.gzqylc.docker.admin.web.logger.LogTailSender;
import com.gzqylc.docker.admin.web.logger.LoggerConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint(value = LoggerConstants.WEBSOCKET_URL_PREFIX + "{logger}")
@Slf4j
@Component
public class LoggerWebSocketServer {


    private static ConcurrentHashMap<Session, LogTailSender> sessionList = new ConcurrentHashMap<>();


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("logger") String logger) {
        try {
            session.getBasicRemote().sendText("日志服务后台连接成功 ------ [ ID = " + logger + " ]");


            File file = new File(LoggerConstants.getLogPath(logger));
            session.getBasicRemote().sendText("日志路径 " + file.getAbsolutePath());

            LogTailSender logTailSender = new LogTailSender(file, session);
            sessionList.put(session, logTailSender);

            logTailSender.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("有连接加入，当前连接数为：{}, sesssionId:{}", sessionList.size(), session.getId());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        LogTailSender tailSender = sessionList.remove(session);
        if (tailSender != null) {
            tailSender.stop();
        }
        log.info("有连接关闭，当前连接数为：{}", sessionList.size());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("来自客户端的消息：{}", message);
    }

    /**
     * 出现错误
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误：{}，Session ID： {}", error.getMessage(), session.getId());
    }

    /**
     * 发送消息，实践表明，每次浏览器刷新，session会发生变化。
     */
    public static void sendMessage(Session session, String message) {
        try {
            RemoteEndpoint.Basic remote = session.getBasicRemote();
            remote.sendText(message);
        } catch (IOException e) {
            log.error("发送消息出错：{}", e.getMessage());
        }
    }


}
