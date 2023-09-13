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
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;

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
    @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(mediaType = "text/plain"))
    public ResponseEntity<Game> initGame(@Parameter(description = "Indiquer si le joueur veut jouer en premier", required = true, example = "true") @RequestParam boolean starter, @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Données du joueur démarreur de partie",
            required = true,
            content = @Content(schema = @Schema(implementation = Player.class)))
    @RequestBody Player starterPlayer) {
        LOGGER.info("init game...");
        Game firstGridGame = gameService.initGame(starterPlayer, starter);
        template.convertAndSend("/init-game", firstGridGame);
        return ResponseEntity.ok(firstGridGame);
    }

    /**
     * @param bodyInput
     * @return the grid filled with the player's input in the body
     */
    @Operation(summary = "Jouer un coup", description = "Jouer un coup dans la partie en envoyant les coordonnées de la case")
    @PostMapping(value = "/play")
    @ApiResponse(responseCode = "200", description = "Coup joué avec succès")
    @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(mediaType = "text/plain"))
    public ResponseEntity<NextGridDTO> play(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = """
                    Données du coup à jouer où :
                    \nrow : Le numéro de ligne de la grande grille (0 à 8 du haut vers le bas)
                    \ncolumn : Le numéro de la colonne de la grande grille (0 à 8 de gauche à droite)
                    \nchildRow : Le numéro de ligne de la petite grille (0 à 8 du haut vers le bas)
                    \nchildColumn : Le numéro de la colonne de la petite grille (0 à 8 de gauche à droite)
                    \nvalue : La valeur du coup à jouer (x_value, o_value ou none)""",
            required = true,
            content = @Content(schema = @Schema(implementation = GridDTO.class)))
                                            @RequestBody GridDTO bodyInput) {
        NextGridDTO nextGrid = gameService.fillGrid(bodyInput);
        template.convertAndSend("/play", nextGrid);
        return ResponseEntity.ok(nextGrid);
    }

    @Operation(summary = "Quitter la partie", description = "Quitter la partie en cours")
    @DeleteMapping(value = "/quit")
    @ApiResponse(responseCode = "201", description = "La partie est terminée ", content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(mediaType = "text/plain"))
    public ResponseEntity<Boolean> quit() {
        Boolean quitGameValue = gameService.quitGame();
        template.convertAndSend("/quit", quitGameValue);
        return ResponseEntity.ok(quitGameValue);
    }


}
