import java.util.Scanner;
import java.io.*;
import java.util.Queue;
import java.util.Stack;
import java.util.ArrayList;
import java.util.LinkedList;

public class Game {

    // Globals
	public static int counter = 0;
    public static final boolean DEBUGGING = false;   // Debugging flag.
    public static final int MAX_LOCALES = 2;        // Total number of rooms/locations we have in the game.
    public static final int MAX_SCORE = 40;
    public static int currentLocale = 0;            // Player starts in locale 0.
    public static String command;                   // What the player types as he or she plays the game.
    public static boolean stillPlaying = true;      // Controls the game loop.
    public static Locale[] locations;               // An uninitialized array of type Locale. See init() for initialization.
    public static Items[] itemArray;
    public static Items[] inventory;
    public static ArrayList<String> ShopItems = new ArrayList<String>();
    public static int moves = 0;                    // Counter of the player's moves.
    public static int score = 5;                    // Tracker of the player's score.
    public static double gold = 0;
    public static Queue<String> queue;
    public static Stack<String> backwards;
    public static boolean mapAcquired = false;
    
    public static void main(String[] args) {
    	System.out.println("Welcome to the Haunted Game");
        // Set starting locale, if it was provided as a command line parameter.
        if (args.length > 0) {
           try {
              int startLocation = Integer.parseInt(args[0]);
              // Check that the passed-in value for startLocation is within the range of actual locations.
              if ( startLocation >= 0 && startLocation <= MAX_LOCALES) {
                  currentLocale = startLocation;
              }
           } catch(NumberFormatException ex) {   // catch(Exception ex)
              System.out.println("Warning: invalid starting location parameter: " + args[0]);
              if (DEBUGGING) {
                 System.out.println(ex.toString());
              }
           }
        }

        // Get the game started.
        init();
        updateDisplay();

        // Game Loop
        while (stillPlaying) {
            getCommand();
            navigate();
            updateDisplay();
        }

        // We're done. Thank the player and exit.
        System.out.println("Thank you for playing.");
    }


    private static void init() {
        // Initialize any uninitialized globals.
        command = new String();

        // Set up the location instances of the Locale class.
        Locale loc0 = new Locale(0);
        loc0.setName("Abandoned Playground");
        loc0.setDesc("The swing set is barely holding together. You can go east or south from here.");
        loc0.setNorth(0);
        loc0.setSouth(1);
        loc0.setEast(2);
        loc0.setWest(0);
       
        Locale loc1 = new Locale(1);
        loc1.setName("Old Mansion");
        loc1.setDesc("There is a hole in the window. You can go north or east from here.");
        loc1.setNorth(0);
        loc1.setSouth(1);
        loc1.setEast(3);
        loc1.setWest(1);
        
        OuterDimension loc2 = new OuterDimension(2); //Locale(2);
        loc2.setName("Graveyard");
        loc2.setDesc("The tombstones are chipped on the sides. You can go east, west or south from here.");
        loc2.setDimensionLocation("Far Far Away");
        loc2.setNorth(2);
        loc2.setSouth(3);
        loc2.setEast(4);
        loc2.setWest(0);
        
        Locale loc3 = new Locale(3);
        loc3.setName("Sheriff's Office");
        loc3.setDesc("There seems to be no law here anymore. You can go east, west or north from here.");
        loc3.setNorth(2);
        loc3.setSouth(3);
        loc3.setEast(5);
        loc3.setWest(1);
        
        Locale loc4 = new Locale(4);
        loc4.setName("Haunted Bakery");
        loc4.setDesc("Stale bread is behind the glass window. You can go east, west or south from here.");
        loc4.setNorth(4);
        loc4.setSouth(5);
        loc4.setEast(6);
        loc4.setWest(2);
        
        OuterDimension loc5 = new OuterDimension(5); //Locale(5);
        loc5.setName("Broken Fountain");
        loc5.setDesc("There seems to be frogs living in it. You can go east, west or north from here.");
        loc5.setDimensionLocation("Stolen property of an Alien");
        loc5.setNorth(4);
        loc5.setSouth(5);
        loc5.setEast(7);
        loc5.setWest(3);
        
        Locale loc6 = new Locale(6);
        loc6.setName("Magick Shoppe");
        loc6.setDesc("Outside the ancient oak tree there is a sign that displays the words 'Magick For Sale'. In the shop there is a \n Magic Wand \n Old Staff \n Spellbook \n Love Potion\n You can go west or south from here.");
        loc6.setNorth(6);
        loc6.setSouth(7);
        loc6.setEast(6);
        loc6.setWest(4);
        
        Locale loc7 = new Locale(7);
        loc7.setName("Run-down School");
        loc7.setDesc("The front door's lock is completely rusted over. You can go north or west from here.");
        loc7.setNorth(6);
        loc7.setSouth(7);
        loc7.setEast(7);
        loc7.setWest(5);
        
        locations = new Locale[8];
        locations[2] = loc2; // Graveyard   
        locations[0] = loc0; // Abandoned Playground  
        locations[1] = loc1; // Old Mansion  
        locations[3] = loc3; // Sheriff's Office
        locations[4] = loc4; // Haunted Bakery
        locations[5] = loc5; // Broken Fountain
        locations[6] = loc6; // Magick Shoppe
        locations[7] = loc7; // Run-down School
        
        Items item1 = new Items(0);
        item1.setName("axe");
        item1.setDesc("Sharp and pointy, probably can cut through wood.");
        
        Items item2 = new Items(1);
        item2.setName("wrench");
        item2.setDesc("You can probably knock something unconcious with this.");
        
        Items item3 = new Items(2);
        item2.setName("hammer");
        item2.setDesc("Can make nails go through things.");
        
        Items item4 = new Items(3);
        item2.setName("crowbar");
        item2.setDesc("Makes prying open doors seem like childs play.");
        
        itemArray = new Items[4];
        itemArray[0] = item1;
        itemArray[1] = item2;
        itemArray[2] = item3;
        itemArray[3] = item4;
        
        Items notTaken0 = new Items(0);
        notTaken0.setName("Item not found");
        notTaken0.setDesc("");
        
        Items notTaken1 = new Items(1);
        notTaken1.setName("Item not found");
        notTaken1.setDesc("");
        
        Items notTaken2 = new Items(2);
        notTaken2.setName("Item not found");
        notTaken2.setDesc("");
        
        Items notTaken3 = new Items(3);
        notTaken3.setName("Item not found");
        notTaken3.setDesc("");
        
        inventory = new Items[4];
        inventory[0] = notTaken0;
        inventory[1] = notTaken1;
        inventory[2] = notTaken2;
        inventory[3] = notTaken3;
        
        if (DEBUGGING) {
           System.out.println("All game locations:");
           for (int i = 0; i < locations.length; ++i) {
              System.out.println(i + ":" + locations[i].toString());
           }
           System.out.println("When you open your eyes you awake in the playground. You see a piece of paper under the slide that looks like it could be a map and take it.");
        }
    }

    private static void updateDisplay() {
        System.out.println(locations[currentLocale].getName());
        System.out.println(locations[currentLocale].getDesc());
    }

    private static void getCommand() {
        System.out.print("[" + moves + " moves, score " + score + ", " + ((score * 100) / MAX_SCORE) + "% completed] " + "Gold Collected: " + gold);
        Scanner inputReader = new Scanner(System.in);
        command = inputReader.nextLine();  // command is global.
    }

    private static void navigate() {
        final int INVALID = currentLocale;
        int dir = INVALID; 
        
        backwards = new Stack<String>();
        queue = new LinkedList<String>();
      
        if (        command.equalsIgnoreCase("north") || command.equalsIgnoreCase("n") ) {
            dir = locations[currentLocale].getNorth();
            backwards.add("north");
            queue.add("north");
            counter++;
        } else if ( command.equalsIgnoreCase("south") || command.equalsIgnoreCase("s") ) {
            dir = locations[currentLocale].getSouth();
            backwards.add("south");
            queue.add("south");
            counter++;
        } else if ( command.equalsIgnoreCase("east")  || command.equalsIgnoreCase("e") ) {
            dir = locations[currentLocale].getEast();
            backwards.add("east");
            queue.add("east");
            counter++;
        } else if ( command.equalsIgnoreCase("west")  || command.equalsIgnoreCase("w") ) {
            dir = locations[currentLocale].getWest();
            backwards.add("west");
            queue.add("west");
            counter++;

        } else if (command.equalsIgnoreCase("take")) {
        	take();
        } else if ( command.equalsIgnoreCase("quit")  || command.equalsIgnoreCase("q")) {
            quit();
        } else if ( command.equalsIgnoreCase("help")  || command.equalsIgnoreCase("h")) {
            help();   

        } else if (command.equalsIgnoreCase("inventory") || command.equalsIgnoreCase("i")){
        	inv();
        } else if (command.equalsIgnoreCase("buy") || command.equalsIgnoreCase("b")){
        	buy();
        } 
        if (command.equalsIgnoreCase("map") || command.equalsIgnoreCase("m")){
        		System.out.println("Abandoned Playground\t Graveyard\t Haunted Bakery\t          Magick Shoppe");
        		System.out.println("Old Mansion\t      Sheriff's Office   Broken Fountain\t  Run-down School");
        }
        
        if(!(command.equalsIgnoreCase("north") || command.equalsIgnoreCase("n") ||
        	 command.equalsIgnoreCase("east") || command.equalsIgnoreCase("e") || 
        	 command.equalsIgnoreCase("west") || command.equalsIgnoreCase("w") || 
        	 command.equalsIgnoreCase("south") || command.equalsIgnoreCase("s") ||
        	 command.equalsIgnoreCase("quit") || command.equalsIgnoreCase("q") ||
        	 command.equalsIgnoreCase("help") || command.equalsIgnoreCase("h") ||
        	 command.equalsIgnoreCase("inventory") || command.equalsIgnoreCase("i") ||
        	 command.equalsIgnoreCase("buy") || command.equalsIgnoreCase("b") ||
        	 command.equalsIgnoreCase("take") ||
        	 command.equalsIgnoreCase("map") || command.equalsIgnoreCase("m"))){ 
        		System.out.println("That is not a valid command, type help or h for valid commands.");
        }

        if (dir == currentLocale) {
            System.out.println("You did not move to a different location.");
        } else {
            currentLocale = dir;
            moves = moves + 1;

            if (locations[dir].getHasVisited() == false && dir != 0){
                score = score + 5;
                gold = gold + 5;
                locations[dir].setHasVisited(true);
            }
        }
        
        if(score == 40){
        	System.out.println("You have successfully navigated through the map and find the exit to safety.");
        	quit();
        }
       }
   
    private static void take(){
    	if(currentLocale == 1){

            inventory[0] = itemArray[0];
            System.out.println(itemArray[0].getDesc());
        }
        else if (currentLocale == 2) {

            inventory[1] = itemArray[1];
            System.out.println(itemArray[1].getDesc());
        }
        else if (currentLocale == 3) {

            inventory[2] = itemArray[2];
            System.out.println(itemArray[2].getDesc());
        }
        else if (currentLocale == 4) {

            inventory[3] = itemArray[3];
            System.out.println(itemArray[3].getDesc());
        }
    }
    
    private static void inv() {
    	System.out.println("Items: Map, " + inventory[0].getName() + ", " + inventory[1].getName() + ", " + inventory[2].getName() + ", " + inventory[3].getName() + ShopItems);
    }
    
    private static void buy() {
    	 // Make the list manager.
        ListMan lm1 = new ListMan();
        lm1.setName("Magic Items");
        lm1.setDesc("These are some of my favorite things.");


        final String fileName = "magicitems.txt";
        readMagicItemsFromFile(fileName, lm1);

        // Display the list of items.jkk
        System.out.println(lm1.toString());

        // Ask player for an item.
        Scanner inputReader = new Scanner(System.in);
        System.out.print("What item would you like to buy? ");
        String targetItem = new String();
        targetItem = inputReader.nextLine();
        System.out.println();

        ListItem li = new ListItem();
        li = sequentialSearch(lm1, targetItem);
        if (li != null) {
            System.out.println(li.toString());
        }
    }


    //Private List Stuff
    private static ListItem sequentialSearch(ListMan lm,
                                             String target) {


        ListItem retVal = null;
        System.out.println("Searching for " + target + ".");
        int counter = 0;
        ListItem currentItem = new ListItem();
        currentItem = lm.getHead();
        boolean isFound = false;
        while ( (!isFound) && (currentItem != null) ) {
            counter = counter +1;
            if (currentItem.getName().equalsIgnoreCase(target)) {
                // We found it!
                isFound = true;
                retVal = currentItem;
            } else {
                // Keep looking.
                currentItem = currentItem.getNext();
            }
        }
        if (isFound) {
            System.out.println("I found " + target + " after " + counter + " comparisons.");
            if (gold >= currentItem.getCost()) {
                System.out.println(currentItem.getName() + " has been purchased and added to your inventory.");
                gold = gold - currentItem.getCost();
                //add to inventory
                ShopItems.add(currentItem.getName());
            }
            else if (gold <= currentItem.getCost()){
                System.out.println(currentItem.getName() + " is too expensive, acquire more gold to purchase this item.");
            }
            return  currentItem;

        } else {
            System.out.println("Sorry I could not find " + target + " in " + counter + " comparisons. Feel free to re-enter the shop to try again.");
        }

        return retVal;
    }


    private static void readMagicItemsFromFile(String fileName,
                                               ListMan lm) {
        File myFile = new File(fileName);
        try {
            Scanner input = new Scanner(myFile);
            while (input.hasNext()) {
                // Read a line from the file.
                String itemName = input.nextLine();

                // Construct a new list item and set its attributes.
                ListItem fileItem = new ListItem();
                fileItem.setName(itemName);
                fileItem.setCost(Math.random() * 10);
                fileItem.setNext(null); // Still redundant. Still safe.

                // Add the newly constructed item to the list.
                lm.add(fileItem);
            }
            // Close the file.
            input.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File not found. " + ex.toString());
        }
    }
    
    private static void help() {
        System.out.println("The commands are as follows:");
        System.out.println("   n/north");
        System.out.println("   s/south");
        System.out.println("   q/quit");
        System.out.println("   m/map if it is unlocked");
        System.out.println("   h/help to repeat these instructions");
        System.out.println("   take");
        System.out.println("   If you are in the Magick Shoppe:");
        System.out.println("   b/buy");
    }

    private static void quit() {
        System.out.println("Directions Taken:\n");
        for(int i = 0; i < counter; i++){
        	System.out.println(queue.peek() + "\n");
        	queue.poll();
        } 
        System.out.println("Directions Taken Backwards:\n");
        for(int j = 0; j < counter; j++){
        	System.out.println(backwards.peek() + "\n");
        	backwards.pop();
        }        
        System.err.println(counter);
        stillPlaying = false;
    }
}