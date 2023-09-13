package fr.uparis.morpion.metamorpionback.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NextGridDTO {

    private Integer playedRow;
    private Integer playedColumn;
    private Integer playedChildRow;
    private Integer playedChildColumn;
    private Integer nextRow;
    private Integer nextColumn;
    private Player player;
    private boolean finished;
    private BoxEnum lastChildFinished;


}
