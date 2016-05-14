package gravity;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
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
        error               //general error
    }

    public GCP()
    {
        try
        {
            server = new Socket(Settings.serverIP, Settings.serverPort);
            writer = new PrintWriter(server.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
        } catch (UnknownHostException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static Message decodeIncoming()
    {
        StringTokenizer tokenizer = null;
        try
        {
            tokenizer = new StringTokenizer(reader.readLine(), DELIMITER);
            return new Message(Codes.valueOf(tokenizer.nextToken()), tokenizer.nextToken());
        } catch (IOException e)
        {
            e.printStackTrace();
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

    public static boolean login()
    {
        writer.println(messageComposer(Codes.login, Settings.localUser + "|" + Settings.localColor));
        if (decodeIncoming().code.equals(Codes.login_ok))
            return true;
        if (decodeIncoming().code.equals(Codes.login_usr))
            return false;
        return false;
    }

    public static String messageComposer(Codes code, String message)
    {
        return new String(code.toString() + DELIMITER + message + "\n\r");

    }

    static class Message
    {
        GCP.Codes code;
        List<String> payload;

        public Message(GCP.Codes code, String payload)
        {
            this.code = code;
            StringTokenizer tokenizer = new StringTokenizer(payload, GCP.DELIMITER);
            while (tokenizer.hasMoreTokens())
                this.payload.add(tokenizer.nextToken());
        }
    }
}