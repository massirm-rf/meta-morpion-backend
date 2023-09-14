package fr.uparis.morpion.metamorpionback.services;

import fr.uparis.morpion.metamorpionback.model.*;
import fr.uparis.morpion.metamorpionback.network.Network;
import lombok.Getter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static fr.uparis.morpion.metamorpionback.utils.Constants.HEIGHT;
import static fr.uparis.morpion.metamorpionback.utils.Constants.WIDTH;

@Service
@Getter
public class GameService {
    private final Network network;
    private final SimpMessagingTemplate template;
    private Game game;

    public GameService(Network network, SimpMessagingTemplate template) {
        this.network = network;
        this.template = template;
    }

    public Game initGame(boolean vsAI, String ip, Player starterPlayer, boolean starter, boolean isFrontend) {

        BoxEnum player2GameValue = (starterPlayer.getGameValue().compareTo(BoxEnum.x_value) == 0) ? BoxEnum.o_value :
                BoxEnum.x_value;
        Player player2 = Player.builder().playerName("player2").gameValue(player2GameValue).ai(vsAI).build();
        this.game = Game.builder().player1(starterPlayer).player2(player2).grid(new Grid()).build();
        game.setCurrentPlayer((starter) ? starterPlayer : player2);

        if (ip != null) {
            game.setIp(ip);
            if (isFrontend) {
                Map<String, Object> params = new HashMap<>();
                params.put("ip", ip);
                params.put("starter", starter);
                params.put("vsAI", vsAI);
                network.initGame(params, starterPlayer);
            }
        }

        template.convertAndSend("/init-game", game);

        if (isFrontend && game.getCurrentPlayer().isAi()) {
            fillGrid(playWithAILevel1(NextGridDTO.builder().player(game.getCurrentPlayer()).build(),
                    game.getCurrentPlayer().getGameValue()), true);
        }

        game.setEmpty(false);

        return game;
    }

    public NextGridDTO fillGrid(GridDTO gridInfos, boolean isFrontend) {
        Integer row = gridInfos.getRow();
        Integer column = gridInfos.getColumn();
        Integer childRow = gridInfos.getChildRow();
        Integer childColumn = gridInfos.getChildColumn();
        BoxEnum value = gridInfos.getValue();


        if (value == BoxEnum.none) {
            throw new IllegalArgumentException("value must be x or o");
        }

        Player nextPlayer;
        if (game.getPlayer1().getGameValue().compareTo(value) == 0) {
            nextPlayer = game.getPlayer2();
            game.setCurrentPlayer(game.getPlayer1());
        } else {
            nextPlayer = game.getPlayer1();
            game.setCurrentPlayer(game.getPlayer2());
        }

        NextGridDTO nextGridInfos = NextGridDTO.builder().playedRow(row).playedColumn(column)
                .playedChildRow(childRow).playedChildColumn(childColumn)
                .nextRow(childRow).nextColumn(childColumn).player(nextPlayer).build();

        BoxEnum childWinnerValue = game.getGrid().getChildGrids()[row][column].getWinner();

        if (game.getGrid().getChildGrids()[row][column].isFull() || childWinnerValue != BoxEnum.none) {
            throw new IllegalArgumentException(" You must play a not completed grid !");
        }

        game.getGrid().getChildGrids()[row][column].setBox(childRow, childColumn, value);
        childWinnerValue = game.getGrid().getChildGrids()[row][column].getWinner();
        nextGridInfos.setLastChildFinished(childWinnerValue);
        game.getGrid().getChildGrids()[row][column].setWinnerValue(childWinnerValue);


        if (game.getGrid().getChildGrids()[childRow][childColumn].isFull() || game.getGrid().getChildGrids()[childRow][childColumn].getWinnerValue() != BoxEnum.none) {
            nextGridInfos.setNextRow(null);
            nextGridInfos.setNextColumn(null);
        }

        BoxEnum winnerValue = game.getGrid().calculateWinner();
        if (winnerValue != BoxEnum.none) {
            game.getGrid().setWinnerValue(winnerValue);
            game.setFinished(true);
            nextGridInfos.setFinished(true);
            nextGridInfos.setPlayer(game.getCurrentPlayer());
        }

        template.convertAndSend("/play", nextGridInfos);
        if (!this.game.isFinished() && nextPlayer.isAi() && (game.getIp() == null || !isFrontend) && !game.isEmpty()) {
            gridInfos = playWithAILevel1(nextGridInfos, nextPlayer.getGameValue());
            return fillGrid(gridInfos, true);
        }
        if (game.getIp() != null && isFrontend) {
            Map<String, Object> params = new HashMap<>();
            params.put("ip", game.getIp());
            network.play(params, gridInfos);
        }
        return nextGridInfos;
    }

    public Boolean quitGame(boolean isFrontend) {
        if (game.getIp() != null && isFrontend) {
            Map<String, Object> params = new HashMap<>();
            params.put("ip", game.getIp());
            network.quit(params, null);
        }
        this.game = null;
        return Boolean.TRUE;
    }

    public GridDTO playWithAILevel1(NextGridDTO nextGrid, BoxEnum playerValue) {
        Integer row = nextGrid.getNextRow();
        Integer column = nextGrid.getNextColumn();
        boolean played = false;
        Random random = new Random();
        GridDTO gridDto = null;
        // if we can play everywhere
        if (row == null && column == null) {
            for (int i = 0; i < HEIGHT; i++) {
                for (int j = 0; j < WIDTH; j++) {
                    if (!game.getGrid().getChildGrids()[i][j].isFull() && game.getGrid().getChildGrids()[i][j].getWinner() == BoxEnum.none) {
                        row = i;
                        column = j;
                    }
                }
            }
        }
        while (!played) {
            int childRow = random.nextInt(3);
            int childColumn = random.nextInt(3);
            if (game.getGrid().getChildGrids()[row][column].getBoxes()[childRow][childColumn] == BoxEnum.none) {
                gridDto = new GridDTO(row, column, childRow, childColumn, playerValue);
                played = true;
            }
        }

        try {
            Thread.sleep(1000);
        } catch (Exception exception) {

        }
        return gridDto;
    }

    private GridDTO findBestMove(int zoneRow, int zoneColumn, BoxEnum value) {
        int bestScore = Integer.MIN_VALUE;
        Integer bestChildRow = null;
        Integer bestChildColumn = null;
        BoxEnum[][] childGrid = this.game.getGrid().getChildGrids()[zoneRow][zoneColumn].getBoxes();

        for (int i = 0; i < childGrid.length; i++) {
            for (int j = 0; j < childGrid[i].length; j++) {
                if (childGrid[i][j] == BoxEnum.none) {
                    childGrid[i][j] = value;
                    int score = minimax(zoneRow, zoneColumn,0, value == BoxEnum.o_value ? BoxEnum.x_value :
                            BoxEnum.o_value, value);
                    childGrid[i][j] = value;

                    if (score > bestScore) {
                        bestScore = score;
                        bestChildRow = i;
                        bestChildColumn = j;
                    }
                }
            }
        }

        return GridDTO.builder().childRow(bestChildRow).childColumn(bestChildColumn).row(zoneRow).column(zoneColumn).value(value).build();
    }

    private int minimax(int zoneRow, int zoneColumn, int depth, BoxEnum value, BoxEnum currentPlayerValue) {
        // Check if the game is over or if a terminal state is reached.
        // You'll need to define your own logic for checking the game state
        // and evaluating it.

        // For example, if 'X' wins, return 10; if 'O' wins, return -10; if it's a tie, return 0.

        // If the game is over or the maximum depth is reached, return the evaluation score.
        if (this.game.getGrid().calculateWinner() != BoxEnum.none || depth >= 1) {
            return evaluate(value, zoneRow, zoneColumn, currentPlayerValue);
        }

        BoxEnum[][] childGrid = this.game.getGrid().getChildGrids()[zoneRow][zoneColumn].getBoxes();
        int bestScore;
        if (value == currentPlayerValue) {
            bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < childGrid.length; i++) {
                for (int j = 0; j < childGrid[i].length; j++) {
                    if (childGrid[i][j] == BoxEnum.none) {
                        childGrid[i][j] = value;
                        int score = minimax(i, j, depth + 1, value == BoxEnum.o_value ? BoxEnum.x_value :
                                BoxEnum.o_value, currentPlayerValue);
                        childGrid[i][j] = BoxEnum.none;
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
        } else {
            bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < childGrid.length; i++) {
                for (int j = 0; j < childGrid[i].length; j++) {
                    if (childGrid[i][j] == BoxEnum.none) {
                        childGrid[i][j] = value;
                        int score = minimax(i, j, depth + 1, value == BoxEnum.o_value ? BoxEnum.x_value :
                                BoxEnum.o_value, currentPlayerValue);
                        childGrid[i][j] = BoxEnum.none;
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
        }
        return bestScore;
    }

    private int evaluate(BoxEnum value, int zoneRow, int zoneColumn, BoxEnum currentPlayerValue) {
        BoxEnum[][] childGrid = this.game.getGrid().getChildGrids()[zoneRow][zoneColumn].getBoxes();
        for (int i = 0; i < childGrid.length; i++) {
            for (int j = 0; j < childGrid[i].length; j++) {
                if (childGrid[i][j] == BoxEnum.none) {
                    childGrid[i][j] = value;
                    BoxEnum winnerValue = this.game.getGrid().getChildGrids()[zoneRow][zoneColumn].getWinner();
                    boolean possibleNextWinner =
                            this.game.getGrid().getChildGrids()[zoneRow][zoneColumn].getPossibleWinner(value);
                    childGrid[i][j] = BoxEnum.none;
                    if (winnerValue != BoxEnum.none) {
                        return currentPlayerValue == value ? 20 : -10;
                    }
                    if (possibleNextWinner) {
                        return currentPlayerValue == value ? 10 : -5;
                    }
                }
            }
        }
        return 0;
    }
}
