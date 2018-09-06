package autext;

import java.io.IOException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Lenovo
 */
public class Word_match extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("Text/Html");
        PrintWriter out = response.getWriter();
        String word = request.getParameter("w");
        String line = "";
        ArrayList<String> words = new ArrayList<String>();
        List<Integer> percentage_match = new ArrayList<Integer>();
        String output="";
        File file = new File("D:\\projeCT\\Word Suggestion\\dictionary.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            while ((line = reader.readLine()) != null) {
                words.add(line);
            }

            for (int i = 0; i < words.size(); i++) {
                double p_match=printSimilarity(word, words.get(i));
                int pp_match=(int) (p_match*100);
                percentage_match.add((int) pp_match);
            }

        }
        int max = Collections.max(percentage_match);
        int index = percentage_match.indexOf(max);
        output+=words.get(index)+",";
       
        percentage_match.set(index,0);
        
        max = Collections.max(percentage_match);
        index = percentage_match.indexOf(max);
        output+=words.get(index)+",";
    
        percentage_match.set(index,0);
        
        max = Collections.max(percentage_match);
        index = percentage_match.indexOf(max);
        output+=words.get(index)+",";
 
        out.println(output);
        

    }

    public static double printSimilarity(String s, String t) {
        double match = similarity(s, t);
        return match;
    }

    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0; /* both strings are zero length */ }
        /* // If you have StringUtils, you can use it to calculate the edit distance:
         return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) /
         (double) longerLength; */
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }

}
