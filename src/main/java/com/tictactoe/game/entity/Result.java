package com.tictactoe.game.entity;

public class Result {

    private int[][] chessboards;
    private String message;

    public int[][] getChessboards() {
        return chessboards;
    }

    public void setChessboards(int[][] chessboards) {
        this.chessboards = chessboards;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
