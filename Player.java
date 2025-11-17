import java.util.ArrayList;

class Player {
    private String name;
    private Room currentRoom;
    private ArrayList<Item> inventory;

    public Player(String name, Room startingRoom) {
        this.name = name;
        this.currentRoom = startingRoom;
        this.inventory = new ArrayList<>();
    }

    public void takeItemContainer(String area) {
        ArrayList<Item> items = currentRoom.takeItemContainer(area);
        if (items == null) {
            GUI.println("<font color=red>no such container!</font>");
            return;
        }
        inventory.addAll(items);
        GUI.println("picked up:");
        for (Item item : items) {
            GUI.printf("- %s\n", item.getName());
        }
        GUI.rerenderInventory();
    }

    public void dropItem(String itemName) {
        Item item = null;
        System.out.println();
        for (Item i : inventory) {
            System.out.println(i.getName());
            if (i.getName().equals(itemName)) {
                item = i;
            }
        }
        if (item == null) {
            GUI.println("<font color=red>no such item!</font>");
            return;
        }
        dropItem(item);
        GUI.rerenderInventory();
    }

    public void dropItem(Item item) {
        dropItem(item, "floor");
        GUI.rerenderInventory();
    }

    public void dropItem(Item item, String area) {
        currentRoom.dropItem(item, area);
        inventory.remove(item);
        GUI.rerenderInventory();
    }

    public ArrayList<Item> getInventory() {
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
            currentRoom = nextRoom;
            // System.out.println("You moved to: " + currentRoom.getDescription());
        } else {
            GUI.print("You can't go that way!\n");
        }
    }
}
