package com.tictactoe.game.controller;

import com.alibaba.fastjson.JSON;
import com.tictactoe.game.entity.Move;
import com.tictactoe.game.entity.Result;
import com.tictactoe.game.utils.ChessboardUtil;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/websocket/{role}/{socketId}")
public class WebSocketOneToOne {
    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount;
    //实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key为用户标识
    private static Map<String,WebSocketOneToOne> connections = new ConcurrentHashMap<>();
    private static Map<String,Boolean> plays = new ConcurrentHashMap<>();
    static {
        plays.put("1",true);
        plays.put("2",true);
    }

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    private String role;
    private String socketId;
    private static int[][] chessboards=new int[3][3];
    /**
     * 连接建立成功调用的方法
     *
     * @param session
     *            可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(@PathParam("role") String role,@PathParam("socketId") String socketId, Session session) {
        this.session = session;

        this.role = role;             //用户标识
        this.socketId = socketId;         //会话标识

        connections.put(role,this);     //添加到map中


        addOnlineCount();               // 在线数加
        System.out.println("有新连接加入！新用户："+role+",当前在线人数为" + getOnlineCount());

        Move move= new Move();
        move.setMessage("初始化");

        String string ="初始化";  //需要发送的信息
        String to = "";      //发送对象的用户标识

        if(role.equals("1")){
            to="2";
        }else{
            to="1";
        }
        send(string,role,to,socketId,chessboards);

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        connections.remove(role);  // 从map中移除
        subOnlineCount();          // 在线数减
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     *            客户端发送过来的消息
     * @param session
     *            可选的参数
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
        Move move= JSON.parseObject(message, Move.class);

        String string = move.getMessage();  //需要发送的信息
        String to = move.getRole();      //发送对象的用户标识
        int row=move.getRow();
        int column=move.getColumn();
        if(role.equals("1")){
            chessboards[row][column]=1;

        }else{
            chessboards[row][column]=-1;
        }

            send(string,role,to,socketId,chessboards);




    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();

    }


    //发送给指定角色
    public static void send(String msg,String from,String to,String socketId,int[][] chessboards1){

        Result result=new Result();
        boolean finish=ChessboardUtil.isFinish(chessboards1);
        int flag= ChessboardUtil.isSuccess(chessboards1);
        String message=null;
        result.setChessboards(chessboards1);

        try {



            //to指定用户
            WebSocketOneToOne con = connections.get(to);
            if(con!=null){
                if(socketId==con.socketId||con.socketId.equals(socketId)) {


                    if (flag == 1) {
                        if (con.role.equals("1")) {
                            message = "你赢了";
                        } else {
                            message = "你输了";
                        }
                        chessboards=new int[3][3];
                    } else if (flag == -1) {
                        if (con.role.equals("2")) {
                            message = "你赢了";
                        } else {
                            message = "你输了";
                        }
                        chessboards=new int[3][3];
                    } else {
                        if(finish){
                            message = "平局";
                            chessboards=new int[3][3];
                        }else {
                            message = "继续下";
                        }

                    }
                    result.setMessage(message);
                    String m = JSON.toJSONString(result);

                    con.session.getBasicRemote().sendText(m);
                }
            }


            //from具体用户
            WebSocketOneToOne confrom = connections.get(from);
            if(confrom!=null){
                if(socketId==confrom.socketId||confrom.socketId.equals(socketId)) {
                    if (flag == 1) {
                        if (confrom.role.equals("1")) {
                            message = "你赢了";
                        } else {
                            message = "你输了";
                        }
                        chessboards=new int[3][3];
                    } else if (flag == -1) {
                        if (confrom.role.equals("2")) {
                            message = "你赢了";
                        } else {
                            message = "你输了";
                        }
                        chessboards=new int[3][3];
                    } else {
                        if(finish){
                            message = "平局";
                            chessboards=new int[3][3];
                        }else {
                            message = "继续下";
                        }
                    }
                    result.setMessage(message);
                    String m = JSON.toJSONString(result);
                    confrom.session.getBasicRemote().sendText(m);
                }
            }


    }catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketOneToOne.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketOneToOne.onlineCount--;
    }

}
