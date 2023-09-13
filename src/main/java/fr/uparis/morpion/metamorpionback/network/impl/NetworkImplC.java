package fr.uparis.morpion.metamorpionback.network.impl;

import fr.uparis.morpion.metamorpionback.network.Network;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
@Profile({"c"})
public class NetworkImplC implements Network {
    @Value("room-code")
    private String roomCode;
    @Value("player-uuid")
    private String playerUUIO;

    @Override
    public Object initGame(Map<String, Object> params, Object body) {
        RestTemplate template = new RestTemplate();
        String url = (String) params.get("ip");
        String gameType = "PVP_ONLINE";
        String uuid = UUID.randomUUID().toString();
        boolean firstToPlay = (Boolean) params.get("starter");
        url = String.format("%s/create?gameType=%s&playerUUID%s&firstToPlay=%s", url, gameType, uuid, firstToPlay);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setOrigin((String) params.get("ip"));
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        Object result = template.postForObject(url, request, Object.class);
        return result;
    }

    @Override
    public Object play(Map<String, Object> params, Object body) {
        RestTemplate template = new RestTemplate();
        String url = (String) params.get("ip");
        int absoluteI = (Integer) params.get("i") + (Integer) params.get("parentI") * 3;
        int absoluteJ = (Integer) params.get("j") + (Integer) params.get("parentJ") * 3;
        url = String.format("%s/send-move?playerUUID%s&roomCode=%s&i=%s&j=%s", url, this.playerUUIO, this.roomCode,
                absoluteI, absoluteJ)   ;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setOrigin((String) params.get("ip"));
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        return template.postForObject(url, request, Object.class);
    }
}
