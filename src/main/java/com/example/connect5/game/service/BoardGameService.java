package com.example.connect5.game.service;

import com.example.connect5.domain.DropResponse;
import org.springframework.stereotype.Service;

@Service
public interface BoardGameService {
    char getSymbolForPlayer(String playerName);
    DropResponse chooseAndDrop(char symbol, int column);
    String enterThePlayer(String playerName);
    Boolean exitThePlayer(String playerName);
    String getLastPlayer();
    String getBroadcastMessage();
    char[][] getBoardState();
    String restart();
}
