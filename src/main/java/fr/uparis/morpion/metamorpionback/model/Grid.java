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
    private boolean completed;

    public Grid() {
        childGrids = new ChildGrid[WIDTH][HEIGHT];
    }

}
