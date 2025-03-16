/* Battleshps
 * Author: User
 * Creation date: 16/12/2024
 */
MACHINE
    Battleshps
    
SETS
    PLAYERS = {Player1, Player2};
    STATUS = {Deployment_phase, Both_need_deploy_their_fleet, One_need_to_Deploy, Ongoing, Player1_wins, Player2_wins} ;
    MESSAGES = {Error_Invalid_number_of_positions, Error_Target_out_of_bounds, Player_1_Hit_and_Win, Player_2_Hit_and_Win, Success,     
        Player_1_Hit, Player_2_Hit, Player_1_Miss, Player_2_Miss} 

CONSTANTS
    GRID,
    MAX_SHIPS

PROPERTIES
    GRID = (1..10) * (1..10) &
    MAX_SHIPS = 3

VARIABLES
    gridPlayer1, gridPlayer2,      // Grids storing ship positions for Player1 and Player2
    playerTurn,   
    gameState,
    player1Ships, player2Ships,   // Remaining ships for Player1 and Player2
    shotsPlayer1, shotsPlayer2    // Total shots taken by Player1 and Player2
    
INVARIANT
    gridPlayer1 <: GRID &               // Player1's grid positions must be within the grid
    gridPlayer2 <: GRID &
    card(gridPlayer1) <= MAX_SHIPS &    // Player1 & Player2 cannot have more than MAX_SHIPS
    card(gridPlayer2) <= MAX_SHIPS &
    playerTurn : PLAYERS &
    gameState : STATUS &
    player1Ships : NAT  &             
    player2Ships : NAT  &
    shotsPlayer1 : NAT  &
    shotsPlayer2 : NAT 

INITIALISATION
    gridPlayer1 := {} ||
    gridPlayer2 := {} ||
    playerTurn := Player1 ||
    gameState := Both_need_deploy_their_fleet ||   // Initial game state: players need to deploy their fleets
    player1Ships := 0 ||                           // Ships initialized to 0 until deployed
    player2Ships := 0 ||
    shotsPlayer1 := 0 ||
    shotsPlayer2 := 0

OPERATIONS
    
//---------------- Deploy ships for the given player-------------------------------
    report <-- deployFleet(player, positions) =
    PRE
        gameState : {Both_need_deploy_their_fleet, One_need_to_Deploy} &  // Only allow during deployment
        player : PLAYERS & 
        positions <: GRID &                                    // Positions must be within the grid
        card(positions) = MAX_SHIPS &                          // Number of positions must equal MAX_SHIPS
        (player = Player1 => gridPlayer1 = {}) &               // Player1's & Player2's grids must be empty
        (player = Player2 => gridPlayer2 = {}) &
        player = playerTurn
    THEN
        IF card(positions) = MAX_SHIPS  
        THEN
            IF player = Player1 
            THEN
                gridPlayer1 := positions ||
                player1Ships := MAX_SHIPS || 
                IF gridPlayer2 = {} 
                THEN
                    gameState := One_need_to_Deploy ||
                    report := Success ||
                    playerTurn := Player2
                ELSE
                    gameState := Ongoing
                END
            ELSE
                gridPlayer2 := positions ||
                player2Ships := MAX_SHIPS ||
                gameState := Ongoing||
                playerTurn := Player1 ||
                report := Success 
            END
        ELSE
            report := Error_Invalid_number_of_positions
        END
    END;

    
//------------ Player shooting at the target grid ------------------------------------------  
    report <-- playerShoots(target) =
    PRE
        target : GRID &                                    // Target must be a valid grid position
        gameState = Ongoing                        
    THEN
        IF target /: GRID
        THEN
            report := Error_Target_out_of_bounds
        ELSE
            IF playerTurn = Player1 
            THEN
                shotsPlayer1 := shotsPlayer1 + 1 ||
                IF target : gridPlayer2 
                THEN
                    gridPlayer2 := gridPlayer2 - {target} ||  
                    player2Ships := player2Ships - 1 ||
                    IF player2Ships = 1 
                    THEN
                        gameState := Player1_wins ||
                        report := Player_1_Hit_and_Win
                    ELSE
                        playerTurn := Player2 ||
                        report := Player_1_Hit
                    END
                ELSE
                    playerTurn := Player2 ||
                    report := Player_1_Miss
                END 
            ELSE
                shotsPlayer2 := shotsPlayer2 + 1 ||
                IF target : gridPlayer1 
                THEN
                    gridPlayer1 := gridPlayer1 - {target} ||  
                    player1Ships := player1Ships - 1 ||
                    IF player1Ships = 1 
                    THEN
                        gameState := Player2_wins ||
                        report := Player_2_Hit_and_Win
                    ELSE
                        playerTurn := Player1 ||
                        report := Player_2_Hit
                    END
                ELSE
                    playerTurn := Player1 ||
                    report := Player_2_Miss
                END
            END
        END
    END;
    
  
//-------------Locations of ships for players--------------------
    shipsquares <-- shipLocations(player) =
    PRE player : PLAYERS
    THEN
        IF player = Player1
        THEN
            shipsquares := gridPlayer1
        ELSE
            shipsquares := gridPlayer2
        END
    END;


//-------------Remaining ships for both players--------------------
    shipCounts <-- shipsLeft =
    BEGIN
        shipCounts := (player1Ships, player2Ships)
    END;


//-------------Number of shots taken -----------------------------
    shotCount <-- shotsTaken(player) =
    PRE player : PLAYERS
    THEN
        IF player = Player1 
        THEN
            shotCount := shotsPlayer1
        ELSE
            shotCount := shotsPlayer2
        END
    END;



//-------------Current status of the game -----------------------------
    report <-- gameStatus =
    BEGIN
        IF gameState = Both_need_deploy_their_fleet 
        THEN
            report := Both_need_deploy_their_fleet
        ELSIF gameState = One_need_to_Deploy 
        THEN
            report := One_need_to_Deploy
        ELSIF gameState = Deployment_phase 
        THEN
            report := Deployment_phase
        ELSIF gameState = Ongoing 
        THEN
            report := Ongoing
        ELSIF gameState = Player1_wins 
        THEN
            report := Player1_wins
        ELSE
            report := Player2_wins
        END
    END;
    
    

//-------------Current player whose turn it is -----------------------------
    result <-- getCurrentPlayer =
    BEGIN
        result := playerTurn
    END;



//--------------Check and return the winner-------------------------------
    winner <-- checkWinner =
    PRE
        gameState : {Player1_wins, Player2_wins}
    THEN
        IF gameState = Player1_wins THEN
            winner := Player1
        ELSE
            winner := Player2
        END
    END;



//--------------Restart the game to initial state ----------
    restartGame =
    PRE
        gameState : {Player1_wins, Player2_wins, Both_need_deploy_their_fleet, One_need_to_Deploy} 
    THEN
        gridPlayer1 := {} ||
        gridPlayer2 := {} ||
        playerTurn := Player1 ||
        gameState := Both_need_deploy_their_fleet ||
        player1Ships := 0 ||
        player2Ships := 0 ||
        shotsPlayer1 := 0 ||
        shotsPlayer2 := 0
    END
    
     

END

