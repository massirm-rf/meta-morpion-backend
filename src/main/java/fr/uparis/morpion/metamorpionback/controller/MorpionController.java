package fr.uparis.morpion.metamorpionback.controller;

import fr.uparis.morpion.metamorpionback.model.Grid;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/morpion")
public class MorpionController {

    private static final Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger(MorpionController.class);

    public MorpionController() {

    }

    /**
     * @param bodyInput
     * @return the grid filled with the player's input in the body
     */
    @PostMapping(value = "/fill")
    public void fillGrid(@RequestBody Object bodyInput) {
        //TODO
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
