package codes.kooper.blockify.models;

import codes.kooper.blockify.types.BlockifyChunk;
import codes.kooper.blockify.types.BlockifyPosition;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class View {

    private final HashMap<BlockifyChunk, HashMap<BlockifyPosition, Material>> blocks;
    private final Stage stage;
    private boolean breakable;
    private Pattern pattern;

    public View(Stage stage, Pattern pattern) {
        this.blocks = new HashMap<>();
        this.stage = stage;
        this.pattern = pattern;
    }

    public BlockifyPosition getHighestBlock(int x, int z) {
        for (int y = stage.getMaxPosition().getY(); y >= stage.getMinPosition().getY(); y--) {
            BlockifyPosition position = new BlockifyPosition(x, y, z);
            if (hasBlock(position) && getBlock(position).isSolid()) {
                return position;
            }
        }
        return null;
    }

    public BlockifyPosition getLowestBlock(int x, int z) {
        for (int y = stage.getMinPosition().getY(); y <= stage.getMaxPosition().getY(); y++) {
            BlockifyPosition position = new BlockifyPosition(x, y, z);
            if (hasBlock(position) && getBlock(position).isSolid()) {
                return position;
            }
        }
        return null;
    }

    public void removeBlock(BlockifyPosition position) {
        if (hasBlock(position)) {
            blocks.get(position.toBlockifyChunk()).remove(position);
            if (blocks.get(position.toBlockifyChunk()).isEmpty()) {
                blocks.remove(position.toBlockifyChunk());
            }
        }
    }

    public void removeBlocks(Set<BlockifyPosition> positions) {
        for (BlockifyPosition position : positions) {
            removeBlock(position);
        }
    }

    public void addBlock(BlockifyPosition position) {
        if (!hasBlock(position)) {
            blocks.putIfAbsent(position.toBlockifyChunk(), new HashMap<>());
            blocks.get(position.toBlockifyChunk()).put(position, getPattern().getRandomBlockData().getMaterial());
        }
    }

    public void addBlocks(Set<BlockifyPosition> positions) {
        for (BlockifyPosition position : positions) {
            addBlock(position);
        }
    }

    public boolean hasBlock(BlockifyPosition position) {
        return blocks.containsKey(position.toBlockifyChunk()) && blocks.get(position.toBlockifyChunk()).containsKey(position);
    }

    public boolean hasBlocks(Set<BlockifyPosition> positions) {
        for (BlockifyPosition position : positions) {
            if (!hasBlock(position)) {
                return false;
            }
        }
        return true;
    }

    public Map<BlockifyPosition, Material> getMultiBlockChanges() {
        Map<BlockifyPosition, Material> blockChanges = new HashMap<>();
        for (Map.Entry<BlockifyChunk, HashMap<BlockifyPosition, Material>> entry : blocks.entrySet())
        {
            blockChanges.putAll(entry.getValue());
        }
        return blockChanges;
    }

    public Material getBlock(BlockifyPosition position) {
        return blocks.get(position.toBlockifyChunk()).get(position);
    }

    public boolean hasChunk(int x, int z) {
        return blocks.containsKey(new BlockifyChunk(x, z));
    }

    public void setBlocks(Set<BlockifyPosition> positions, Material material) {
        for (BlockifyPosition position : positions) {
            setBlock(position, material);
        }
    }

    public void setBlock(BlockifyPosition position, Material material) {
        if (hasBlock(position)) {
            blocks.get(position.toBlockifyChunk()).put(position, material);
        }
    }
}