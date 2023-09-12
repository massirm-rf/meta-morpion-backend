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
    private boolean isCompleted;

    public ChildGrid() {
        boxes = new BoxEnum[WIDTH][HEIGHT];
        initBoxes();
        completer = BoxEnum.NONE;
    }

    private void initBoxes() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                boxes[i][j] = BoxEnum.NONE;
            }
        }
    }

    public BoxEnum getWinner() {
        BoxEnum childGridWinner;
        for (int i = 0; i < HEIGHT; i++) {
            childGridWinner = checkLine(i);
            if (childGridWinner != BoxEnum.NONE) {
                return childGridWinner;
            }
            childGridWinner = checkColumn(i);
            if (checkColumn(i) != BoxEnum.NONE) {
                return checkColumn(i);
            }
        }
        return checkDiagonalWinner();
    }

    private BoxEnum checkLine(int lineNumber) {
        BoxEnum firstLineBox = boxes[lineNumber][0];
        for (int i = 1; i < WIDTH; i++) {
            if (!boxes[lineNumber][i].equals(firstLineBox)) return BoxEnum.NONE;
        }
        return firstLineBox;
    }

    private BoxEnum checkColumn(int lineNumber) {
        BoxEnum firstColumnBox = boxes[0][lineNumber];
        for (int i = 1; i < HEIGHT; i++) {
            if (!boxes[lineNumber][i].equals(firstColumnBox)) return BoxEnum.NONE;
        }
        return firstColumnBox;
    }

    private BoxEnum checkDiagonalWinner() {
        BoxEnum centralBox = boxes[1][1];
        if ((boxes[0][0] == centralBox && centralBox == boxes[2][2]) || (boxes[0][2] == centralBox && centralBox == boxes[2][0])) {
            return centralBox;
        }
        return BoxEnum.NONE;
    }
}
