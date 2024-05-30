package com.kun.dto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NIOServer extends Thread{
    public void run(){
        // 创建selector和channel
        try(Selector selector = Selector.open();
            ServerSocketChannel serverSocket = ServerSocketChannel.open();){
            // channel绑定ip和端口
            serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(),8080));
            // NoBlocking 非阻塞
            serverSocket.configureBlocking(false);
            // 注册到selector，并说明关注点为accept
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            while(true){
                // 阻塞等待就绪的客户端channel
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                // 轮询selectionkeys 如果有可读或者可写的channel，通知
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    // do sth 通过当前轮询到的key，获取该key对应的channel，向channel中写入内容
                    doSomething((ServerSocketChannel) key.channel());
                    iterator.remove();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void doSomething(ServerSocketChannel server) throws IOException{
        try(SocketChannel client = server.accept();){
            client.write(Charset.defaultCharset().encode("Hello there is server"));
        }
    }

    public static void main(String[] args) {
        NIOServer server = new NIOServer();
        server.start();

        for(int i=0;i<100;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 客户端通过socket绑定服务端ip和端口
                    // 开辟一个buffer用来读从channel中获取的信息
                    try(Socket client = new Socket(InetAddress.getLocalHost(),8080);){
                        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        br.lines().forEach(System.out::println);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
