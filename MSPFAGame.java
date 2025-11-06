/* this is a text adventure game based on the MSPA (MicroSoft Paint Adventures) series, specifically homestuck
 * 
 * this was made for a university project
 */

import java.util.HashMap;
import java.util.Iterator;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MSPFAGame {
    private static Player player;
    private static HashMap<String, Room> rooms;

    public static void main(String[] args) throws IOException {
        rooms = new HashMap<>();

        Room voidRoom = new Room("in the void", null);
        Room startRoom = voidRoom;

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(MSPFAGame.class.getResourceAsStream("/rooms.json"));

        Iterator<String> roomNames = jsonNode.get("rooms").fieldNames();
        while (roomNames.hasNext()) {
            String roomName = roomNames.next();
            Room room = new Room(jsonNode.get("rooms").get(roomName).get("desc").asText(""),
                    jsonNode.get("rooms").get(roomName).get("items"));
            rooms.put(roomName, room);

            if (roomName.equals(jsonNode.get("START").asText())) {
                startRoom = room;
            }
        }

        roomNames = jsonNode.get("rooms").fieldNames();
        while (roomNames.hasNext()) {
            String roomName = roomNames.next();
            Room room = rooms.get(roomName);

            Iterator<String> directions = jsonNode.get("rooms").get(roomName).get("conns").fieldNames();
            while (directions.hasNext()) {
                String direction = directions.next();
                room.setExit(direction,
                        rooms.get(jsonNode.get("rooms").get(roomName).get("conns").get(direction).asText()));
            }
        }

        // create the Player character and start outside
        player = new Player("Player", startRoom);

        // show GUI
        // launch(args);
        GUI.init();

        GUI.println();
        GUI.println("A young person stands in their room.");
        GUI.println("It just so happens that this day, the 27th of July, is this person's 13th birthday.");
        GUI.println("\nType 'help' if you need help.");

        printInfo();
    }

    public static void printInfo() {
        GUI.print("\n" + player.getCurrentRoom().getLongDescription());
    }

    public static void exit() {
        exit(true);
    }

    public static void exit(boolean save) {
        if (save) {
            System.out.println("save function will go here");
        }
        System.exit(0);
    }

    public static Player getPlayer() {
        return player;
    }
}
