package fr.uparis.morpion.metamorpionback.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import static fr.uparis.morpion.metamorpionback.utils.Constants.HEIGHT;
import static fr.uparis.morpion.metamorpionback.utils.Constants.WIDTH;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ChildGrid {
    private BoxEnum[][] boxes;
    private BoxEnum completer;

    public ChildGrid() {
        boxes = new BoxEnum[WIDTH][HEIGHT];
        initBoxes();
        completer = BoxEnum.none;
    }

    public void setBox(int row, int column, BoxEnum box) {
        boxes[row][column] = box;
    }

    private void initBoxes() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                boxes[i][j] = BoxEnum.none;
            }
        }
    }

    public boolean isFull() {
        for (BoxEnum[] boxLine : boxes) {
            for (BoxEnum box : boxLine) {
                if (box == BoxEnum.none) {
                    return false;
                }
            }
        }
        return true;
    }

    public BoxEnum getWinner() {
        BoxEnum childGridWinner;
        for (int i = 0; i < HEIGHT; i++) {
            childGridWinner = checkLine(i);
            if (childGridWinner != BoxEnum.none) {
                return childGridWinner;
            }
            childGridWinner = checkColumn(i);
            if (checkColumn(i) != BoxEnum.none) {
                return checkColumn(i);
            }
        }
        return checkDiagonalWinner();
    }

    private BoxEnum checkLine(int lineNumber) {
        BoxEnum firstLineBox = boxes[lineNumber][0];
        for (int i = 1; i < WIDTH; i++) {
            if (!boxes[lineNumber][i].equals(firstLineBox)) return BoxEnum.none;
        }
        return firstLineBox;
    }

    private BoxEnum checkColumn(int lineNumber) {
        BoxEnum firstColumnBox = boxes[0][lineNumber];
        for (int i = 1; i < HEIGHT; i++) {
            if (!boxes[lineNumber][i].equals(firstColumnBox)) return BoxEnum.none;
        }
        return firstColumnBox;
    }

    private BoxEnum checkDiagonalWinner() {
        BoxEnum centralBox = boxes[1][1];
        if ((boxes[0][0] == centralBox && centralBox == boxes[2][2]) || (boxes[0][2] == centralBox && centralBox == boxes[2][0])) {
            return centralBox;
        }
        return BoxEnum.none;
    }
}
