package fr.uparis.morpion.metamorpionback.controller;

import fr.uparis.morpion.metamorpionback.model.Game;
import fr.uparis.morpion.metamorpionback.model.Grid;
import fr.uparis.morpion.metamorpionback.model.GridDTO;
import fr.uparis.morpion.metamorpionback.model.Player;
import fr.uparis.morpion.metamorpionback.services.GameService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/morpion")
public class MorpionController {

    private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(MorpionController.class);
    private final SimpMessagingTemplate template;
    private GameService gameService;

    /**
     * initialize the grid of the game
     */
    @Operation(summary = "Création de partie", description = "Créer une nouvelle partie")
    @PostMapping(value = "/init")
    public void initGame(@RequestParam boolean starter, @RequestBody Player starterPlayer) {
        LOGGER.info("init game...");
        template.convertAndSend("/init-game", gameService.initGame(starterPlayer, starter));
    }

    /**
     * @param bodyInput
     * @return the grid filled with the player's input in the body
     */
    @Operation(summary = "Jouer un coup", description = "Jouer un coup dans la partie en envoyant les coordonées de la case")
    @PostMapping(value = "/play")
    public void play(@RequestBody GridDTO bodyInput) {
        //TODO
        template.convertAndSend("/play", gameService.fillGrid(bodyInput));
    }

    /**
     * @return the player who has to play (X or O)
     */
    @Operation(summary = "Dernier joueur", description = "Récupérer le dernier joueur qui a joué")
    @GetMapping(value = "/player-round")
    public String getPlayerRound() {
        //TODO
        return "x";
    }

    /**
     * @return the actual grid of the game
     */
    @Operation(summary = "Etat de la grille", description = "Récupérer l'état actuel de la grille")
    @GetMapping(value = "/actual-grid")
    public void getActualGrid() {
        template.convertAndSend(gameService.getGame().getGrid());
    }


    /**
     * @return the next grid of the game
     */
    @Operation(summary = "Prochaine zone", description = "Récupérer la prochaine zone du jeu où il faut jouer")
    @GetMapping("/next-grid")
    public Grid getNextGrid() {
        //TODO
        return null;
    }


}
