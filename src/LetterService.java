import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

public class LetterService implements ILetterService {
    @Override
    public void CombineTwoLetters(String inputFile1, String inputFile2, String resultFile) {

        try {
            PrintWriter printWriter = new PrintWriter(resultFile);
            BufferedReader br = new BufferedReader(new FileReader(inputFile1));

            //read text from the inputFile1 and write it on the resultFile
            String line = br.readLine();

            while (line != null) {
                printWriter.println(line);
                line = br.readLine();
            }

            //append a separator line to the resultFile
            printWriter.println("--------------");

            //start reading second file
            br = new BufferedReader(new FileReader(inputFile2));
            line = br.readLine();

            //append the content inputFile2 content to the resultFile
            while (line != null) {
                printWriter.println(line);
                line = br.readLine();
            }

            br.close();
            printWriter.flush();
            printWriter.close();

        } catch (Exception e) {

            //print stack trace in case of exception.
            e.printStackTrace();
        }

    }
}
