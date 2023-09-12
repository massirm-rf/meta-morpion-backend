package fr.uparis.morpion.metamorpionback.controller;

import fr.uparis.morpion.metamorpionback.model.*;
import fr.uparis.morpion.metamorpionback.services.GameService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/morpion")
public class MorpionController {

    private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(MorpionController.class);

    private GameService gameService;


    /**
     * initialize the grid of the game
     *
     */
    @PostMapping(value = "/init")
     public Game initGame(@RequestParam boolean starter, @RequestBody Player starterPlayer) {
        LOGGER.info("init game...");
        return gameService.initGame(starterPlayer, starter);
     }

     @PostMapping(value = "/join")
     public Game joinGame(@RequestBody Player starterPlayer) {
        LOGGER.info("join game...");
        return gameService.joinGame(starterPlayer);
     }

    /**
     * @param bodyInput
     * @return the grid filled with the player's input in the body
     */
    @PostMapping(value = "/fill")
    public NextGridDTO fillGrid(@RequestBody GridDTO bodyInput) {
        //TODO
        return gameService.fillGrid(bodyInput);
    }

    /**
     * @return the player who has to play (X or O)
     */
    @GetMapping(value = "/player-round")
    public String getPlayerRound() {
        //TODO
        return "x";
    }

    /**
     * @return the actual grid of the game
     */
    @GetMapping(value = "/actual-grid")
    public Grid getActualGrid() {
        //TODO
        return null;
    }


    /**
     * @return the next grid of the game
     */
    @GetMapping("/next-grid")
    public Grid getNextGrid() {
        //TODO
        return null;
    }




}
