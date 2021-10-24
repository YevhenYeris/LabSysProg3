package yrs.yvhn;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class PascalAnalyzer {

    /*
    – числа (десяткові, з плаваючою крапкою, шістнадцяткові),
    – рядкові та символьні константи,
    – директиви препроцесора,
    – коментарі,
    – зарезервовані слова,
    – оператори,
    – розділові знаки,
    – ідентифікатори.
     */

    private int _currSymb = 0;
    private String _currToken = "undefined";
    private String _currLexem = "undefined";
    private List<String> _keywords = Arrays.asList("const", "or", "array", "begin",
                                                        "case", "do", "downto", "else",
                                                        "end", "for", "function", "goto",
                                                        "if", "label", "div", "mod",
                                                        "and", "nil", "not", "of",
                                                        "packed", "procedure", "program",
                                                        "record", "in", "repeat", "file",
                                                        "set", "then", "to", "type", "until",
                                                        "var", "while", "with");

    private String _code = "";
    private String _resOfAnalysis = "";

    public PascalAnalyzer(String fileName) throws IOException
    {
        _code = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
    }

    public String GetResult()
    {
        return _resOfAnalysis;
    }

    private void readWord()
    {
        _currToken = "identifier";
        StringBuilder readText = new StringBuilder();

        while((_currSymb < _code.length())
                && Character.toString(_code.charAt(_currSymb)).matches("[a-z]|[A-Z]|[0-9]|_"))
        {
            readText.append(_code.charAt(_currSymb));
            _currSymb += 1;
        }
        _currLexem = readText.toString();

        if(_keywords.contains(_currLexem))
        {
            _currToken = "keyword";
        }
        _resOfAnalysis += _currLexem + "  -  " + _currToken + "\n";
    }

    private void readNumber()
    {
        StringBuilder readText = new StringBuilder();
        while((_currSymb < _code.length())
                && Character.toString(_code.charAt(_currSymb)).matches("[0-9]|.|x"))
        {
            readText.append(_code.charAt(_currSymb));
            _currSymb += 1;
        }
        _currLexem = readText.toString();
        _currToken = "number";
        _resOfAnalysis += _currLexem + "  -  " + _currToken + "\n";
    }

    private void readOperator()
    {
        _currToken = "operator";
        _currLexem = Character.toString(_code.charAt(_currSymb));
        _resOfAnalysis += _currLexem + "  -  " + _currToken + "\n";
        _currSymb++;
    }

    private void readSpace()
    {
        _currLexem = "\" \"";
        _currToken = "space";
        _resOfAnalysis += _currLexem + "  -  " + _currToken + "\n";
        _currSymb++;
    }

    private void readEscape()
    {
        _currToken = "escape character";
        _currLexem = "\"\\" + _code.charAt(_currSymb) + "\"";

        _resOfAnalysis += _currLexem + "  -  " + _currToken + "\n";
        _currSymb++;
    }

    private void readBracket()
    {
        _currToken = "bracket";
        _currLexem = Character.toString(_code.charAt(_currSymb));
        _resOfAnalysis += _currLexem + "  -  " + _currToken + "\n";
        _currSymb++;
    }

    private void readDelimiter()
    {
        _currLexem = Character.toString(_code.charAt(_currSymb));
        _currToken = "delimiter";
        _resOfAnalysis += _currLexem + "  -  " + _currToken + "\n";
        _currSymb++;
    }

    private void readCharConst()
    {
        _currToken = "character/string const";
        Character ch = _code.charAt(_currSymb);
        StringBuilder symbols = new StringBuilder();
        if(_code.charAt(_currSymb) == '"') {
            _currSymb++;
            ch = _code.charAt(_currSymb);
            while (_currSymb < _code.length() && (!ch.equals('"'))) {
                symbols.append(_code.charAt(_currSymb));
                _currSymb++;
                ch = _code.charAt(_currSymb);
            }
            _currLexem = symbols.toString();
        }
    }

    private void readComment()
    {
        _currToken = "comment";
        Character ch =  _code.charAt(_currSymb);
        StringBuilder symbols = new StringBuilder();
        if(_code.charAt(_currSymb) == '#')
        {

            ch = _code.charAt(_currSymb);
            while (_currSymb < _code.length() && (!ch.equals('}'))) {
                symbols.append(_code.charAt(_currSymb));
                _currSymb++;
                ch = _code.charAt(_currSymb);

            }
            _currLexem = symbols.toString();
        }
        _resOfAnalysis += _currLexem + "  -  " + _currToken + "\n";
    }

    private void readUndefined()
    {
        _currLexem = Character.toString(_code.charAt(_currSymb));
        _currToken = "undefined or special lexeme";
        _resOfAnalysis += _currLexem + "  -  " + _currToken + "\n";
        _currSymb++;
    }

    public String Analyze()
    {
        String resOfAnalysis = "";
        _currSymb = 0;

        while (_currSymb < _code.length())
        {
            String currChar = Character.toString(_code.charAt(_currSymb));

            if(currChar.matches("[a-z]|[A-Z]|_"))
            {
                readWord();
                continue;
            }
            if(currChar.matches("[0-9]"))
            {
                readNumber();
                continue;
            }
            if(currChar.matches("\\+|-|\\*|/|%|^|=|:"))
            {
                readOperator();
                continue;
            }
            if(_code.charAt(_currSymb) == ' ')
            {
                readSpace();
                continue;
            }
            if(currChar.matches("\\t|\\r|\\n"))
            {
                readEscape();
                continue;
            }
            if(currChar.matches("\\[|]|\\{|}"))
            {
                readBracket();
                continue;
            }
            if(currChar.matches(";|:|,"))
            {
                readDelimiter();
                continue;
            }
            if(currChar.matches("\'|\""))
            {
                readCharConst();
                continue;
            }
            if(_code.charAt(_currSymb) == '{')
            {
                readComment();
                continue;
            }
            if(true)
            {
                readUndefined();
            }
            _currSymb++;
        }

        return  resOfAnalysis;
    }
}
