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
                    Game.getPlayer().move(args);
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
                    Game.getPlayer().dropItem(args);
                };
            }),
            Map.entry("inv", new Command("inv", "view inventory", "inv", "") {
                @Override
                public void commandLogic(String args) {
                    if (Game.getPlayer().getInventory().size() < 1) {
                        GUI.println("you don't have anything!");
                        return;
                    }
                    GUI.println("you have: ");
                    for (Item item : Game.getPlayer().getInventory()) {
                        GUI.printf("- %s: %s\n", item.getName(), item.getDescription());
                    }
                };
            }),
            Map.entry("look", new Command("look", "look around", "look",
                    "list item containers") {
                @Override
                public void commandLogic(String args) {
                    Set<String> itemKeys = Game.getPlayer().getCurrentRoom().getGrabbableKeys();
                    if (itemKeys.size() < 1) {
                        GUI.println("nothing here!");
                        return;
                    }
                    GUI.print("you see: ");
                    for (String area : itemKeys) {
                        GUI.print(area + ", ");
                    }
                    GUI.backspace(2);
                    GUI.println();
                };
            }),
            Map.entry("name", new Command("name", "set your name", "name &lt;new name&gt;",
                    "") {
                @Override
                public void commandLogic(String args) {
                    if (args.length() < 1) {
                        GUI.print("you need to specify a name!\n");
                        return;
                    }
                    Game.getPlayer().setName(args);
                    GUI.printf("Your name is now: %s\n", Game.getPlayer().getName());
                };
            }),
            Map.entry("take", new Command("take", "take", "take &lt;item&gt;",
                    "take an item, or all items from a container") {
                @Override
                public void commandLogic(String args) {
                    if (args.length() < 1) {
                        GUI.print("you need to specify an item!\n");
                        return;
                    }
                    Game.getPlayer().takeItems(args);
                };
            }),
            Map.entry("quit", new Command("quit", "quit the game") {
                @Override
                public void commandLogic(String args) {
                    Game.exit();
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
        GUI.rerender();
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
