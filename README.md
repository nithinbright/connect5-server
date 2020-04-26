#connect5-server

#Description
connect5-server is a spring boot application which maintains the state of the Connect5 game and also exposes some REST
APIs for the clients to connect and play the game.

#Steps  to run
1. mvn clean install generates a jar file inside the target folder
2.java -jar connect5-0.0.1-SNAPSHOT.jar
3. This starts the server application and it can accept requests from the client over http on port 8080

#REST APIS exposed:
    http://localhost:8080/api/v1/grid : This returns the state of the Connect5 Board.
	http://localhost:8080/api/v1/drop: This POST request given the playername and column number, makes the game possible. It
	also retruns whether the game ended due to a draw,Win or if the grid is full.
	http://localhost:8080/api/v1/lastplayer
	This GET request returns the name of the last player who made a move.
	http://localhost:8080/api/v1/poll
	This GET request makes the asynchronous event mechanism possible for events such as waiting for turns,informing the
	other player of exits,wins,draws,etc.
	http://localhost:8080/api/v1/exit
	This POST request exits the player from the game
	http://localhost:8080/api/v1/enter
	This POST request enters the player into the game. This is the first step of the game.

#Tests

Integration tests for the Connect5BoardGameController has been written to check for several scenarios.

#Limitations
After every game the server application has to be stopped/started.

#Improvements that can be made
1. Implement the Restart Game API
2. Refactor the code to remove some hardcoding and other issues
3. Active Users can be maintained using session based logic
4. State of the game can be persisted in DB instead of in-memory
5. Further unit tests can be implemented to improve the code coverage.


