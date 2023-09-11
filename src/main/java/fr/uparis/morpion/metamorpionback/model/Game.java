package fr.uparis.morpion.metamorpionback.model;

import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Game {
    private Player player1;
    private Player player2;
    private boolean isFinished;
    private final Grid grid;

}
