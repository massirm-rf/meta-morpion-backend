package fr.uparis.morpion.metamorpionback.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ScoreEval {
    private int score;
    private Integer childRow;
    private Integer childColumn;
}
