package fr.uparis.morpion.metamorpionback.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GridDTO {

    private int row;
    private int column;
    private int childRow;
    private int childColumn;
    private BoxEnum value;


}
