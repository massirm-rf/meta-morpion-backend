package fr.uparis.morpion.metamorpionback.network.impl;

import fr.uparis.morpion.metamorpionback.model.GridDTO;
import fr.uparis.morpion.metamorpionback.model.Player;
import fr.uparis.morpion.metamorpionback.network.Network;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Service
@Profile({"e"})
public class NetworkImplE implements Network {

    @Value("${my-ip}")
    private String myIp;

    @Override
    public Object initGame(Map<String, Object> params, Object body) {

        RestTemplate template = new RestTemplate();

        String url = (String) params.get("ip");
        boolean starter = (boolean) params.get("starter");
        boolean vsAI = (boolean) params.get("vsAI");
        Player starterPlayer = (Player) body;

        url = String.format("%s/morpion/init?vsAI=%s&starter=%s&starterPlayer=%s&ip=%s", url, vsAI, starter, starterPlayer, myIp);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setOrigin(myIp);


        HttpEntity<Player> request = new HttpEntity<>(starterPlayer, headers);

        return template.postForObject(url, request, Object.class);

    }

    @Override
    public Object play(Map<String, Object> params, Object body) {

        RestTemplate template = new RestTemplate();

        String url = (String) params.get("ip");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setOrigin(myIp);

        GridDTO gridDTO = (GridDTO) body;

        HttpEntity<GridDTO> request = new HttpEntity<>(gridDTO, headers);

        return template.postForObject(url + "/morpion/play", request, Object.class);



    }

    @Override
    public Object quit(Map<String, Object> params, Object body) {
        RestTemplate template = new RestTemplate();
        String url = (String) params.get("ip");
        template.delete(url + "/morpion/quit", Collections.emptyMap());
        return null;
    }

}
