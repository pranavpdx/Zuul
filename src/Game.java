
import java.util.ArrayList;

/**
 * This class is the main class of the "World of Zuul" application. "World of
 * Zuul" is a very simple, text based adventure game. Users can walk around some
 * scenery. That's all. It should really be extended to make it more
 * interesting!
 * 
 * To play this game, create an instance of this class and call the "play"
 * method.
 * 
 * This main class creates and initialises all the others: it creates all rooms,
 * creates the parser and starts the game. It also evaluates and executes the
 * commands that the parser returns.
 * 
 * @author Michael Kolling and David J. Barnes
 * @version 1.0 (February 2002)
 */

class Game {
	Items head = new Items("head");
	Items heart = new Items("heart");
	Items body = new Items("body");
	Items chip = new Items("chip");
	Items arms = new Items("arms");
	private Parser parser;
	private Room currentRoom;
	Room outside, theatre, pub, lab, office, onetwenty, auditoriam, storage, mathLab, courtyard, gym, lang, props, sec1,
			sec2;
	ArrayList<Items> inventory = new ArrayList<Items>();

	/**
	 * Create the game and initialise its internal map.
	 */
	public Game() {
		createRooms();
		parser = new Parser();
	}

	public static void main(String[] args) {
		Game myGame = new Game();
		myGame.play();
	}

	/**
	 * Create all the rooms and link their exits together.
	 */
	private void createRooms() {

		// create the rooms
		outside = new Room("outside the main entrance of the university");
		theatre = new Room("in a lecture theatre");
		pub = new Room("in the campus pub");
		lab = new Room("in a computing lab");
		office = new Room("in the computing admin office");
		onetwenty = new Room("in the programming lab where this was created");
		auditoriam = new Room("in the auditoriam ");
		storage = new Room("in the creepy old storage closet");
		mathLab = new Room("in math lab where the teachers are screaming about limits");
		courtyard = new Room("in the sunny courtyard");
		gym = new Room("in the gym, a room you've never been in before");
		lang = new Room("in the language department");
		props = new Room(
				"in the props closet filled with dustry props, theres a secret exit to the north!");
		sec1 = new Room(
				"in secret room number one, there is a sign: 'If you have the finished robot enter to the next room'");
		sec2 = new Room("in secret room number 2. Place your finished robot here! If its not finished come back!");
		
		
		// Initialize room exits
		outside.setExit("east", theatre);
		outside.setExit("south", lab);
		outside.setExit("west", pub);
		outside.setExit("north", onetwenty);

		theatre.setExit("west", outside);
		theatre.setExit("south", office);
		theatre.setExit("north", auditoriam);
		theatre.setExit("east", lang);

		pub.setExit("east", outside);
		pub.setExit("west", courtyard);

		lab.setExit("north", outside);
		lab.setExit("east", office);
		lab.setExit("south", gym);

		office.setExit("west", lab);
		office.setExit("east", storage);

		onetwenty.setExit("south", outside);
		onetwenty.setExit("east", auditoriam);
		onetwenty.setExit("north", mathLab);

		auditoriam.setExit("west", onetwenty);
		auditoriam.setExit("south", theatre);
		auditoriam.setExit("east", props);

		storage.setExit("west", office);
		storage.setItem(heart);

		mathLab.setExit("south", onetwenty);
		mathLab.setItem(body);

		courtyard.setExit("east", pub);
		courtyard.setItem(head);

		gym.setExit("north", lab);
		gym.setItem(chip);

		lang.setExit("west", theatre);
		lang.setItem(arms);

		props.setExit("west", auditoriam);
		props.setExit("north", sec1);

		sec1.setExit("south", props);
		sec1.setExit("north", sec2);

		sec2.setExit("south", sec1);

		currentRoom = outside; // start game outside
	}

	/**
	 * Main play routine. Loops until end of play.
	 */
	public void play() {
		printWelcome();

		// Enter the main command loop. Here we repeatedly read commands and
		// execute them until the game is over.

		boolean finished = false;
		while (!finished) {
			Command command = parser.getCommand();
			finished = processCommand(command);
		}
		System.out.println("Thank you for playing.  Good bye.");
	}

	/**
	 * Print out the opening message for the player.
	 */
	private void printWelcome() {
		System.out.println();
		System.out.println("Welcome to the Zuul Game!");
		System.out.println("Adventure is a new, incredibly awesome game that helps an unknown person");
		System.out.println("Collect spare parts and meet him at the secret room to complete the game");
		System.out.println("Type 'help' if you need help.");
		System.out.println();
		System.out.println(currentRoom.getLongDescription());
	}

	/**
	 * Given a command, process (that is: execute) the command. If this command ends
	 * the game, true is returned, otherwise false is returned.
	 */
	private boolean processCommand(Command command) {
		boolean wantToQuit = false;

		if (command.isUnknown()) {
			System.out.println("I don't know what you mean...");
			return false;
		}

		String commandWord = command.getCommandWord();
		if (commandWord.equals("help")) {
			printHelp();
		} else if (commandWord.equals("go")) {
			wantToQuit = goRoom(command);
		} else if (commandWord.equals("quit")) {
			wantToQuit = quit(command);
		} else if (commandWord.equals("inventory")) {
			printInventory();
		} else if (commandWord.equals("get")) {
			getItem(command);
		} else if (commandWord.equals("drop")) {
			dropItem(command);
		}
		return wantToQuit;
	}

	private void dropItem(Command command) {
		// TODO Auto-generated method stub
		if (!command.hasSecondWord()) {
			// if there is no second word, what to drop...
			System.out.println("Drop what?");
			return;
		}

		String item = command.getSecondWord();

		// Try to leave current room.
		Items newItem = null;
		int index = 0;
		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i).getDescription().equals(item))
				newItem = inventory.get(i);
			index = i;
		}

		if (newItem == null)
			System.out.println("You don't have that item!!");
		else {
			inventory.remove(index);
			currentRoom.setItem(new Items(item));
			System.out.println("Dropped: " + item + "!");
		}
	}

	private void getItem(Command command) {
		// TODO Auto-generated method stub
		if (!command.hasSecondWord()) {
			// if there is no second word, we don't know what to pick up...
			System.out.println("Get what?");
			return;
		}

		String item = command.getSecondWord();

		// Try to leave current room.
		Items newItem = currentRoom.getItems(item);

		if (newItem == null)
			System.out.println("That item is not here!");
		else {
			inventory.add(newItem);
			currentRoom.removeItem(item);
			System.out.println("Picked up: " + item + "!");
		}
	}

	private void printInventory() {
		// TODO Auto-generated method stub
		String output = "";
		for (int i = 0; i < inventory.size(); i++) {
			output += inventory.get(i).getDescription();
		}
		System.out.println("You are carrying: ");
		System.out.println(output);

	}

	// implementations of user commands:

	/**
	 * Print out some help information. Here we print some stupid, cryptic message
	 * and a list of the command words.
	 */
	private void printHelp() {
		System.out.println("You are lost. You are alone. You wander");
		System.out.println("around at the university.");
		System.out.println();
		System.out.println("Your command words are:");
		parser.showCommands();
	}

	/**
	 * Try to go to one direction. If there is an exit, enter the new room,
	 * otherwise print an error message.
	 */
	private boolean goRoom(Command command) {
		if (!command.hasSecondWord()) {
			// if there is no second word, we don't know where to go...
			System.out.println("Go where?");
			return false;
		}

		String direction = command.getSecondWord();

		// Try to leave current room.
		Room nextRoom = currentRoom.getExit(direction);

		if (nextRoom == null)
			System.out.println("There is no door!");
		else {
			currentRoom = nextRoom;
			System.out.println(currentRoom.getLongDescription());
			if(currentRoom == sec2 && inventory.contains(head) && inventory.contains(heart) && inventory.contains(chip) && inventory.contains(arms) && inventory.contains(body)) {
				System.out.println("All the parts you have gathered suddently merge and create a robot");
				System.out.println("An old sage appears in the room");
				System.out.println("He whispers 'Thank you for finishing my robot'");
				System.out.println("Then instantly he and the robot dissapear ");
				System.out.println("You win!");
				return true;
			}
		}
		return false;
	}

	/**
	 * "Quit" was entered. Check the rest of the command to see whether we really
	 * quit the game. Return true, if this command quits the game, false otherwise.
	 */
	private boolean quit(Command command) {
		if (command.hasSecondWord()) {
			System.out.println("Quit what?");
			return false;
		} else
			return true; // signal that we want to quit
	}
}