package com.example.connect5.controller;

import com.example.connect5.domain.DropResponse;
import com.example.connect5.domain.PlayerMove;
import com.example.connect5.game.service.BoardGameService;
import com.example.connect5.util.BoardGameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/***
 * Controller class that exposes REST APIs for the Connect5 Game
 */

@RestController
@RequestMapping("/api/v1")
public class Connect5BoardGameController {
    public static final String WAIT_FOR_YOUR_TURN = "Wait for your Turn";
    @Autowired
    private BoardGameService boardGameService;


    @PostMapping("/drop/{playerName}")
    ResponseEntity<DropResponse> drop(@RequestBody PlayerMove playerMove, @PathVariable String playerName) {

        if(!BoardGameUtil.isValidPlayerMove(playerMove)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //Check if the same player is trying to play out of turn
        if(playerName.equalsIgnoreCase(boardGameService.getLastPlayer())){
            DropResponse response= new DropResponse();
            response.setMessage(WAIT_FOR_YOUR_TURN);
            response.setGameOver(false);
            new ResponseEntity<DropResponse>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<DropResponse>(boardGameService.chooseAndDrop(boardGameService.getSymbolForPlayer(playerName),
                playerMove.getColumn()), HttpStatus.OK);

    }

    @GetMapping("/grid")
    ResponseEntity<String> getCurrentGrid(){
        return new ResponseEntity<String>(BoardGameUtil.printBoard(boardGameService.getBoardState()),HttpStatus.OK);
    }

    @GetMapping("/lastplayer")
    ResponseEntity<String> getLastPlayer(){
        return new ResponseEntity<String>(boardGameService.getLastPlayer(),HttpStatus.OK);
    }

    @GetMapping("/poll")
    ResponseEntity<String> getBroadcastMessage(){
        return new ResponseEntity<String>(boardGameService.getBroadcastMessage(),HttpStatus.OK);
    }

    @PostMapping("/enter/{playerName}")
    ResponseEntity<String> enterTheGame(@PathVariable String playerName) {
        return new ResponseEntity<String>(boardGameService.enterThePlayer(playerName), HttpStatus.OK);
    }

    @PostMapping("/exit/{playerName}")
    ResponseEntity<Boolean> exitTheGame(@PathVariable String playerName) {
        return new ResponseEntity<Boolean>(boardGameService.exitThePlayer(playerName), HttpStatus.OK);
    }

    @PostMapping("/restart")
    ResponseEntity<String> restartTheGame() {

        return new ResponseEntity<String>(boardGameService.restart(),HttpStatus.OK);
    }
}
