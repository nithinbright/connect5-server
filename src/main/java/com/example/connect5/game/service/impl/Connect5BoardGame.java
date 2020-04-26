package com.example.connect5.game.service.impl;

import com.example.connect5.controller.Connect5BoardGameController;
import com.example.connect5.domain.DropResponse;
import com.example.connect5.game.service.BoardGameService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
@Service
public class Connect5BoardGame implements BoardGameService {

    public static final String YOU_WON_THE_GAME = "**********YOU WON THE GAME!!!******";
    private static String lastPlayer;

    private static ConcurrentMap<String, String> activePlayers;
    private static final char[] PLAYERS = {'O', 'X'};
    private final static String PLAYER1="player1";
    private final static String PLAYER2="player2";
    private static char[][] grid;
    @Value("${connect5.grid.colums}")
    private static int noOfGridColumns=9;
    @Value("${connect5.grid.rows}")
    private static int noOfGridRows=6;
    private static int  lastCol;
    private static int lastTop;
    private static int totalMovesMade;
    private static String broadcastMessage;
    static{
        init();
    }


    private static void init() {
        activePlayers=new ConcurrentHashMap<String,String>();
       // activePlayers.put(PLAYER1, null);
       // activePlayers.put(PLAYER2,null);
        grid=new char[noOfGridColumns][noOfGridColumns];
        lastCol = -1;
        lastTop = -1;
        totalMovesMade=0;
        broadcastMessage="";
        //Initialize the grid with '.'
        for (int i = 0; i < noOfGridRows; i++) {
            Arrays.fill(grid[i] = new char[noOfGridColumns], '.');
        }
    }

    @Override
    public char getSymbolForPlayer(String playerName) {
        if(activePlayers.get(PLAYER1)!= null){
            if(activePlayers.get(PLAYER1).equalsIgnoreCase(playerName)){
                return PLAYERS[0];
            }
        }/*else{
            activePlayers.put(PLAYER1,playerName);
            //Update the broadcast message
           // broadcastMessage="Game Started: Player "+ playerName +" is making his move";
            return PLAYERS[0];
        }*/

        if(activePlayers.get(PLAYER2)!= null){
            if(activePlayers.get(PLAYER2).equalsIgnoreCase(playerName)){
                return PLAYERS[1];
            }
        }/*else{
            activePlayers.put(PLAYER2,playerName);
            //Update the broadcast message
            //broadcastMessage="Game Started: Player "+ playerName +" is making his move";
            return PLAYERS[1];
        }*/
        return '.';
    }



    @Override
    public DropResponse chooseAndDrop(char playerSymbol, int column) {
        DropResponse response = new DropResponse();
        //Check if the position is occupied
        if (isPositionEmpty(column)) {

        //Check if all the moves are done
        if (isFull()) {
            response.setMessage("####### GAME OVER: IT'S A DRAW !!##########");
            //Set the broadcast message
            broadcastMessage = "####### GAME OVER: IT'S A DRAW !!##########";
            return response;
        }

        if(broadcastMessage!=null && broadcastMessage!= ""){
            response.setMessage(broadcastMessage);
            response.setGameOver(true);
            return response;
        }
        int i = 0;
        for (i = 0; i < noOfGridRows; i++) {
            if (grid[i][column] == 'O' || grid[i][column] == 'X') {
                grid[i - 1][column] = playerSymbol;
                break;
            }
        }
        if (i == noOfGridRows)
            grid[i - 1][column] = playerSymbol;

        totalMovesMade++;
        boolean isWinningMove = isWinningMove(i - 1, column);
        if (isWinningMove) {
            response.setGameOver(true);
            response.setMessage(YOU_WON_THE_GAME);
            //Update the broadcast message
            broadcastMessage = "###### Game Over: Player " + getPlayerFromSymbol(playerSymbol) + " won the game #####";
        } else {
            response.setGameOver(false);
            response.setMessage(Connect5BoardGameController.WAIT_FOR_YOUR_TURN);
        }
        lastPlayer = getPlayerFromSymbol(playerSymbol);
    }
        return response;

    }

    @Override
    public String enterThePlayer(String playerName) {
        StringBuffer playerSymbol=new StringBuffer("");
        if(activePlayers.get(PLAYER1)== null){
            activePlayers.put(PLAYER1,playerName);
           return playerSymbol.append("##### Your Symbol is "+PLAYERS[0]+" ####").toString();
        }
        if(activePlayers.get(PLAYER2)== null){
            activePlayers.put(PLAYER2,playerName);
           return playerSymbol.append("##### Your Symbol is "+PLAYERS[1]+" ####").toString();
        }
        return playerSymbol.toString();
    }


    private String getPlayerFromSymbol(char playerSymbol) {
        if(playerSymbol=='O'){
            return activePlayers.get(PLAYER1);
        }
        return activePlayers.get(PLAYER2);
    }

    private boolean isWinningMove(int row, int column){
        int num=grid[row][column];
        int count=0;
        int i=column;

        //HORIZONTAL.
        while(i<noOfGridColumns && grid[row][i] == num){
            count++;
            i++;
        }
        i=column-1;
        while(i>=0 && grid[row][i] == num){
            count++;
            i--;
        }
        if(count == 5)
            return true;

        //VERTICAL.
        count=0;
        int j=row;
        while(j<noOfGridRows && grid[j][column] == num){
            count++;
            j++;
        }
        if(count == 5)
            return true;

        //SECONDARY DIAGONAL.
        count=0;
        i=row;
        j=column;
        while(i<noOfGridColumns && j<noOfGridRows && grid[i][j] == num){
            count++;
            i++;
            j++;
        }
        i=row-1;
        j=column-1;
        while(i>=0 && j>=0 && grid[i][j] == num){
            count++;
            i--;
            j--;
        }
        if(count == 5)
            return true;

        //LEADING DIAGONAL.
        count=0;
        i=row;
        j=column;
        while(i<noOfGridColumns && j>=0 && grid[i][j] == num){
            count++;
            i++;
            j--;
        }
        i=row-1;
        j=column+1;
        while(i>=0 && j<noOfGridRows && grid[i][j] == num){
            count++;
            i--;
            j++;
        }
        if(count == 5)
            return true;

        return false;
    }

    private boolean isPositionEmpty(int column){
        return grid[0][column] == '.';
    }

    private boolean isFull(){
        return totalMovesMade == noOfGridRows*noOfGridColumns;
    }

    @Override
    public Boolean exitThePlayer(String playerName) {
        if(playerName!= null) {
            //Check if the player is active
            if(activePlayers.get(PLAYER1)!= null) {
                if (activePlayers.get(PLAYER1).equalsIgnoreCase(playerName)) {
                    //Remove from active users
                    activePlayers.remove(PLAYER1);
                    //Update the broadcast message
                    broadcastMessage = "##### Game Over: Player " + playerName + " exited the game. ####";
                    return true;
                }
            }
            if(activePlayers.get(PLAYER2)!= null) {
                if (activePlayers.get(PLAYER2).equalsIgnoreCase(playerName)) {
                    //Remove from active users
                    activePlayers.remove(PLAYER2);
                    //Update the broadcast message
                    broadcastMessage = "##### Game Over: Player " + playerName + " exited the game.####";
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getLastPlayer() {
        //String lastPlayer=getLastPlayer();
        return lastPlayer;
    }

    @Override
    public String getBroadcastMessage() {
        return broadcastMessage;
    }

    @Override
    public char[][] getBoardState() {
        return grid;
    }

    @Override
    public String restart() {
    init();
    return "OK";
    }
}
