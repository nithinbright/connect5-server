package com.example.connect5.controller;

import com.example.connect5.Connect5Application;
import com.example.connect5.domain.DropResponse;
import com.example.connect5.domain.PlayerMove;
import com.example.connect5.game.service.impl.Connect5BoardGame;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)

public class Connect5BoardGameControllerIntegrationTest {
    private static final String GET_BOARD_STATE_URL="http://localhost:8080/api/v1/grid";
    private static final String CHOOSE_AND_DROP_URL="http://localhost:8080/api/v1/drop/";
    private static final String GET_LAST_PLAYER_URL="http://localhost:8080/api/v1/lastplayer";
    private static final String GET_POLLING_URL= "http://localhost:8080/api/v1/poll";
    private static final String EXIT_PLAYER_URL= "http://localhost:8080/api/v1/exit/";
    private static final String ENTER_PLAYER_URL= "http://localhost:8080/api/v1/enter/";

    TestRestTemplate testRestTemplate;
    String playerName;


    @Before
    public void setup(){
        testRestTemplate = new TestRestTemplate();

    }

    /**
     * Tests for /enter endpoint
     */
    @Test
    public void testEnterPlayerIsSucessful(){
       String response1= enterTheGame("nit1");
       assert(response1!= null);
       //Enter the second player
        String response2= enterTheGame("nit2");
        assert(response2!= null);

    }

    private String enterTheGame(String playerName) {
        HttpEntity<String> request = new HttpEntity<>("");
        ResponseEntity<String> response=testRestTemplate.postForEntity(ENTER_PLAYER_URL+playerName, request, String.class);
        assert(response.getStatusCode()==HttpStatus.OK);
        if(response.getStatusCode()== HttpStatus.OK){
            assert (response.getBody()!= null);
            if(response.getBody()!= null){
                String playerSymbol= response.getBody();
                if(playerSymbol!=null){
                    return playerSymbol;
                }
            }
        }
        return null;
    }

    @Test
    public void testEnterTheSamePlayerAgain(){

    }

    @Test
    public void testEnterMoreThan2Players(){

    }
/*
Tests for /drop endpoint
 */
    @Test
    public void testDropWhenPlayerNameIsEmpty(){
        playerName="";
        PlayerMove chooseAndDropRequest= new PlayerMove();
        chooseAndDropRequest.setColumn(0);
        //Make a POST request to the Connect5 server
        HttpEntity<PlayerMove> request = new HttpEntity<>(chooseAndDropRequest);
        ResponseEntity<DropResponse> responseEntity=testRestTemplate.postForEntity(CHOOSE_AND_DROP_URL+playerName, request, DropResponse.class);
        assert(responseEntity.getStatusCode()== HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetEmptyGrid(){
        ResponseEntity<String> response
                = testRestTemplate.getForEntity(GET_BOARD_STATE_URL, String.class);
        assert (response.getStatusCode()==HttpStatus.OK);
        assert (response.getBody()!=null);
        assert(response.getBody().contains("."));

    }

    @Test
    public void testDropWithInvalidColumnNumber(){
        playerName="nit";
        int colum=12;
        PlayerMove chooseAndDropRequest= new PlayerMove();
        chooseAndDropRequest.setColumn(12);
        //Make a POST request to the Connect5 server
        HttpEntity<PlayerMove> request = new HttpEntity<>(chooseAndDropRequest);
        ResponseEntity<DropResponse> responseEntity=testRestTemplate.postForEntity(CHOOSE_AND_DROP_URL+playerName, request, DropResponse.class);
        assert(responseEntity.getStatusCode()== HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testDropWhenPlayerTriesToPlayOutOfTurn(){
        //Player nit plays the first time
        playerName="nit1";
        PlayerMove chooseAndDropRequest= new PlayerMove();
        chooseAndDropRequest.setColumn(0);
        //Make a POST request to the Connect5 server
        HttpEntity<PlayerMove> request = new HttpEntity<>(chooseAndDropRequest);
        ResponseEntity<DropResponse> responseEntity1=testRestTemplate.postForEntity(CHOOSE_AND_DROP_URL+playerName, request, DropResponse.class);

        //Player nit tries to play again
        ResponseEntity<DropResponse> responseEntity2=testRestTemplate.postForEntity(CHOOSE_AND_DROP_URL+playerName, request, DropResponse.class);
        assert(responseEntity2.getStatusCode()==HttpStatus.OK);
        assert (responseEntity2.getBody()!= null);
        assert (responseEntity2.getBody().getMessage().equalsIgnoreCase(Connect5BoardGameController.WAIT_FOR_YOUR_TURN));

    }

    @Test
    public void testDropWinnningScenario(){

        for(int i=0;i<5;i++) {
            //Player1 turn
            player1Turn(i);

            //Player 2 turn
            player2Turn(i);
        }
        //Test the grid
        testgetGridWithValues();
        //Test the poll, given nit1 has won
        testPollWhenOtherPlayerWins();
    }

    private void player2Turn(int i) {
        playerName="nit2";
        PlayerMove chooseAndDropRequest2= new PlayerMove();
        chooseAndDropRequest2.setColumn(1);
        //Make a POST request to the Connect5 server
        HttpEntity<PlayerMove> request2 = new HttpEntity<>(chooseAndDropRequest2);
        ResponseEntity<DropResponse> responseEntity2=testRestTemplate.postForEntity(CHOOSE_AND_DROP_URL+playerName, request2, DropResponse.class);
    }



    public void testgetGridWithValues(){
        ResponseEntity<String> response
                = testRestTemplate.getForEntity(GET_BOARD_STATE_URL, String.class);
        assert (response.getStatusCode()==HttpStatus.OK);
        assert (response.getBody()!=null);
        assert(response.getBody().contains("O"));

    }

    private void player1Turn(int i) {
        playerName="nit1";
        PlayerMove chooseAndDropRequest1= new PlayerMove();
        chooseAndDropRequest1.setColumn(0);
        //Make a POST request to the Connect5 server
        HttpEntity<PlayerMove> request1 = new HttpEntity<>(chooseAndDropRequest1);
        ResponseEntity<DropResponse> responseEntity1=testRestTemplate.postForEntity(CHOOSE_AND_DROP_URL+playerName, request1, DropResponse.class);
        //Check for the winning move
        if(i==4){
          assert(responseEntity1.getStatusCode()==HttpStatus.OK);
          if(responseEntity1.getStatusCode()== HttpStatus.OK){
              DropResponse response= responseEntity1.getBody();
              assert(response!= null);
              assert (response.getGameOver()==true);
              assert (response.getMessage().equalsIgnoreCase(Connect5BoardGame.YOU_WON_THE_GAME));
          }
        }
    }

    /**
     * Tests for /lastplayer endpoint
     */

    @Test
    public void testLastPlayer(){
        ResponseEntity<String> response
                = testRestTemplate.getForEntity(GET_LAST_PLAYER_URL, String.class);
        assert (response.getStatusCode()==HttpStatus.OK);
        assert (response.getBody()!= null);
        assert (response.getBody().equalsIgnoreCase("nit1"));

    }

    /**
     * Tests for /poll endpoint
     */


    public void testPollWhenOtherPlayerWins(){
        ResponseEntity<String> response
                = testRestTemplate.getForEntity(GET_POLLING_URL, String.class);
        assert (response.getStatusCode()==HttpStatus.OK);
        assert (response.getBody()!= null);
        assert (response.getBody().equalsIgnoreCase("###### Game Over: Player nit1 won the game #####"));
    }

    /**
     * Tests for /exit endpoint
     */


    public void testSuccessfulPlayerExit(){
        HttpEntity<String> request = new HttpEntity<>("");
        playerName="nit1";
        ResponseEntity<Boolean> response=testRestTemplate.postForEntity(EXIT_PLAYER_URL+playerName, request, Boolean.class);
        assert (response.getStatusCode()==HttpStatus.OK);
        assert (response.getBody()!=null);
        assert (response.getBody().booleanValue()==true);
        //Test /Poll when the player exits
        testPollWhenOtherPlayerExitsTheGame();
    }

    @Test
    public void testExitForNonExistingPLayer(){
        HttpEntity<String> request = new HttpEntity<>("");
        playerName="nit11";
        ResponseEntity<Boolean> response=testRestTemplate.postForEntity(EXIT_PLAYER_URL+playerName, request, Boolean.class);
        assert (response.getStatusCode()==HttpStatus.OK);
        assert (response.getBody()!=null);
        assert (response.getBody().booleanValue()==false);
    }


    public void testPollWhenOtherPlayerExitsTheGame(){
        ResponseEntity<String> response
                = testRestTemplate.getForEntity(GET_POLLING_URL, String.class);
        assert (response.getStatusCode()==HttpStatus.OK);
        assert (response.getBody()!= null);
        assert (response.getBody().equalsIgnoreCase("##### Game Over: Player nit1 exited the game.####"));

    }





}
