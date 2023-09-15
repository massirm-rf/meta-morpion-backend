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

    private GridDTO playWithAILevel2(NextGridDTO nextGrid, BoxEnum playerValue) {
        GridDTO gridDTO = this.findBestMove(nextGrid.getNextRow(), nextGrid.getNextColumn(), playerValue);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return gridDTO;
    }


    private GridDTO findBestMove(Integer zoneRow, Integer zoneColumn, BoxEnum value) {
        int bestScore = Integer.MIN_VALUE;
        Integer bestChildRow = null;
        Integer bestChildColumn = null;

        if (zoneRow == null || zoneColumn == null) {
            ChildGrid[][] childGrids = this.game.getGrid().getChildGrids();
            for (int i = 0; i < childGrids.length; i++) {
                for (int j = 0; j < childGrids[i].length; j++) {
                    if (childGrids[i][j].getWinner() == BoxEnum.none && !childGrids[i][j].isFull()) {
                        int score = this.evaluate(value, i, j, value).getScore();
                        if (score > bestScore) {
                            bestScore = score;
                            zoneRow = i;
                            zoneColumn = j;
                        }
                    }
                }
            }
        }
        bestScore = Integer.MIN_VALUE;
        if (zoneRow != null && zoneColumn != null) {
            BoxEnum[][] childGrid = this.game.getGrid().getChildGrids()[zoneRow][zoneColumn].getBoxes();
            ScoreEval scoreEval = this.evaluate(value, zoneRow, zoneColumn, value);
            bestScore = scoreEval.getScore();
            bestChildRow = scoreEval.getChildRow();
            bestChildColumn = scoreEval.getChildColumn();
            for (int i = 0; i < childGrid.length; i++) {
                for (int j = 0; j < childGrid[i].length; j++) {
                    if (childGrid[i][j] == BoxEnum.none) {
                        childGrid[i][j] = value;
                        int score = minimax(i, j, 0, value == BoxEnum.x_value ? BoxEnum.o_value : BoxEnum.x_value, value);
                        childGrid[i][j] = BoxEnum.none;
                        if (score > bestScore) {
                            bestScore = score;
                            bestChildRow = i;
                            bestChildColumn = j;
                        }
                    }
                }
            }
        }
        return GridDTO.builder().childRow(bestChildRow).childColumn(bestChildColumn).row(zoneRow).column(zoneColumn).value(value).build();
    }


    public GridDTO playWithAILevel3(NextGridDTO nextGrid, BoxEnum playerValue) {
        GridDTO gridDto = null;
        Integer row = nextGrid.getNextRow();
        Integer column = nextGrid.getNextColumn();
        BoxEnum opponent = BoxEnum.x_value == playerValue ? BoxEnum.o_value : BoxEnum.x_value;

        if (row == null || column == null) {
            gridDto = getChildGridTowin(playerValue);
            if (gridDto != null)
                return gridDto;
            gridDto = getChildGridTowin(opponent);
            if (gridDto != null)
                return new GridDTO(gridDto.getRow(), gridDto.getColumn(), gridDto.getChildRow(), gridDto.getChildColumn(), playerValue);

            gridDto = getPossibleChildGridTowin(playerValue, false);
            if (gridDto != null)
                return gridDto;
            gridDto = getPossibleChildGridTowin(opponent, true);
            if (gridDto != null)
                return new GridDTO(gridDto.getRow(), gridDto.getColumn(), gridDto.getChildRow(), gridDto.getChildColumn(), playerValue);
        }

        // si mon coup est gagnant
        gridDto = getCoordinatesToWin(playerValue, row, column);
        if (gridDto != null) return gridDto;

        //si l'adversaire peut gagner dans cette grille
        gridDto = getCoordinatesToWin(opponent, row, column);
        if (gridDto != null)
            return new GridDTO(gridDto.getRow(), gridDto.getColumn(), gridDto.getChildRow(), gridDto.getChildColumn(), playerValue);


        //coup merdique
        gridDto = playForCleanChildGrid(row, column);
        if (gridDto != null) {
            gridDto = new GridDTO(row, column, gridDto.getChildRow(), gridDto.getChildColumn(), playerValue);
            return gridDto;
        }

        // jouer avec la deuxième valeur
        gridDto = getCoordinatesToPossibleWin(playerValue, row, column, false);
        if (gridDto != null)
            return gridDto;

        gridDto = getCoordinatesToPossibleWin(opponent, row, column, true);
        if (gridDto != null)
            return new GridDTO(gridDto.getRow(), gridDto.getColumn(), gridDto.getChildRow(), gridDto.getChildColumn(), playerValue);

        return playWithAILevel1(nextGrid, playerValue);
    }

    private int minimax(int zoneRow, int zoneColumn, int depth, BoxEnum value, BoxEnum currentPlayerValue) {
        if (this.game.getGrid().calculateWinner() != BoxEnum.none || depth >= 1) {
            return evaluate(value, zoneRow, zoneColumn, currentPlayerValue).getScore();
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
                        int myScore = this.evaluate(value, zoneRow, zoneColumn, currentPlayerValue).getScore();
                        childGrid[i][j] = BoxEnum.none;
                        bestScore = Math.max(score, bestScore);
                        bestScore = Math.max(myScore, bestScore);
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
                        int myScore = this.evaluate(value, zoneRow, zoneColumn, currentPlayerValue).getScore();
                        childGrid[i][j] = BoxEnum.none;
                        bestScore = Math.min(score, bestScore);
                        bestScore = Math.min(myScore, bestScore);
                    }
                }
            }
        }
        return bestScore;
    }

    private ScoreEval evaluate(BoxEnum value, int zoneRow, int zoneColumn, BoxEnum currentPlayerValue) {
        BoxEnum[][] childGrid = this.game.getGrid().getChildGrids()[zoneRow][zoneColumn].getBoxes();
        Integer childRow = null;
        Integer childColumn = null;
        ScoreEval bestScore = null;
        for (int i = 0; i < childGrid.length; i++) {
            for (int j = 0; j < childGrid[i].length; j++) {
                if (childGrid[i][j] == BoxEnum.none) {
                    childGrid[i][j] = value;
                    BoxEnum winnerValue = this.game.getGrid().getChildGrids()[zoneRow][zoneColumn].getWinner();
                    boolean possibleNextWinner =
                            this.game.getGrid().getChildGrids()[zoneRow][zoneColumn].getPossibleWinner(value);
                    childGrid[i][j] = BoxEnum.none;
                    childRow = i;
                    childColumn = j;
                    if (winnerValue != BoxEnum.none) {
                        return ScoreEval.builder().score(currentPlayerValue == value ? 20 : -10).childRow(i).childColumn(j).build();
                    }
                    if (possibleNextWinner) {
                        bestScore = ScoreEval.builder().score(currentPlayerValue == value ? 10 : -5).childRow(i).childColumn(j).build();
                    }
                }
            }
        }
        return bestScore != null ? bestScore : ScoreEval.builder().score(0).childRow(childRow).childColumn(childColumn).build();
    }

    private GridDTO getCoordinatesToWin(BoxEnum possibleWinnerValue, int zoneRow, int zoneColumn) {
        BoxEnum[][] childGrid = this.game.getGrid().getChildGrids()[zoneRow][zoneColumn].getBoxes();
        for (int i = 0; i < childGrid.length; i++) {
            for (int j = 0; j < childGrid[i].length; j++) {
                if (childGrid[i][j] == BoxEnum.none) {
                    childGrid[i][j] = possibleWinnerValue;
                    BoxEnum winnerValue = this.game.getGrid().getChildGrids()[zoneRow][zoneColumn].getWinner();
                    childGrid[i][j] = BoxEnum.none;
                    if (winnerValue == possibleWinnerValue) {
                        return new GridDTO(zoneRow, zoneColumn, i, j, possibleWinnerValue);
                    }
                }
            }
        }
        return null;
    }

    private GridDTO getCoordinatesToPossibleWin(BoxEnum possibleWinnerValue, int zoneRow, int zoneColumn, boolean allowRandomRound) {
        BoxEnum[][] childGrid = this.game.getGrid().getChildGrids()[zoneRow][zoneColumn].getBoxes();
        for (int i = 0; i < childGrid.length; i++) {
            for (int j = 0; j < childGrid[i].length; j++) {
                if (childGrid[i][j] == BoxEnum.none) {
                    childGrid[i][j] = possibleWinnerValue;
                    boolean possibleNextWinner =
                            this.game.getGrid().getChildGrids()[zoneRow][zoneColumn].getPossibleWinner(possibleWinnerValue);
                    childGrid[i][j] = BoxEnum.none;
                    if (possibleNextWinner) {
                        if (allowRandomRound) {
                            return new GridDTO(zoneRow, zoneColumn, i, j, possibleWinnerValue);
                        }
                        if (game.getGrid().getChildGrids()[i][j].isFull() || game.getGrid().getChildGrids()[i][j].getWinner() != BoxEnum.none) {
                            return new GridDTO(zoneRow, zoneColumn, i, j, possibleWinnerValue);
                        }
                    }
                }
            }
        }
        return null;
    }


    private GridDTO getPossibleChildGridTowin(BoxEnum playerValue, boolean allowRandom) {
        ChildGrid[][] childGrids = this.game.getGrid().getChildGrids();
        for (int i = 0; i < childGrids.length; i++) {
            for (int j = 0; j < childGrids[i].length; j++) {
                if (childGrids[i][j].getWinner() == BoxEnum.none && !childGrids[i][j].isFull()) {
                    GridDTO gridDTO = getCoordinatesToPossibleWin(playerValue, i, j, allowRandom);
                    if (gridDTO != null)
                        return gridDTO;
                }
            }
        }
        return null;
    }

    private GridDTO getChildGridTowin(BoxEnum playerValue) {
        ChildGrid[][] childGrids = this.game.getGrid().getChildGrids();
        for (int i = 0; i < childGrids.length; i++) {
            for (int j = 0; j < childGrids[i].length; j++) {
                if (childGrids[i][j].getWinner() == BoxEnum.none && !childGrids[i][j].isFull()) {
                    GridDTO gridDTO = getCoordinatesToWin(playerValue, i, j);
                    if (gridDTO != null)
                        return gridDTO;
                }
            }
        }
        return null;
    }

    private GridDTO playForCleanChildGrid(int zoneRow, int zoneColumn) {
        ChildGrid childGrid = game.getGrid().getChildGrids()[zoneRow][zoneColumn];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (childGrid.getBoxes()[i][j] == BoxEnum.none && game.getGrid().getChildGrids()[i][j].isEmpty())
                    return new GridDTO(i, j, i, j, null);
            }
        }
        return null;
    }


}
