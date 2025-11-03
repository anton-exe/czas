class Parser {
    public static void parseCmd(String cmd) {
        String[] args = cmd.split(" ");
        switch (args[0]) {
            case "help":
                System.out.print("Commands:\n"
                        + "help           : display this message\n"
                        + "go <direction> : move in that direction\n"
                        + "quit           : quit the game\n");
                break;

            case "go":
                if (args.length < 2) {
                    System.out.println("you need to specify a direction!");
                    break;
                }
                MSPFAGame.getPlayer().move(args[1]);
                break;

            case "quit":
                MSPFAGame.input.close();
                System.exit(0);
                break;

            default:
                System.out.println("Invalid command!");
                break;
        }
    }
}
