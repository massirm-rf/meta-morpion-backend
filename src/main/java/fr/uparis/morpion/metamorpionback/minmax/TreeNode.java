package fr.uparis.morpion.metamorpionback.minmax;

import fr.uparis.morpion.metamorpionback.model.Grid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TreeNode {
    private Grid state;
    private List<TreeNode> children;

    public TreeNode(Grid state) {
        this.state = state;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }

}
