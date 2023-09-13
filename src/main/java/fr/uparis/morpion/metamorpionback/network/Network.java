package fr.uparis.morpion.metamorpionback.network;

import java.util.Map;

public interface Network {
    Object initGame(Map<String, Object> params, Object body);

    Object play(Map<String, Object> params, Object body);
}
