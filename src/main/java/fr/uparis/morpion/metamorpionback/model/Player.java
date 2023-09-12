package fr.uparis.morpion.metamorpionback.model;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class Player {
    private final String playerName;
    private final BoxEnum gameValue;
}
