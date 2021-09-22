package com.he.transport.socket.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * 通过线程池子 来执行任务。
 */
public class RpcServer01 {
    private final ExecutorService threadPool;
    private static final int COREPOLLSIZE=5;
    private static final int MAXPOLLSIZE=50;
    private static final long KEEPALIVETIME=60;

    private static final Logger logger = LoggerFactory.getLogger(RpcServer01.class);

    public RpcServer01() {
        BlockingQueue<Runnable> workingQueue=new ArrayBlockingQueue<Runnable>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool=new ThreadPoolExecutor(COREPOLLSIZE
                , MAXPOLLSIZE
                , KEEPALIVETIME
                ,TimeUnit.SECONDS
                ,workingQueue
                ,threadFactory);
    }
    public void register(Object service,int port) throws IOException {
        ServerSocket serverSocket=new ServerSocket(port);
        try{
            logger.info("服务器正在启动...");
            Socket socket;
            while((socket=serverSocket.accept())!=null) {
                logger.info("客户端连接！Ip为：" + socket.getInetAddress());
                threadPool.execute(new WorkerThread(socket, service));
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
