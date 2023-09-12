package fr.uparis.morpion.metamorpionback.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NextGridDTO {

    private Integer row;
    private Integer column;
    private Player player;
    private boolean isFinished;


}
