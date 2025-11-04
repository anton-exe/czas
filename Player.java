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

    public void takeItem(String area) {
        ArrayList<Item> items = currentRoom.takeItem(area);
        if (items == null) {
            GUI.println("<font color=red>no such container!</font>");
            return;
        }
        inventory.addAll(items);
        GUI.println("picked up:");
        for (Item item : items) {
            GUI.printf("- %s\n", item.getName());
        }
    }

    public ArrayList<Item> getInventory() {
        return inventory;

    }

    public String getName() {
        return name;
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
