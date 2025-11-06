import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class Room {
    private String description;
    private HashMap<String, Room> exits; // Map direction to neighboring Room
    private HashMap<String, ArrayList<Item>> items;

    public Room(String description, JsonNode itemNode) {
        ObjectMapper om = new ObjectMapper();
        this.description = description;
        this.exits = new HashMap<>();

        this.items = new HashMap<>();

        this.items.put("floor", new ArrayList<Item>());
        if (itemNode == null) {
            return;
        }
        Iterator<String> itemAreaNames = itemNode.fieldNames();
        while (itemAreaNames.hasNext()) {
            String itemArea = itemAreaNames.next();
            ArrayList<Item> items;
            try {
                items = om.readValue(itemNode.get(itemArea).toString(),
                        new TypeReference<ArrayList<Item>>() {
                        });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return;
            }
            this.items.put(itemArea, items);
        }
    }

    public Set<String> getItemAreas() {
        return items.keySet();
    }

    public ArrayList<Item> getFloorItems() {
        return items.getOrDefault("floor", new ArrayList<Item>());
    }

    public ArrayList<Item> takeItemContainer(String area) {
        ArrayList<Item> items = this.items.get(area);
        this.items.remove(area);
        if (area == "floor") {
            this.items.put("floor", new ArrayList<Item>());
        }
        return items;
    }

    public void dropItem(Item item, String area) {
        items.get(area).add(item);
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
