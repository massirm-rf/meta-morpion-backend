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

    public Grid() {
        childGrids = new ChildGrid[WIDTH][HEIGHT];
    }


    public boolean isCompleted() {

        for (int i = 0; i<HEIGHT; i++) {
            for (int j=0; j<WIDTH; j++) {
                if (childGrids[i][j].getWinner() == BoxEnum.NONE) {
                    return false;
                }
            }
        }

        return true;
    }
}
