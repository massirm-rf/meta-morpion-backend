package fr.uparis.morpion.metamorpionback.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Player {
    private String playerName;
    private BoxEnum gameValue;
    private boolean ai;
}
