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
    private static boolean gameOver = false;

    public static final File SAVE_FILE = new File("./data.sav");

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
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
            Room voidRoom = new Room("in the void", null, 0, 0, 1, new HashMap<String, String>());
            Room startRoom = voidRoom;

            rooms = new HashMap<>();

            Iterator<String> roomNames = jsonNode.get("rooms").fieldNames();
            while (roomNames.hasNext()) {
                String roomName = roomNames.next();
                JsonNode roomNode = jsonNode.get("rooms").get(roomName);
                // @SuppressWarnings("unchecked") // it's fine
                Room room = new Room(roomNode.get("desc").asText(""),
                        roomNode.get("items"), roomNode.get("map").get("x").asInt(0),
                        roomNode.get("map").get("y").asInt(0),
                        roomNode.get("map").get("index").asInt(0),
                        objectMapper.convertValue(roomNode.get("requirements"), Map.class));
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
            player = new Player("the Bob from all those examples", startRoom);
        }

        // show GUI
        // launch(args);
        GUI.init();

        GUI.println();
        if (SAVE_FILE.exists()) {
            GUI.println("[You need to get the bus to the University of Stupidly Far. You are running out of time.]");
        } else {
            GUI.print("You wake up.");
            Thread.sleep(2000);
            GUI.println(" It's a beatiful tuesday morning and you check your phone.");
            Thread.sleep(2500);
            GUI.println("\noh shit oh fuck your once every two hours bus leaves in 30 minutes");
            Thread.sleep(2000);
            GUI.println("you need to get ready now shit you went to bed at 20:00 HOW—");
            Thread.sleep(2000);
        }
        GUI.println("\n[Type 'help' if you need help.]");

        printInfo();

        GUI.unblockCmds();

        while (!gameOver) {
            System.out.print("");
        }

        GUI.blockCmds();
        GUI.println("</font>\n\n\n\nYou run over to the bus stop as fast as you can.");
        Thread.sleep(1500);
        GUI.print("\nLuckily, the bus is still there and you get on board");
        Thread.sleep(1500);
        GUI.println(", after struggling to get your leap card out of your wallet at the door.");
        Thread.sleep(2000);
        GUI.println("You sit at a seat near the back and fall asleep...");
        Thread.sleep(4000);
        GUI.println(
                "\nWhen you wake back up 3 hours later, the bus is stuck in traffic and you are still barely halfway.");
        Thread.sleep(1500);
        GUI.println("Today's exam is already halfway done.");
        Thread.sleep(1500);
        GUI.println("\n\n== THE END ==\n\n");
        GUI.print("(delete data.sav if you want to play again for some strange reason)");
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

    public static void endGame() {
        gameOver = true;
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
