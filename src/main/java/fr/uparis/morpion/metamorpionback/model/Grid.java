package fr.uparis.morpion.metamorpionback.model;

import lombok.*;

import static fr.uparis.morpion.metamorpionback.utils.Constants.HEIGHT;
import static fr.uparis.morpion.metamorpionback.utils.Constants.WIDTH;


@Data
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Grid {

    private ChildGrid childGrids[][];
    private BoxEnum winnerValue;

    public Grid() {
        childGrids = new ChildGrid[WIDTH][HEIGHT];
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                childGrids[i][j] = new ChildGrid();
            }
        }
    }

    public BoxEnum calculateWinner() {
        BoxEnum childGridWinner;
        for (int i = 0; i < HEIGHT; i++) {
            childGridWinner = checkLine(i);
            if (childGridWinner != BoxEnum.none) {
                return childGridWinner;
            }
            childGridWinner = checkColumn(i);
            if (childGridWinner != BoxEnum.none) {
                return childGridWinner;
            }
        }
        return checkDiagonalWinner();
    }

    private BoxEnum checkLine(int lineNumber) {
        BoxEnum firstLineBox = childGrids[lineNumber][0].getWinner();
        for (int i = 1; i < WIDTH; i++) {
            if (!childGrids[lineNumber][i].getWinner().equals(firstLineBox)) return BoxEnum.none;
        }
        return firstLineBox;
    }

    private BoxEnum checkColumn(int lineNumber) {
        BoxEnum firstColumnBox = childGrids[0][lineNumber].getWinner();
        for (int i = 1; i < HEIGHT; i++) {
            if (!childGrids[lineNumber][i].getWinner().equals(firstColumnBox)) return BoxEnum.none;
        }
        return firstColumnBox;
    }

    private BoxEnum checkDiagonalWinner() {
        BoxEnum centralBox = childGrids[1][1].getWinner();
        if ((childGrids[0][0].getWinner() == centralBox && centralBox == childGrids[2][2].getWinner()) || (childGrids[0][2].getWinner() == centralBox && centralBox == childGrids[2][0].getWinner())) {
            return centralBox;
        }
        return BoxEnum.none;
    }

    public boolean isCompleted() {

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (childGrids[i][j].getWinner() == BoxEnum.none) {
                    return false;
                }
            }
        }

        return true;
    }
}
