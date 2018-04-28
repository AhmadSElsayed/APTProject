package Ranker;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;

public class Token {
    long wordID;
    int occurrences;
    int generatedTopic;
    int updateValue;
    double[] wordTopicVector;

    public Token(long wordID, int occurrences, int generatedTopic, int updateValue, double[] wordTopicVector) {
        this.wordID = wordID;
        this.occurrences = occurrences;
        this.generatedTopic = generatedTopic;
        this.updateValue = updateValue;
        this.wordTopicVector = wordTopicVector;
    }

    public Token(String[] line){
        this.wordID = Integer.parseInt(line[0]);
        this.occurrences = Integer.parseInt(line[1]);
    }

    public static Token[] parseString(String document) {
        String[] lines = document.split(";");
        Token[] tokens = new Token[lines.length];
        for (int i = 0; i < lines.length; i++){
            tokens[i] = new Token(lines[i].split(","));
        }
        return tokens;
    }
}

