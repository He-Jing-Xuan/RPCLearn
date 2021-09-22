package com.he.transport.socket.client;

import com.he.common.entity.RpcRequest;
import com.he.transport.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
    private RpcRequest rpcRequest;
    private String host;
    private int port;

    public Object sendRequest(RpcRequest rpcRequest,String host,int port) throws IOException, ClassNotFoundException {
        this.rpcRequest = rpcRequest;
        this.host = host;
        this.port = port;
        Socket socket = new Socket(host, port);
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("调用时发生错误");
            return null;
        }


    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        return null;
    }
}
