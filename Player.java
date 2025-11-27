import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Player implements Serializable {
    private String name;
    private Room currentRoom;
    private List<Item> inventory;

    public Player(String name, Room startingRoom) {
        this.name = name;
        this.currentRoom = startingRoom;
        this.inventory = new ArrayList<>();
    }

    public void takeItems(String key) {
        Grabbable items = currentRoom.takeItems(key);
        if (items == null) {
            GUI.println("<font color=red>no such item!</font>");
            return;
        }
        inventory.addAll(items.getItems());
        if (items.getItems().size() <= 1) {
            GUI.println("picked up: " + items.getItems().get(0).getName());
        } else {
            GUI.println("picked up:");
            for (Item item : items.getItems()) {
                GUI.printf("- %s\n", item.getName());
            }
        }
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public void move(String direction) {
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom != null) {
            if (nextRoom.canEnter(this)) {
                currentRoom = nextRoom;
            }
        } else {
            GUI.print("You can't go that way!\n");
        }
    }
}
