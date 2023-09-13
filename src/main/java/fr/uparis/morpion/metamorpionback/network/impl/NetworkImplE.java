package fr.uparis.morpion.metamorpionback.network.impl;

import fr.uparis.morpion.metamorpionback.network.Network;
import org.springframework.context.annotation.Profile;

import java.util.Map;

@Profile(value = "E")
public class NetworkImplE implements Network {


    @Override
    public Object initGame(Map<String, Object> params, Object body) {
        return null;
    }

    @Override
    public Object play(Object body) {
        return null;
    }

}
