package com.kun.dto;

import org.eclipse.jetty.util.BlockingArrayQueue;
import org.eclipse.jetty.util.IO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DemoServer extends Thread{
    private ServerSocket serverSocket;

    public int getPort(){
        return serverSocket.getLocalPort();
    }

    public void run(){
        ExecutorService executorService = new ThreadPoolExecutor(5,10,10000, TimeUnit.MILLISECONDS,
                new BlockingArrayQueue<>());

        try{
            serverSocket = new ServerSocket(0);
            while(true){
                // 阻塞地等待一个连接
                Socket socket = serverSocket.accept();
                RequestHandler requestHandler = new RequestHandler(socket);
                executorService.execute(requestHandler);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        DemoServer server = new DemoServer();
        // 开始监听
        server.start();


        for(int i=0;i<50;i++) {
            // 这里的服务端ip地址就是本机的ip
            try(Socket client = new Socket(InetAddress.getLocalHost(),server.getPort())){
                BufferedReader buffer = new BufferedReader(new InputStreamReader(client.getInputStream()));
                buffer.lines().forEach(System.out::println);
            }catch (IOException e){
                e.printStackTrace();
            }

//            Thread.sleep(2000);
        }
    }



    }



class RequestHandler extends Thread{
    private Socket socket;
    public RequestHandler(Socket socket){
        this.socket = socket;
    }

    public void run(){
        try(PrintWriter pw = new PrintWriter(socket.getOutputStream())){
            pw.println("hello world");
            pw.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
