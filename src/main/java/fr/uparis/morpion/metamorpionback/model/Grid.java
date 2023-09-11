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

    private final childGrid childGrids[][];
    private boolean completed;

    public Grid() {
        childGrids = new childGrid[WIDTH][HEIGHT];
    }

    @Data
    @AllArgsConstructor
    @Getter
    @Setter
    class childGrid {
        private final BoxEnum[][] boxes;
        private BoxEnum completer;
        private boolean isCompleted;

        public childGrid() {
            boxes = new BoxEnum[WIDTH][HEIGHT];
            completer = BoxEnum.NONE;
        }
    }

}
