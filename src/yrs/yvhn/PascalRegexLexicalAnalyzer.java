package yrs.yvhn;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PascalRegexLexicalAnalyzer {

    private static final String OperatorRegex = ":=|\\+|-|\\*|/|%|==|<>|>|<|>=|<=|and|and then" +
            "|or|or else|not|&|\\||!|~|<<|>>|xor|shl|shr|div|mod";
    private static final String KeywordRegex = "const|array|begin" +
            "|case|do|downto|else" +
            "|end|for|function|goto" +
            "|if|label|nil|of" +
            "|packed|procedure|program" +
            "|record|in|repeat|file" +
            "|set|type|until" +
            "|var|while|with";

    private String _code = "";
    private String _resOfAnalysis = "";

    private String[] _lexemes;

    public PascalRegexLexicalAnalyzer(String fileName) throws IOException
    {
        _code = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);

        String[] lexemes = _code.split("//[^\\n]*\\n");
        _code = String.join("", lexemes);
        lexemes = _code.split("\\(\\*[^(**)]*\\*\\)");
        _code = String.join("", lexemes);
        lexemes = _code.split("\\{[^{}]*}");
        _code = String.join("", lexemes);
        lexemes = _code.split("\\n+|\\t+|\\s+|\\r+");

        _lexemes = lexemes;
        FindStrings();
        FindDelimiters();
        FindOperators();
    }

    public void RemoveComments()
    {
        List<String> lexemesWithoutComments = new ArrayList<>();

        for (int i = 0; i < _lexemes.length; ++i)
        {
            if (_lexemes[i].matches("\\s*\\(\\*.*"))
            {
                while (++i < _lexemes.length && !_lexemes[i].matches(".*\\*\\)"))
                {

                }
                continue;
            }
            if (_lexemes[i].matches("\\s*\\{.*"))
            {
                while (++i < _lexemes.length && !_lexemes[i].matches(".*}"))
                {

                }
                continue;
            }
            if (_lexemes[i].matches("\\s*\\\\.*"))
            {
                while (++i < _lexemes.length && !_lexemes[i].matches(".*\\n"))
                {

                }
                continue;
            }
            lexemesWithoutComments.add(_lexemes[i]);
        }
        _lexemes = new String[lexemesWithoutComments.size()];
        lexemesWithoutComments.toArray(_lexemes);
    }

    public void FindDelimiters()
    {
        List<String> lexemesWithDelims = new ArrayList<>();

        for (String item : _lexemes)
        {
            String lastChar = Character.toString(item.charAt(item.length() - 1));
            if (item.length() > 0 && isDelimiter(lastChar))
            {
                lexemesWithDelims.add(item.substring(0, item.length()-1));
                lexemesWithDelims.add(lastChar);
            }
            else
            {
                lexemesWithDelims.add(item);
            }
        }
        _lexemes = new String[lexemesWithDelims.size()];
        lexemesWithDelims.toArray(_lexemes);
    }

    public void FindStrings()
    {
        List<String> lexems = new ArrayList<>();
        int i = -1;

        while (++i < _lexemes.length)
        {
            if (_lexemes[i].contains("'"))
            {
                if (_lexemes[i].indexOf("'") > 0)
                {
                    lexems.add(_lexemes[i].substring(0, _lexemes[i].indexOf("'")));

                    int pos1 = _lexemes[i].indexOf("'");
                    String strConst = _lexemes[i].substring(pos1, _lexemes[i].length());

                    while (++i < _lexemes.length)
                    {
                        if (_lexemes[i].contains("'"))
                        {
                            int pos = _lexemes[i].indexOf("'");
                            strConst += _lexemes[i].substring(0, pos + 1);

                            lexems.add(strConst);
                            if (pos + 1 < _lexemes[i].length()) {
                                lexems.add(_lexemes[i].substring(pos + 1));
                            }
                            break;
                        }
                        else
                        {
                            strConst += _lexemes[i];
                        }
                        if (i == _lexemes.length - 1)
                        {
                            lexems.add(strConst);
                        }
                    }
                }
                continue;
            }
            lexems.add(_lexemes[i]);
        }
        _lexemes = new String[lexems.size()];
        lexems.toArray(_lexemes);
    }

    public void FindOperators()
    {
        List<String> lexemes = new ArrayList<>();

        for (int i = 0; i < _lexemes.length; ++i)
        {
            if (GetClass(_lexemes[i]) == "undefined")
            {
                String[] operands = _lexemes[i].split("(?=" + OperatorRegex + ")|(?<=" + OperatorRegex + ")");

                lexemes.addAll(Arrays.asList(operands));
                continue;
            }
            lexemes.add(_lexemes[i]);
        }
        _lexemes = new String[lexemes.size()];
        lexemes.toArray(_lexemes);
    }

    public String GetResult()
    {
        return _resOfAnalysis;
    }

    public void Analyze()
    {
        for (String item : _lexemes)
        {
            String lexemeClass = GetClass(item);

            if (lexemeClass != "comment")
            {
                _resOfAnalysis += item + " - " + lexemeClass + "\n";
            }
        }
    }

    public String GetClass(String lexeme)
    {
        if (isKeyWord(lexeme))
        {
            return  "keyword";
        }
        if (isDirective(lexeme))
        {
            return "preprocessor directive";
        }
        if (isNumber(lexeme))
        {
            return  "number";
        }
        if (isOperator(lexeme))
        {
            return  "operator";
        }
        if (isDelimiter(lexeme))
        {
            return "delimiter";
        }
        if (isCharConst(lexeme))
        {
            return  "character constant";
        }
        if (isIdentifier(lexeme))
        {
            return "identifier";
        }
        return "undefined";
    }

    public boolean isNumber(String text)
    {
        return text.matches("^(\\+|-)?[0-9]+.?[0-9]+$|^0x([0-9]|[A-F])*$");
    }

    public boolean isCharConst(String text)
    {
        return text.matches("^'.*'$");
    }

    public boolean isDirective(String text)
    {
        return text.matches("^\\$([A-Z]|[0-9])+$");
    }

    public boolean isComment(String text)
    {
        return text.matches("^\\(\\*.*\\*\\)$|^\\{.*\\}$|^//.*$");
    }

    public boolean isKeyWord(String text)
    {
        return text.matches(KeywordRegex);
    }

    public boolean isOperator(String text)
    {
        return text.matches(OperatorRegex);
    }

    public boolean isDelimiter(String text)
    {
        return text.matches(";|\\.|\\(|\\)");
    }

    public boolean isIdentifier(String text)
    {
        return text.matches("^[A-z]+([A-z]|[0-9])*$");
    }
}
