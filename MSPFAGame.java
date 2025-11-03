/* This game is a classic text-based adventure set in a university environment.
   The Player starts outside the main entrance and can navigate through different rooms like a 
   lecture theatre, campus pub, computing lab, and admin office using simple text commands (e.g., "go east", "go west").
    The game provides descriptions of each location and lists possible exits.

Key features include:
Room navigation: Moving among interconnected rooms with named exits.
Simple command parser: Recognizes a limited set of commands like "go", "help", and "quit".
Player character: Tracks current location and handles moving between rooms.
Text descriptions: Provides immersive text output describing the Player's surroundings and available options.
Help system: Lists valid commands to guide the Player.
Overall, it recreates the classic Zork interactive fiction experience with a university-themed setting, 
emphasizing exploration and simple command-driven gameplay
*/

import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MSPFAGame {
    private static Player player;
    private static HashMap<String, Room> rooms;
    public static Scanner input;

    public static void main(String[] args) throws IOException {
        rooms = new HashMap<>();

        Room voidRoom = new Room("in the void");
        Room startRoom = voidRoom;

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(MSPFAGame.class.getResourceAsStream("/rooms.json"));

        Iterator<String> roomNames = jsonNode.get("rooms").fieldNames();
        while (roomNames.hasNext()) {
            String roomName = roomNames.next();
            Room room = new Room(jsonNode.get("rooms").get(roomName).get("desc").asText(""));
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

        System.out.println();
        System.out.println("Welcome to the University adventure!");
        System.out.println("Type 'help' if you need help.");

        input = new Scanner(System.in);

        while (true) {
            System.out.print("\n" + player.getCurrentRoom().getLongDescription() + "\n > ");
            Parser.parseCmd(input.nextLine());
        }
    }

    public static Player getPlayer() {
        return player;
    }
}
