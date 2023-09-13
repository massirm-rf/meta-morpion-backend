package fr.uparis.morpion.metamorpionback.services;

import fr.uparis.morpion.metamorpionback.model.*;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
@Getter
public class GameService {
    private Game game;

    public Game initGame(Player starterPlayer, boolean starter) {

        BoxEnum player2GameValue = (starterPlayer.getGameValue().compareTo(BoxEnum.x_value) == 0) ? BoxEnum.o_value : BoxEnum.x_value;
        Player player2 = Player.builder().playerName("player2").gameValue(player2GameValue).build();
        this.game = Game.builder().player1(starterPlayer).player2(player2).grid(new Grid()).build();

        game.setCurrentPlayer((starter) ? starterPlayer : player2);


        return game;

    }

    public NextGridDTO fillGrid(GridDTO gridInfos) {
        Integer row = gridInfos.getRow();
        Integer column = gridInfos.getColumn();
        Integer childRow = gridInfos.getChildRow();
        Integer childColumn = gridInfos.getChildColumn();
        BoxEnum value = gridInfos.getValue();


        if (value == BoxEnum.none ) {
            throw new IllegalArgumentException("value must be x or o");
        }

        Player nextPlayer;
        if( game.getPlayer1().getGameValue().compareTo(value) == 0 ) {
            nextPlayer = game.getPlayer2();
            game.setCurrentPlayer(game.getPlayer1());
        } else {
            nextPlayer = game.getPlayer1();
            game.setCurrentPlayer(game.getPlayer2());
        }

        NextGridDTO nextGridInfos = NextGridDTO.builder().row(childRow).column(childColumn).player(nextPlayer).build();
        BoxEnum childWinnerValue = game.getGrid().getChildGrids()[row][column].getWinner();

        if ( game.getGrid().getChildGrids()[row][column].isFull() || childWinnerValue != BoxEnum.none) {
            throw new IllegalArgumentException(" You must play a not completed grid !");
        }

        game.getGrid().getChildGrids()[row][column].setBox(childRow, childColumn, value);
        nextGridInfos.setLastChildFinished(childWinnerValue);
        game.getGrid().getChildGrids()[row][column].setWinnerValue(childWinnerValue);


        if(game.getGrid().getChildGrids()[childRow][childColumn].isFull() || game.getGrid().getChildGrids()[childRow][childColumn].getWinnerValue() != BoxEnum.none){
            nextGridInfos.setRow(null);
            nextGridInfos.setColumn(null);
            return nextGridInfos;
        }

        BoxEnum winnerValue = game.getGrid().calculateWinner();
        if (winnerValue != BoxEnum.none) {
            game.getGrid().setWinnerValue(winnerValue);
            nextGridInfos.setFinished(true);
            nextGridInfos.setPlayer(game.getCurrentPlayer());
            return nextGridInfos;
        }

        return nextGridInfos;
    }

    public Boolean quitGame(){
        this.game = null;
        return Boolean.TRUE;
    }


}
