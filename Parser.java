import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;

class Parser {
    final static public TreeMap<String, Command> cmds = new TreeMap<>(Map.ofEntries(
            Map.entry("help", new Command("help", "display help", "help [command]",
                    "in usage: [square brackets] show OPTIONAL arguments,\n" +
                            "           &lt;angle brackets&gt; show REQUIRED arguments") {
                @Override
                public void commandLogic(String args) {
                    GUI.print("<font color=#ffffaa>");
                    if (args.length() < 1) {
                        GUI.println("Available commands:");
                        for (Command cmd : cmds.values()) {
                            GUI.println(cmd.getShortHelp());
                        }
                    } else {
                        GUI.println(cmds.get(args).getLongHelp());
                    }
                    GUI.print("</font>");
                };
            }),
            Map.entry("go", new Command("go", "move to new area", "go &lt;direction&gt;",
                    "go to the area in the specified direction") {
                @Override
                public void commandLogic(String args) {
                    MSPFAGame.getPlayer().move(args);
                };
            }),
            Map.entry("drop", new Command("drop", "drop an item", "drop &lt;item&gt;",
                    "drop an item from your inventory onto the floor") {
                @Override
                public void commandLogic(String args) {
                    if (args.length() < 1) {
                        GUI.print("you need to specify an item!\n");
                        return;
                    }
                    MSPFAGame.getPlayer().dropItem(args);
                };
            }),
            Map.entry("inv", new Command("inv", "view inventory", "inv", "") {
                @Override
                public void commandLogic(String args) {
                    if (MSPFAGame.getPlayer().getInventory().size() < 1) {
                        GUI.println("you don't have anything!");
                        return;
                    }
                    GUI.println("you have: ");
                    for (Item item : MSPFAGame.getPlayer().getInventory()) {
                        GUI.printf("- %s: %s\n", item.getName(), item.getDescription());
                    }
                    GUI.println();
                };
            }),
            Map.entry("look", new Command("look", "look around", "look",
                    "list item containers") {
                @Override
                public void commandLogic(String args) {
                    Set<String> itemAreas = MSPFAGame.getPlayer().getCurrentRoom().getItemAreas();
                    ArrayList<Item> floorItems = MSPFAGame.getPlayer().getCurrentRoom().getFloorItems();
                    if (itemAreas.size() < 2 && floorItems.size() < 1) {
                        GUI.println("nothing here!");
                        return;
                    }
                    if (itemAreas.size() > 1) {
                        GUI.print("you see: ");
                        for (String area : itemAreas) {
                            if (area == "floor") {
                                continue;
                            }
                            GUI.print(" " + area);
                        }
                        GUI.println();
                    }
                    if (floorItems.size() > 0) {
                        GUI.println("the floor has:");
                        for (Item item : floorItems) {
                            GUI.printf("- %s", item.getName());
                        }
                        GUI.println();
                    }
                };
            }),
            Map.entry("open", new Command("open", "open a container", "open &lt;container&gt;",
                    "open a container and take all items inside") {
                @Override
                public void commandLogic(String args) {
                    if (args.length() < 1 || args == "floor") {
                        GUI.print("you need to specify a container!\n");
                        return;
                    }
                    MSPFAGame.getPlayer().takeItemContainer(args);
                };
            }),
            Map.entry("pickup", new Command("pickup", "pickup all floor items", "pickup") {
                @Override
                public void commandLogic(String args) {
                    MSPFAGame.getPlayer().takeItemContainer("floor");
                };
            }),
            Map.entry("quit", new Command("quit", "quit the game") {
                @Override
                public void commandLogic(String args) {
                    MSPFAGame.exit();
                };
            })));

    public static void parseCmd(String cmd) {
        String commandWord = cmd.split(" ")[0];
        String args = "";
        if (cmd.split(" ").length > 1) {
            args = cmd.split(" ", 2)[1];
        }
        if (cmds.containsKey(commandWord)) {
            cmds.get(commandWord).commandLogic(args);
        } else {
            GUI.printf("<font color=red>ERROR! %s is not a valid command!</font>\n", commandWord);
        }
    }
}

abstract class Command {
    private String name;
    private String shortDesc;
    private String description;
    private String usage;

    Command(String name) {
        this(name, "(No Description)");
    }

    Command(String name, String shortDesc) {
        this(name, shortDesc, name);
    }

    Command(String name, String shortDesc, String usage) {
        this(name, shortDesc, usage, "");
    }

    Command(String name, String shortDesc, String usage, String description) {
        this.name = name;
        this.shortDesc = shortDesc;
        this.usage = usage;
        this.description = description;
    }

    public String getShortHelp() {
        return String.format("%24s | %s", name, shortDesc);
    }

    public String getLongHelp() {
        return String.format("%s\n-----\n%s\n%s", usage, shortDesc, description);
    }

    public abstract void commandLogic(String args);
}
