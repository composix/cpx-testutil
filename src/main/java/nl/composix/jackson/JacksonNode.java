package nl.composix.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.composix.core.Node;
import nl.composix.core.Tree;

public class JacksonNode<T> extends Node<String,T> {
    private static final ObjectMapper mapper = new ObjectMapper();

    public JacksonNode(String[] source, T[] target) {
        super(source, target);
    }

    @Override
    public <V> void assign(V value) {
        if (value instanceof TreeNode) {
            TreeNode tree = (TreeNode) value;
            tree.fieldNames().forEachRemaining((key) -> {
                int index = indexOf(key);
                TreeNode subtree = tree.get(key);
                if (subtree.isContainerNode()) {
                    ((Tree<String,?>) target[index]).assign(subtree);
                } else if (subtree.isValueNode()) {
                    if (subtree.) {

                    }
                    target[index] = su
                }
                if (tree.isContainerNode()) {

                }
                target[indexOf(key)] = get(key);
            });
            for (int i = 0; i < source.length; ++i) {
                if (target[i] != null) {

                }
            }
            return;    
        }
        fromValue(mapper.valueToTree(value));
    }

    public <T> T toValue(Class<T> clazz) {
        if (TreeNode.class.isAssignableFrom(clazz)) {
            return null;
        }
        try {
            return mapper.treeToValue(toValue(TreeNode.class), clazz);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
