package fr.uparis.morpion.metamorpionback.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Player {
    private final String playerName;
    private final BoxEnum gameValue;
}
