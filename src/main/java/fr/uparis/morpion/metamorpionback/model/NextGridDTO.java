package fr.uparis.morpion.metamorpionback.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NextGridDTO {

    private int row;
    private int column;


}
