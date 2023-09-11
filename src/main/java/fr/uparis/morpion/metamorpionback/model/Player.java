package fr.uparis.morpion.metamorpionback.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor

public class Player {
    private final String playerName;
    private final BoxEnum gameValue;
}
