package autext;

import com.sun.faces.util.CollectionsUtils;
import com.sun.jndi.toolkit.url.Uri;
import java.io.BufferedReader;
import java.io.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import java.net.URI;
import java.net.URL;
import static javax.servlet.SessionTrackingMode.URL;

/**
 *
 * @author Lenovo
 */
// spelling checker algorithm...if user type placemint...chatbot should reply..do you mean placement?
public class Random_chat extends HttpServlet {

    protected StanfordCoreNLP pipeline;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("Text/Html");
        PrintWriter out = response.getWriter();
        String ques = request.getParameter("q");
        random_chat rc = new random_chat();

        //Greetings
        String Greetings[] = {"hie", "hello", "hey", "whats up", "wats up", "hi", "hii", "heya", "hey chatbot", "hello chatbot", "helo", "how are you"};
        String Responses[] = {"Hey, how may i help you?", "Hello, how may i help you?", "Heya, how may i help you?"};

        //User writes random
        String randoms[] = {"Can you please repeat?", "I only give answers related to college queries.", "I didn't understand what you are telling", "Sorry, i didn't know the answer related to this."};

        //Bye Response
        String bye[] = {"bie", "bye", "buhbie", "bye thank you", "bie thanks for help", "bye thanks", "thanks", "thanks for help", "bbye", "ok", "k", "ok thanks", "ok thank you", "ok thanku", "thanks chatbot", "bbye thanks"};
        String bye_resp[] = {"Thank you for asking.Have a nice day.", "It's nice talk to you. You may visit again."};

        String qus = ques.replaceAll("[^\\w\\s]", "");

        //For user pressing only enter
        if (qus.equals("")) {
            out.println("???");

        } else if (qus.toLowerCase().contains("who are you")) {
            out.println("I am a Chat Bot and i will help you to solve your college related queries");
        } else if (qus.toLowerCase().contains("who made you")) {
            out.println("I am made by Rohan Hemnani & Aayush Kala from IT department");
        } else if (qus.toLowerCase().contains("what you do") || qus.toLowerCase().contains("what you can do")) {
            out.println("I will try to solve your college related queries.");
        } //Greetings response back
        else if (Arrays.asList(Greetings).contains(qus.toLowerCase())) {
            Random rand;
            String resp;
            rand = new Random();
            resp = Responses[rand.nextInt(Responses.length)];
            out.println(resp);
        } //bye responses back
        else if (Arrays.asList(bye).contains(qus.toLowerCase())) {
            Random rand;
            String resp;
            rand = new Random();
            resp = bye_resp[rand.nextInt(bye_resp.length)];
            out.println(resp);
        } else if (!(Arrays.asList(Greetings).contains(ques.toLowerCase()))) {

            // Splitting words by space
            String[] words = qus.split(" ");
            String line;

            //  For removing Stop Words
            for (int i = 0; i < words.length; i++) {

                // words[i] = words[i].replaceAll("[^\\w]", "");
                File file = new File("D:\\projeCT\\Word Suggestion\\Stopwords.txt");
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

                    while ((line = reader.readLine()) != null) {

                        if (words[i].equalsIgnoreCase(line)) {
                            words[i] = "";
                        }

                    }

                }

            }

            //Making String of remaining words
            String output = "";
            String line2;
            for (int j = 0; j < words.length; j++) {
                output += words[j] + " ";
            }

            output = output.trim();
            List lem_array = rc.lemmatize(output);

            int h = 0;
            for (int i = 0; i < lem_array.size(); i++) {
                File file = new File("D:\\projeCT\\Word Suggestion\\keywords.txt");
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

                    while ((line2 = reader.readLine()) != null) {

                        if (lem_array.get(i).toString().toLowerCase().equals(line2)) {
                            h = 1;
                        }

                    }

                }
                if (h == 0) {
                    lem_array.set(i, "");
                }
                h = 0;
            }
            String after_lem = String.join(" ", lem_array);
            
            //Splitting keywords
            String out1[] = after_lem.split("\\s+");

//            for (int i = 0; i < out1.length; i++) {
//                out.println(out1[i]);
//            }
            try {
                Class.forName("com.mysql.jdbc.Driver");

                java.sql.Connection C1 = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_bot?", "root", "");
                Statement SS = C1.createStatement();
                ResultSet rs;

                String qus_kb = "select question from knowledgebase";
                rs = SS.executeQuery(qus_kb);

                List<Integer> percentage_match = new ArrayList<Integer>();
                int matching = 0;
                while (rs.next()) {
                    ArrayList<String> qus_keywords = new ArrayList<String>();
                    int cnt = 0;
                    String qus_kb2 = rs.getString("question");
                    String[] qus_kb3 = qus_kb2.split(" ");
                    Collections.addAll(qus_keywords, qus_kb3);

                    for (int k = 0; k < out1.length; k++) {
                        if (qus_keywords.contains(out1[k].toLowerCase())) {
                            cnt++;
                        }

                    }

                    matching = (int) ((cnt / (float) out1.length) * 100);

                    percentage_match.add((int) matching);

                }
                // out.println("Matching: " + percentage_match);
                int max = Collections.max(percentage_match);
                int index = percentage_match.indexOf(max) + 1;

                if (max == 0) {
                    Random rand;
                    String rands;
                    rand = new Random();
                    rands = randoms[rand.nextInt(randoms.length)];
                    out.println(rands);
                } else if (out1.length == 1 && !(out1[0].equals(""))) {
                    out.println("What you want to know about " + out1[0] + "?");

                } else if (out1[0].equals("")) {
                    out.println("I can't understand what you are saying?");
                } else {
                    ResultSet rs2;
                    String query = "select answer from knowledgebase";
                    rs2 = SS.executeQuery(query);
                    int i = 0;

                    while (rs2.next()) {
                        i++;
                        if (i == index) {
                            String answer = rs2.getString("answer");
                           
                            out.println(answer);
                            
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("catch" + e);
            }
        }
    }

    public List<String> lemmatize(String documentText) {
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");

        this.pipeline = new StanfordCoreNLP(props);
        List<String> lemmas = new LinkedList<String>();
        Annotation document = new Annotation(documentText);
        this.pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
            
                lemmas.add(token.get(LemmaAnnotation.class));
            }
        }
        return lemmas;
    }

}