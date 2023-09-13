package fr.uparis.morpion.metamorpionback.network.impl;

import fr.uparis.morpion.metamorpionback.model.GridDTO;
import fr.uparis.morpion.metamorpionback.model.Player;
import fr.uparis.morpion.metamorpionback.network.Network;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class NetworkImplD implements Network {
    @Override
    public Object initGame(Map<String, Object> params, Object body) {
        String url = (String) params.get("ip");
        boolean starter = (boolean) params.getOrDefault("starter", true);
        boolean isVersusAi = (boolean) params.getOrDefault("AI", false);
        url = String.format("%s/api/start?isFirst=%s&isVersusAI=%s", url, starter, isVersusAi);
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request =
                new HttpEntity<>(null, headers);
        headers.setOrigin((String) params.get("ip"));
        Object response = template.postForObject(url, request, Object.class);
        return response;
    }

    @Override
    public Object play(Map<String, Object> params, Object body) {
        String url = (String) params.get("ip");
        GridDTO gridDto = (GridDTO) body;
        int bigX = gridDto.getColumn();
        int bigY = gridDto.getRow();
        int littleX = gridDto.getChildColumn();
        int littleY = gridDto.getChildRow();
//        url = String.format("%s?bigX=%s&bigY=%s&littleX=%s&littleY=%s&player=%s", url, bigX,bigY, littleX, littleY, getPlayerValue(gridDto.getValue()));
//        RestTemplate template = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<Object> request =
//                new HttpEntity<>(null, headers);
//        headers.setOrigin((String) params.get("ip"));
//        return template.postForObject(url, request, Object.class);
        return null;
    }
}
