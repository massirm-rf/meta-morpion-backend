package fr.uparis.morpion.metamorpionback.network.impl;

import fr.uparis.morpion.metamorpionback.model.GridDTO;
import fr.uparis.morpion.metamorpionback.model.Player;
import fr.uparis.morpion.metamorpionback.network.Network;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Profile(value = "e")
public class NetworkImplE implements Network {


    @Override
    public Object initGame(Map<String, Object> params, Object body) {

        RestTemplate template = new RestTemplate();

        String url = (String) params.get("ip");
        boolean starter = (boolean) params.get("starter");
        Player starterPlayer = (Player) body;

        url = String.format("%s?starter=%s&starterPlayer=%s", url, starter, starterPlayer);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setOrigin((String) params.get("ip"));


        HttpEntity<Player> request = new HttpEntity<>(starterPlayer, headers);

        return template.postForObject(url, request, Object.class);

    }

    @Override
    public Object play(Map<String, Object> params, Object body) {

        RestTemplate template = new RestTemplate();

        String url = (String) params.get("ip");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setOrigin(url);

        GridDTO gridDTO = (GridDTO) body;

        HttpEntity<GridDTO> request = new HttpEntity<>(gridDTO, headers);

        return template.postForObject(url, request, Object.class);



    }

}
