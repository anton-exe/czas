/* this is a text adventure game
 * 
 * this was made for a university project
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Game {
    private static Player player;
    private static Map<String, Room> rooms;
    private static String[][] maps;

    public static final File SAVE_FILE = new File("./data.sav");

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(Game.class.getResourceAsStream("/data.json"));

        maps = objectMapper.readValue(jsonNode.get("maps").toString(), new TypeReference<String[][]>() {
        });

        if (SAVE_FILE.exists()) {
            FileInputStream savefile = new FileInputStream(SAVE_FILE);
            ObjectInputStream ois = new ObjectInputStream(savefile);
            SaveableGame gameObj = (SaveableGame) ois.readObject();
            player = gameObj.getPlayer();
            rooms = gameObj.getRooms();
            savefile.close();
        } else {
            Room voidRoom = new Room("in the void", null, 0, 0, 0);
            Room startRoom = voidRoom;

            rooms = new HashMap<>();

            Iterator<String> roomNames = jsonNode.get("rooms").fieldNames();
            while (roomNames.hasNext()) {
                String roomName = roomNames.next();
                JsonNode roomNode = jsonNode.get("rooms").get(roomName);
                Room room = new Room(roomNode.get("desc").asText(""),
                        roomNode.get("items"), roomNode.get("map").get("x").asInt(0),
                        roomNode.get("map").get("y").asInt(0),
                        roomNode.get("map").get("index").asInt(0));
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
            player = new Player("DebugPlayer", startRoom);
        }

        // show GUI
        // launch(args);
        GUI.init();

        GUI.println();
        GUI.println("[[PLACEHOLDER]]");
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
            try {
                SaveableGame saveObj = new SaveableGame(player, rooms);

                FileOutputStream savefile = new FileOutputStream(SAVE_FILE);
                ObjectOutputStream oos = new ObjectOutputStream(savefile);
                oos.writeObject(saveObj);
                savefile.close();
            } catch (Exception e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
        System.exit(0);
    }

    public static Player getPlayer() {
        return player;
    }

    public static String[][] getMaps() {
        return maps;
    }

    public static Room getRoom(String name) {
        return rooms.get(name);
    }
}

class SaveableGame implements Serializable {
    private Player player;
    private Map<String, Room> rooms;

    public Player getPlayer() {
        return player;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    SaveableGame(Player player, Map<String, Room> rooms) {
        this.player = player;
        this.rooms = rooms;
    }
}
