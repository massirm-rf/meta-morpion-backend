package fr.uparis.morpion.metamorpionback.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GridDTO {

    private Integer row;
    private Integer column;
    private Integer childRow;
    private Integer childColumn;
    private BoxEnum value;


}
