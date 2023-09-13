package fr.uparis.morpion.metamorpionback.network.impl;

import fr.uparis.morpion.metamorpionback.model.BoxEnum;
import fr.uparis.morpion.metamorpionback.model.GridDTO;
import fr.uparis.morpion.metamorpionback.model.Player;
import fr.uparis.morpion.metamorpionback.network.Network;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Profile({"a"})
public class NetworkImpA implements Network {
    @Override
    public Object initGame(Map<String, Object> params, Object body) {
        String url = (String) params.get("ip");
        boolean starter = (boolean) params.getOrDefault("starter", true);
        Map<String, String> requestBody = new HashMap<>();
        Player player = (Player) body;
        requestBody.put("player", getPlayerValue(player.getGameValue()));
        url = String.format("%s?starts=%s", url, starter);

        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request =
                new HttpEntity<>(requestBody, headers);
        headers.setOrigin((String) params.get("ip"));
        return template.postForObject(url, request, Object.class);
    }

    @Override
    public Object play(Map<String, Object> params, Object body) {
        String url = (String) params.get("ip");
        GridDTO gridDto = (GridDTO) body;
        url = String.format("%s?positionX=%s&positionY=%s&positionx=%s&positiony=%s&player=%s", url, gridDto.getRow(), gridDto.getColumn(), gridDto.getChildRow(), gridDto.getChildColumn(), getPlayerValue(gridDto.getValue()));
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request =
                new HttpEntity<>(null, headers);
        headers.setOrigin((String) params.get("ip"));
        return template.postForObject(url, request, Object.class);
    }


    private String getPlayerValue(BoxEnum playerValue) {
        switch (playerValue) {
            case x_value -> {
                return "X";
            }
            case o_value -> {
                return "O";
            }
            default -> {
                return "NONE";
            }
        }
    }
}
