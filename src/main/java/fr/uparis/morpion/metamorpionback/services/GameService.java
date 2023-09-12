package fr.uparis.morpion.metamorpionback.services;

import fr.uparis.morpion.metamorpionback.model.*;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private Game game;
    private Player currentPlayer;

    public Game initGame(Player starterPlayer, boolean starter) {

        this.game = Game.builder().player1(starterPlayer).grid(new Grid()).build();
        currentPlayer = (starter) ? game.getPlayer1() : game.getPlayer2();

        return game;

    }

    public Game joinGame(Player joinerPlayer) {

        if (joinerPlayer.getGameValue().compareTo(currentPlayer.getGameValue()) == 0) {
            throw new IllegalArgumentException("Game value already taken");
        }

        game.setPlayer2(joinerPlayer);

        return game;

    }

    public NextGridDTO fillGrid(GridDTO gridInfos) {
        int row = gridInfos.getRow();
        int column = gridInfos.getColumn();
        int childRow = gridInfos.getChildRow();
        int childColumn = gridInfos.getChildColumn();
        game.getGrid().getChildGrids()[row][column].setBox(childRow, childColumn, gridInfos.getValue());
        if(game.getGrid().getChildGrids()[childRow][childColumn].isFull()){
            return null;
        }
        NextGridDTO nextGridInfos = NextGridDTO.builder().row(childRow).column(childColumn).build();
        return nextGridInfos;
    }


}
