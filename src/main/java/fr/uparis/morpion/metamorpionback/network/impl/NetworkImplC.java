package fr.uparis.morpion.metamorpionback.network.impl;

import fr.uparis.morpion.metamorpionback.model.GameC;
import fr.uparis.morpion.metamorpionback.model.GridDTO;
import fr.uparis.morpion.metamorpionback.network.Network;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Profile({"c"})
public class NetworkImplC implements Network {
    @Value("${room-code}")
    private String roomCode;
    @Value("${player-uuid}")
    private String playerUUIO;
    @Value("${my-ip}")
    private String myIp;

    @Override
    public Object initGame(Map<String, Object> params, Object body) {
        RestTemplate template = new RestTemplate();
        String url = (String) params.get("ip");
        String gameType = "PVP_ONLINE";
        String uuid = UUID.randomUUID().toString();
        this.playerUUIO = uuid;
        boolean firstToPlay = (Boolean) params.get("starter");
        url = String.format("%s/game/create?gameType=%s&playerUUID=%s&firstToPlay=%s", url, gameType, uuid, firstToPlay);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setOrigin(myIp);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        GameC result = template.postForObject(url, request, GameC.class);

        this.roomCode = result.getRoomCode();

        return result;
    }

    @Override
    public Object play(Map<String, Object> params, Object body) {
        RestTemplate template = new RestTemplate();
        String url = (String) params.get("ip");
        GridDTO gridDTO = (GridDTO) body;
        int absoluteI = gridDTO.getChildRow() + gridDTO.getRow() * 3;   //(Integer) params.get("i") + (Integer) params.get("parentI") * 3;
        int absoluteJ = gridDTO.getChildColumn() + gridDTO.getColumn() * 3;//(Integer) params.get("j") + (Integer) params.get("parentJ") * 3;
        url = String.format("%s/game/send-move?playerUUID=%s&roomCode=%s&i=%s&j=%s", url, this.playerUUIO, this.roomCode,
                absoluteI, absoluteJ)   ;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setOrigin((String) params.get("ip"));
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        return template.postForObject(url, request, Object.class);
    }


    @Override
    public Object quit(Map<String, Object> params, Object body) {
        return null;
    }



    private Map<String, Object> convertUsingReflection(Object object) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field: fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(object));
        }

        return map;
    }


}
