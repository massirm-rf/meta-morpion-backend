package fr.uparis.morpion.metamorpionback.network;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface Network {


    Object initGame(Map<String, Object> params, Object body);

    Object play(Map<String, Object> params, Object body);



}
