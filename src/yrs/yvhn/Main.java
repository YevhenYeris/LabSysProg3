package yrs.yvhn;

import java.io.IOException;

public class Main {

    public static void main(String[] args)
    {
        try
        {
            PascalAnalyzer analyzer = new PascalAnalyzer("input.txt");
            analyzer.Analyze();
            String res = analyzer.GetResult();
            System.out.println(res);
        }
        catch (IOException e)
        {
            System.out.println(e.getStackTrace());
        }

    }
}
