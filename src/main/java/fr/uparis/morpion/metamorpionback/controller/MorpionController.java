package fr.uparis.morpion.metamorpionback.controller;

import fr.uparis.morpion.metamorpionback.model.*;
import fr.uparis.morpion.metamorpionback.services.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @ApiResponse(responseCode = "201", description = "Partie créée avec succès", content = @Content(schema = @Schema(implementation = Game.class)))
    @ApiResponse(responseCode = "400", description = "Requête invalide",content =@Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur",content =@Content(mediaType = "text/plain"))
    public Game initGame(@Parameter(description = "Indiquer si le joueur veut jouer en premier", required = true, example = "true") @RequestParam boolean starter, @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Données du joueur démarreur de partie",
            required = true,
            content = @Content(schema = @Schema(implementation = Player.class)))
    @RequestBody Player starterPlayer) {
        LOGGER.info("init game...");
        template.convertAndSend("/init-game", gameService.initGame(starterPlayer, starter));
        return null;
    }

    /**
     * @param bodyInput
     * @return the grid filled with the player's input in the body
     */
    @Operation(summary = "Jouer un coup", description = "Jouer un coup dans la partie en envoyant les coordonnées de la case")
    @PostMapping(value = "/play")
    @ApiResponse(responseCode = "200", description = "Coup joué avec succès")
    @ApiResponse(responseCode = "400", description = "Requête invalide",content =@Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur",content =@Content(mediaType = "text/plain"))
    public NextGridDTO play(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Données du coup à jouer",
            required = true,
            content = @Content(schema = @Schema(implementation = GridDTO.class)))
                     @RequestBody GridDTO bodyInput) {
        template.convertAndSend("/play", gameService.fillGrid(bodyInput));
        return null;
    }

    /**
     * @return the player who has to play (X or O)
     */
    @Operation(summary = "Dernier joueur", description = "Récupérer le dernier joueur qui a joué")
    @ApiResponse(responseCode = "200", description = "Récupération réussie du dernier joueur")
    @ApiResponse(responseCode = "400", description = "Requête invalide",content =@Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur",content =@Content(mediaType = "text/plain"))
    @GetMapping(value = "/player-round")
    public String getPlayerRound() {
        //TODO
        return "x";
    }

    /**
     * @return the actual grid of the game
     */
    @Operation(summary = "Etat de la grille", description = "Récupérer l'état actuel de la grille")
    @ApiResponse(responseCode = "200", description = "Récupération réussie de l'état actuel de la grille")
    @ApiResponse(responseCode = "400", description = "Requête invalide",content =@Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur",content =@Content(mediaType = "text/plain"))
    @GetMapping(value = "/actual-grid")
    public Grid getActualGrid() {
        template.convertAndSend(gameService.getGame().getGrid());
        return null;
    }


    /**
     * @return the next grid of the game
     */
    @Operation(summary = "Prochaine zone", description = "Récupérer la prochaine zone du jeu où il faut jouer")
    @GetMapping("/next-grid")
    @ApiResponse(responseCode = "200", description = "Récupération réussie de la prochaine zone")
    @ApiResponse(responseCode = "400", description = "Requête invalide",content =@Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur",content =@Content(mediaType = "text/plain"))
    public Grid getNextGrid() {
        //TODO
        return null;
    }



}
