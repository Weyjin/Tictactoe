package com.tictactoe.game.utils;

public class ChessboardUtil {

    public static int isSuccess(int[][] chessboards){
        int flag=0;
           for(int i=0;i<chessboards.length;i++){
               int row=chessboards[i][0]+ chessboards[i][1]+chessboards[i][2];
               if(row==3){
                   flag=1;
                   break;
               }else if(row==-3){
                   flag=-1;
                   break;
               }
               int column=chessboards[0][i]+ chessboards[1][i]+chessboards[2][i];
               if(column==3){
                   flag=1;
                   break;
               }else if(column==-3){
                   flag=-1;
                   break;
               }
           }

           int left_diagonal=chessboards[0][0]+ chessboards[1][1]+chessboards[2][2];
           if(left_diagonal==3){
               flag=1;
           }else if(left_diagonal==-3){
               flag=-1;
           }
           int right_diagonal=chessboards[0][2]+ chessboards[1][1]+chessboards[2][0];
            if(right_diagonal==3){
                flag=1;
            }else if(right_diagonal==-3){
                flag=-1;
            }
           return flag;

    }

    public static boolean isFinish(int[][] chessboards){
        boolean success=true;
        for(int i=0;i<chessboards.length;i++){
            for(int j=0;j<chessboards[i].length;j++){
                if (chessboards[i][j]==0){
                    success=false;
                    break;
                }
            }
        }
        return success;
    }


}
