import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
public class ProcessFiles {

    //directory paths
    private static final String baseDir = "C:\\CombinedLetters";
    private static final String admissionDir = baseDir.concat("\\Input\\Admission");
    private static final String scholarshipDir = baseDir.concat("\\Input\\Scholarship");
    private static final String outputDir = baseDir.concat("\\Output");
    private static final String archiveDir = baseDir.concat("\\Archive");

    //list to store common file names
    private static final ArrayList<String> commonFiles = new ArrayList<>();
    /**
     * Starting point of the processing.
     * Checks the time first then checks for directories of past 7 days.
     * Processes a directory if it needs to be processed.
     */
    public void start(){

        //get the current hour
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        //ask for confirmation if its not 10 AM.
        if (currentHour != 10){
            System.out.println("Application is scheduled to run at 10 AM. Are you sure you want to run at this time?");
            System.out.println("Press y to continue or any other key to exit.");

            Scanner sc = new Scanner(System.in);
            char c = sc.next().charAt(0);

            //exit program if any key other than y is entered
            if (c != 'y') {
                System.exit(0);
            }
        }

        //count how many file needs to be archived
        int archiveCount = 0;

        //check for past 7 days directories
        for (int i = 0; i > -6; i--){

            //get directory name
            String dirName = getDirectoryName(i);

            //process the directory if it needs to be
            if (needsProcessing(dirName)){

                //method call to process the directory
                processDirectory(dirName);
                archiveCount++;
            }
        }

        //display message if there is no any unarchived directory
        if (archiveCount == 0) {
            System.out.print("No any unarchived directory found.");
        }
    }

    /**
     * Generates a directory name.
     * @param  offset  Difference from today, eg. -1 for yesterday.
     * @return      String value directory name in yyyyMMdd format
     */
    public String getDirectoryName(int offset){

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, offset);

        int month = calendar.get(Calendar.MONTH) +1; //month is 0 based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);

        String monthString = String.format("%02d", month);
        String dayString = String.format("%02d", day);

        return String.valueOf(year) + monthString + dayString;
    }

    /**
     * Checks if a directory needs to be processed by checking if
     * it exists and has not been processed already.
     * @param  dirName  Directory name to check if it needs to be processed.
     * @return          boolean value
     */
    public boolean needsProcessing(String dirName){

        //directory name in admission directory
        String admissionDirPath = admissionDir.concat("\\" + dirName);

        //create the file instance directory path
        File admissionDir = new File(admissionDirPath);

        //check if it exists.
        if ( !admissionDir.exists() ){

            return false;

        } else {
            //if it exists, look for the archive-confirmation.txt file in that directory
            String archivedFilePath = admissionDirPath.concat("\\archive-confirmation.txt");
            File archivedFile = new File(archivedFilePath);

            //if that file exists, that means the directory has been archived already.
            if (archivedFile.exists()){
                return false;
            } else {
                return true;
            }
        }

    }

    /**
     * Archives a directory and generates the report.
     * Creates a confirmation file in the input directory.
     * @param  dirName  Directory name that needs to be archived.
     */
    public void processDirectory(String dirName){

        //get instance of the LetterService class
        LetterService service = new LetterService();

        //paths for admission and scholarship directories
        String admissionDirPath = admissionDir.concat("\\" + dirName);
        File admissionDir = new File(admissionDirPath);

        String scholarshipDirPath = scholarshipDir.concat("\\" + dirName);

        //get all files in the admission directory
        File[] admissionLetters = admissionDir.listFiles();

        //process each file
        assert admissionLetters != null;
        for (File admissionLetter : admissionLetters) {

            //get file name
            String fileNameFull = admissionLetter.getName();

            //since the file name is admission-xxxxxxxx.txt, split the name string to get only the part after -.
            String fileName = fileNameFull.split("-")[1];

            //get the file name without extension
            String fileNameWithoutExt = fileName.split("\\.")[0];

            //file paths for admission and scholarship file
            String admissionFilePath = admissionDirPath.concat("\\" + fileNameFull);
            String scholarshipFilePath = scholarshipDirPath.concat("\\scholarship-" + fileName);

            //file paths for output and archive directory
            String resultFilePath = outputDir.concat("\\"+fileName);
            String archiveFilePath = archiveDir.concat("\\" + fileNameFull);

            try {
                //get instances of BufferedReader and PrintWriter to read and write the files
                BufferedReader br = new BufferedReader(new FileReader(admissionFilePath));
                PrintWriter printWriter = new PrintWriter(archiveFilePath);

                String line = br.readLine();

                //read the content of the admission file and write it to the file in archive directory
                while (line != null){
                    printWriter.println(line);
                    line = br.readLine();
                }

                br.close();
                printWriter.flush();
                printWriter.close();

            } catch (Exception e) {

                //print stack trace in case of exception
                e.printStackTrace();
            }

            //create instance of the scholarship file
            File scholarshipFile = new File(scholarshipFilePath);

            //check if the scholarship file for the same student exists
            if (scholarshipFile.exists()){

                //scholarship file exists, combine it
                service.CombineTwoLetters(admissionFilePath, scholarshipFilePath, resultFilePath);

                //add the common files to the commonFiles ArrayList
                commonFiles.add(fileNameWithoutExt);
            }
        }

        //method call to generate archive.txt file
        generateReport(dirName, commonFiles);

        //method call to generate archive-confirmation.txt file
        createArchiveConfirmationFile(dirName);
    }

    /**
     * Generates report of archived files.
     * Creates archive.txt file in the output directory.
     *
     * @param  dirName  Directory name which is archived.
     * @param  list     Arraylist that stores list of files that have been archived.
     */
    public void generateReport(String dirName, ArrayList<String> list)  {

        //path for archive.txt file
        String archiveFilePath = outputDir.concat("\\archive.txt");

        //extract year, month and day from the directory name
        String year = dirName.substring(0, 4);
        String month = dirName.substring(4, 6);
        String day = dirName.substring(6, 8);

        try {
            //write the header text on archive.txt file
            PrintWriter printWriter = new PrintWriter(archiveFilePath);

            printWriter.println(month + "/" + day + "/" + year + " Report");
            printWriter.println("-----------------------");
            printWriter.println();
            printWriter.println("Number of Combined Letters: " + commonFiles.size());

            //get file names from the list and write on the archive.txt file
            for (String fileName : commonFiles) {
                printWriter.println("\t" +fileName);
            }

            printWriter.flush();
            printWriter.close();

            //write confirmation message on Console
            System.out.println("Directory " + dirName + " archived.");

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Prints contents of the archive.txt file in console.
     */
    public void printArchiveFile() {
        String archiveFilePath = outputDir.concat("\\archive.txt");

        try {

            //read the file contents
            BufferedReader br = new BufferedReader(new FileReader(archiveFilePath));
            String line = br.readLine();

            //print its contents
            while (line != null) {
                System.out.println(line);
                line = br.readLine();
            }

            br.close();

        } catch (Exception e) {
            System.out.println("Error: No such file. Perhaps the directory is not archived.");
        }

    }

    /**
     * Creates a text file in the input directory with timestamp.
     * The program will look for this file before processing.
     *
     * @param  dirName  Input directory name where the text file will be generated.
     */
    public void createArchiveConfirmationFile(String dirName){

        String archiveConfirmationFilePath = admissionDir.concat("\\" + dirName + "\\archive-confirmation.txt");

        try {
            PrintWriter printWriter = new PrintWriter(archiveConfirmationFilePath);

            //get current date and time
            Date date = new Date();

            //write it on the file
            printWriter.println("Directory archived on:");
            printWriter.println(date);

            printWriter.flush();
            printWriter.close();

        } catch (Exception e) {

            //print stack trace in case of exception.
            e.printStackTrace();
        }

    }
}
