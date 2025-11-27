import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

class Item implements Grabbable, Serializable {
    private String description;
    private String name;
    private String icon = "_";

    public Item() {

    }

    public Item(String name, String description, String icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BufferedImage getIcon() throws IOException {
        return ImageIO.read(Item.class.getResourceAsStream("imgs/" + icon + ".png"));
    }

    @Override
    public List<Item> getItems() {
        return Collections.singletonList(this);
    }
}

interface Grabbable {
    List<Item> getItems();

    String getName();
}

class ItemContainer implements Grabbable, Serializable {
    private String name;
    private List<Item> items;

    ItemContainer(String name, List<Item> items) {
        this.name = name;
        this.items = items;
    }

    @Override
    public List<Item> getItems() {
        return items;
    }

    @Override
    public String getName() {
        return name;
    }
}
