import java.util.Arrays;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;

class Parser {
    final static public TreeMap<String, Command> cmds = new TreeMap<>(Map.ofEntries(
            Map.entry("help", new Command("help", "display help", "help [command]",
                    "in usage: [square brackets] show OPTIONAL arguments,\n" +
                            "           &lt;angle brackets&gt; show REQUIRED arguments") {
                @Override
                public void commandLogic(String[] args) {
                    GUI.print("<font color=#ffffaa>");
                    if (args.length < 1) {
                        GUI.println("Available commands:");
                        for (Command cmd : cmds.values()) {
                            GUI.println(cmd.getShortHelp());
                        }
                    } else {
                        GUI.println(cmds.get(args[0]).getLongHelp());
                    }
                    GUI.print("</font>");
                };
            }),
            Map.entry("go", new Command("go", "move to new area", "go &lt;direction&gt;",
                    "go to the area in the specified direction") {
                @Override
                public void commandLogic(String[] args) {
                    MSPFAGame.getPlayer().move(args[0]);
                };
            }),
            Map.entry("inv", new Command("inv", "view inventory", "inv", "") {
                @Override
                public void commandLogic(String[] args) {
                    if (MSPFAGame.getPlayer().getInventory().size() < 1) {
                        GUI.println("you don't have anything!");
                        return;
                    }
                    GUI.println("you have: ");
                    for (Item item : MSPFAGame.getPlayer().getInventory()) {
                        GUI.printf("- %s\n", item.getName());
                    }
                    GUI.println();
                };
            }),
            Map.entry("look", new Command("look", "look around", "look",
                    "list item containers") {
                @Override
                public void commandLogic(String[] args) {
                    Set<String> itemAreas = MSPFAGame.getPlayer().getCurrentRoom().getItemAreas();
                    if (itemAreas.size() < 1) {
                        GUI.println("nothing here!");
                        return;
                    }
                    GUI.print("you see: ");
                    for (String area : itemAreas) {
                        GUI.print(" " + area);
                    }
                    GUI.println();
                };
            }),
            Map.entry("open", new Command("open", "open a container", "open &lt;container&gt;",
                    "open a container and take all items inside") {
                @Override
                public void commandLogic(String[] args) {
                    if (args.length < 1) {
                        GUI.print("you need to specify a container!\n");
                        return;
                    }
                    MSPFAGame.getPlayer().takeItem(args[0]);
                };
            }),
            Map.entry("quit", new Command("quit", "quit the game") {
                @Override
                public void commandLogic(String[] args) {
                    MSPFAGame.exit();
                };
            })));

    public static void parseCmd(String cmd) {
        String commandWord = cmd.split(" ")[0];
        String[] args = Arrays.copyOfRange(cmd.split(" "), 1, cmd.split(" ").length);
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

    public abstract void commandLogic(String[] args);
}
