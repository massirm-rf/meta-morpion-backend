package fr.uparis.morpion.metamorpionback.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Random;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class GameC {
    private static final String ROOM_FORMAT = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";


    private String roomCode;
    private  Grid grid;

    private Player player1, player2;
    private Player winner;
    private String currentPlayerId = "";
    private String subgridToPlayId;
    private CellStatus currentSymbol = CellStatus.X;
    private boolean started = false;
    private boolean finished = false;
    private GameType gameType;

}