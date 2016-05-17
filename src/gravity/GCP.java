package gravity;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Gravity communication protocol manager
 */
public class GCP
{
    public static Socket server;
    public static PrintWriter writer;
    public static BufferedReader reader;

    public static final String DELIMITER = "|";

    enum Codes
    {
        login,              //request for login
        login_ok,           //login ok
        login_usr,          //username not available
        matchmaker,         //start matchmaking
        matchmaker_queue,   //player in queue
        match,              //match started
        match_over,         //match finished
        match_error,        //opponent disconnection
        error,              //general error
        exit,               //disconnect from server
        move
    }

    public GCP()
    {
        try
        {
            server = new Socket(Util.serverIP, Util.serverPort);
            writer = new PrintWriter(server.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
        } catch (UnknownHostException e)
        {
        } catch (IOException e)
        {
        }
    }

    public static Message decodeIncoming()
    {
        StringTokenizer tokenizer = null;
        try
        {
            String in = reader.readLine();
            System.out.println(in);
            tokenizer = new StringTokenizer(in, DELIMITER);
            Codes code = Codes.valueOf(tokenizer.nextToken());
            String s = "";
            while (tokenizer.hasMoreTokens())
                s+=tokenizer.nextToken()+DELIMITER;
            return new Message(code, s);
        } catch (IOException e)
        {
            return null;
        }

//        switch (Codes.valueOf(tokenizer.nextToken()))
//        {
//            case login_ok:
//                Main.getController().dialog.close();
//                break;
//            case login_usr:
//                Main.getController().alertManager("Heheheh we found you! You tried to steal a username, use another one!", 3);
//                break;
//            case matchmaker:
//                break;
//            case matchmaker_queue:
//                Main.getController().alertManager("You're in queue for a match!", 4);
//                break;
//            case match:
//                Main.getController().alertManager("Your opponent is...", 4);
//                break;
//            case match_over:
//                Main.getController().alertManager("Yeahhhhhhh the match is ovaaaarrr, guess who's the winner... obviously ", 1);
//                break;
//            case match_error:
//                Main.getController().alertManager("Your opponent was too scared of you and decided to give up, congratulations, you won!", 2);
//                break;
//            case error:
//                Main.getController().alertManager("There's a problem somewhere, but there's also something you can do! Ask someone to debug everything for you! [Or just find a better game]", 3);
//                break;
//            default:
//                Main.getController().alertManager("Something went wrong, but the cow has no freaking idea of what's the problem, try eating a potato and reboot the world [BTW: congratulations, you managed" +
//                        "to break the whole game, that's really impressive!]", 4);
//                break;
//        }
    }

    public static int login()
    {
        if (writer == null || reader == null)
            return -3;
        writer.println(messageComposer(Codes.login, (Util.localUser + "|" + Util.localColor)));
        Message m = decodeIncoming();
        if (m == null)
            return -3;
        if (m.code.equals(Codes.login_ok))
            return 1;
        if (m.code.equals(Codes.login_usr))
            return -1;
        return -2;
    }

    public static String messageComposer(Codes code, String message)
    {
        return new String(code.toString() + DELIMITER + message);
    }

    static class Message
    {
        GCP.Codes code;
        List<String> payload = new ArrayList<>();

        public Message(GCP.Codes code, String payload)
        {
            this.code = code;
            StringTokenizer tokenizer = new StringTokenizer(payload, GCP.DELIMITER);
            if (payload!=null)
            while (tokenizer.hasMoreTokens())
                this.payload.add(tokenizer.nextToken());
        }
    }
}