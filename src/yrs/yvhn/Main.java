package yrs.yvhn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args)
    {
        try
        {
            PascalRegexLexicalAnalyzer analyzer = new PascalRegexLexicalAnalyzer("input.txt");
            analyzer.Analyze();
            String res = analyzer.GetResult();
            System.out.println(res);

            String outputFileName = "output.txt";
            File resultFile = new File(outputFileName);
            resultFile.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
            writer.write(res);
            writer.close();
        }
        catch (IOException e)
        {
            System.out.println(e.getStackTrace());
        }

    }
}
