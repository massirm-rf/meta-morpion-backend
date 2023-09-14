package fr.uparis.morpion.metamorpionback.controller;

import fr.uparis.morpion.metamorpionback.model.*;
import fr.uparis.morpion.metamorpionback.services.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<Game> initGame(@RequestParam(required = false, defaultValue = "false") boolean vsAI, HttpServletRequest httpServletRequest, @Parameter(description = "Adresse ip du joueeur suivi du numero de port (ip:port)", example = "122.122.001.002:4242") @RequestParam(required = false) String ip, @Parameter(description = "Indiquer si le joueur veut jouer en premier", required = true, example = "true") @RequestParam boolean starter, @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Données du joueur démarreur de partie",
            required = true,
            content = @Content(schema = @Schema(implementation = Player.class)))
    @RequestBody Player starterPlayer) {
        LOGGER.info("init game...");

        boolean isFrontend = httpServletRequest.getHeader("host") != null && httpServletRequest.getHeader("host").startsWith("localhost");

        Game firstGridGame = gameService.initGame(vsAI, ip,starterPlayer, starter, isFrontend);
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
    public ResponseEntity<NextGridDTO> play(HttpServletRequest httpServletRequest, @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = """
                    Données du coup à jouer où :
                    \nrow : Le numéro de ligne de la grande grille (0 à 2 du haut vers le bas)
                    \ncolumn : Le numéro de la colonne de la grande grille (0 à 2 de gauche à droite)
                    \nchildRow : Le numéro de ligne de la petite grille (0 à 2 du haut vers le bas)
                    \nchildColumn : Le numéro de la colonne de la petite grille (0 à 2 de gauche à droite)
                    \nvalue : La valeur du coup à jouer (x_value, o_value ou none)""",
            required = true,
            content = @Content(schema = @Schema(implementation = GridDTO.class)))
                                            @RequestBody GridDTO bodyInput) {

        boolean isFrontend = httpServletRequest.getHeader("host") != null && httpServletRequest.getHeader("host").startsWith("localhost");

        NextGridDTO nextGrid = gameService.fillGrid(bodyInput, isFrontend);
        return ResponseEntity.ok(nextGrid);
    }

    @Operation(summary = "Quitter la partie", description = "Quitter la partie en cours")
    @DeleteMapping(value = "/quit")
    @ApiResponse(responseCode = "201", description = "La partie est terminée ", content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content(mediaType = "text/plain"))
    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content(mediaType = "text/plain"))
    public ResponseEntity<Boolean> quit(HttpServletRequest httpServletRequest) {
        boolean isFrontend = httpServletRequest.getHeader("host") != null && httpServletRequest.getHeader("host").startsWith("localhost");
        Boolean quitGameValue = gameService.quitGame(isFrontend);
        template.convertAndSend("/quit", quitGameValue);
        return ResponseEntity.ok(quitGameValue);
    }


}
