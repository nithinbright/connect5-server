package com.example.connect5.util;

import com.example.connect5.domain.PlayerMove;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BoardGameUtil {

    public static boolean isValidPlayerMove(PlayerMove playerMove) {
        if (playerMove.getColumn() == null) {
            return false;
        }
        if (playerMove.getColumn() > 8 || playerMove.getColumn() < 0) {
            return false;
        }
        return true;
    }

    public static String printBoard(char[][] board) {
        StringBuffer sb=new StringBuffer();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {

                    sb.append("["+board[i][j] + "]  ");
            }
            sb.append("\n");
        }


        return sb.toString();
    }
}
