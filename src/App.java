import java.util.Scanner;

public class App {
    /**
     * entry point of the program
     */
    public static void main(String[] args) {
        //get instance of the class
        App app = new App();
        //call methods to print menu and process the user selection
        app.printMenu();
        app.switchChoice();
    }

    /**
     * Prints the menu on program start.
     */
    public void printMenu(){

        System.out.println("Select an option from the menu ( 1 - 2 )");
        System.out.println("---------------------------------------------");
        System.out.println("1. Archive files");
        System.out.println("2. Read Archive file");
    }

    /**
     * Reads user input for the menu choice.
     * @return      int value
     */
    public int getUserChoice(){

        Scanner sc = new Scanner(System.in);
        int choice = -1;

        //continue the loop if the user enters an invalid choice
        while (choice < 1 || choice > 2){

            try {
                System.out.println("Please enter your choice");
                //read the input and convert it to the int
                choice = Integer.parseInt(sc.nextLine());

            } catch (NumberFormatException e){
                //display exception message
                System.out.println("Invalid choice. Please try again.");
            }
        }
        return choice;
    }

    /**
     * Processes the user choice. Starts the processing or prints archive file.
     */
    public void switchChoice(){

        int choice = getUserChoice();

        switch (choice) {
            case 1:
                //method call to start processing
                new ProcessFiles().start();
                break;
            case 2:
                //method call to print archive file
                new ProcessFiles().printArchiveFile();
                break;
        }

    }
}
