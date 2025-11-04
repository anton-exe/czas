import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

class Room {
    private String description;
    private HashMap<String, Room> exits; // Map direction to neighboring Room
    private HashMap<String, ArrayList<Item>> items;

    public Room(String description, JsonNode itemNode) {
        this.description = description;
        this.exits = new HashMap<>();

        this.items = new HashMap<>();

        if (itemNode == null) {
            return;
        }
        Iterator<String> itemAreaNames = itemNode.fieldNames();
        while (itemAreaNames.hasNext()) {
            String itemArea = itemAreaNames.next();
            ArrayList<Item> items = new ArrayList<>();
            Iterator<String> itemNames = itemNode.get(itemArea).fieldNames();
            while (itemNames.hasNext()) {
                String itemName = itemNames.next();
                items.add(new Item(itemName, itemNode.get(itemArea).get(itemName).asText()));
            }
            this.items.put(itemArea, items);
        }
    }

    public Set<String> getItemAreas() {
        return items.keySet();
    }

    public ArrayList<Item> takeItem(String area) {
        ArrayList<Item> items = this.items.get(area);
        this.items.remove(area);
        return items;
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
