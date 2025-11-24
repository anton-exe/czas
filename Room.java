import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class Room {
    private String description;
    private Map<String, Room> exits; // Map direction to neighboring Room
    private Map<String, Grabbable> items;

    public Room(String description, JsonNode itemNode) {
        this.description = description;
        this.exits = new HashMap<>();
        this.items = new HashMap<>();

        if (itemNode == null) {
            return;
        }

        Iterator<JsonNode> grabbables = itemNode.elements();
        while (grabbables.hasNext()) {
            JsonNode grabbable = grabbables.next();

            try {
                ObjectMapper om = new ObjectMapper(); // stupid JSON library boilerplate
                if (grabbable.isArray()) {
                    Iterator<JsonNode> items = grabbable.elements();

                    String name = "the void";
                    List<Item> itemList = new ArrayList<>();

                    while (items.hasNext()) {
                        JsonNode item = items.next();

                        if (item.isTextual()) {
                            name = item.asText();
                        } else {
                            itemList.add(om.readValue(item.toString(), new TypeReference<Item>() {

                            }));
                        }
                    }

                    this.items.put(name, new ItemContainer(name, itemList));
                } else {
                    Item item = om.readValue(grabbable.toString(), new TypeReference<Item>() {
                    });
                    items.put(item.getName(), item);
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public Set<String> getGrabbableKeys() {
        return items.keySet();
    }

    public Grabbable takeItems(String key) {
        Grabbable items = this.items.get(key);
        this.items.remove(key, items);
        return items;

    }

    public void dropItem(Item item) {
        items.put(item.getName(), item);
    }

    public String getDescription() {
        return description;
    }

    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    public Room getExit(String direction) {
        return exits.get(direction);
    }

    public String getExitString() {
        StringBuilder sb = new StringBuilder();
        for (String direction : exits.keySet()) {
            sb.append(direction).append(" ");
        }
        return sb.toString().trim();
    }

    public String getLongDescription() {
        return "You are " + description + ".\nExits: " + getExitString();
    }
}
