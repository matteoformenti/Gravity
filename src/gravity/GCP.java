package gravity;

import com.jfoenix.controls.JFXDialog;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Gravity communication protocol manager
 */
public class GCP
{
    public static Socket server;
    public static PrintWriter writer;
    public static BufferedReader reader;
    public static NetworkListener networkListener;
    public static Thread nlThread;
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
        move,
        turn
    }

    public static void loginOk()
    {
        Main.getController().dialog.close();
        writer.println(messageComposer(Codes.matchmaker, "null"));
    }

    public static void loginUsr()
    {
        Main.getController().dialogs.stream().filter(dialog -> dialog.getId().equals("login-dialog")).forEach(JFXDialog::close);
        Main.getController().dialogManager("This username is already in use! Choose another one!", true);
    }

    public static void matchmakerQueue()
    {
        Platform.runLater(() -> Main.getController().dialogs.add(Main.getController().loadingDialog("Waiting for an opponent", "queue-dialog", false)));
    }

    public static void match(Message m)
    {
        Platform.runLater(() ->
        {
            Main.getController().dialogs.stream().filter(d -> d.getId() != null).filter(d -> d.getId().equals("queue-dialog")).forEach(JFXDialog::close);
            Main.getController().spawnBoard();
            Util.remoteUser = m.getPayload().get(0);
            Util.remoteColor = (Util.localColor.equals(m.getPayload().get(1)) ? ("#212121") : m.getPayload().get(1));
            Main.getController().dialogManager("You're in a match against " + Util.remoteUser, true);
        });
    }

    public static void matchOver(Message m)
    {
        Main.getController().myTurn = false;
        Platform.runLater(() -> Main.getController().matchOverDialog("The match is over, obviously the winner is "+((m.getPayload().get(0).equals(Util.localUser))?"you!":Util.remoteUser)+". What's your next move?", "match_over"));
    }

    public static void matchError(Message m)
    {
        System.out.println("MATCH_ERROR");
    }

    public static void error(Message m)
    {
        System.out.println("ERROR");
    }

    public static void move(Message m)
    {
        Util.cellPainter(m.getPayload());
        Main.getController().myTurn = true;
    }

    public static void sendMove(int x, int y)
    {
        GCP.writer.println(GCP.messageComposer(GCP.Codes.move, x + GCP.DELIMITER + y));
        Main.getController().myTurn = false;
    }

    public static void turn(Message m)
    {
        Platform.runLater(() ->
        {Main.getController().myTurn = false;
        if(m.getPayload().get(0).equals(Util.localUser))
            Main.getController().myTurn = true;
        Main.getController().dialogManager((Main.getController().myTurn)?"It's your turn!":("It's "+Util.remoteUser+"'s turn"), true);
        });
    }

    public static void startGCP()
    {
        try
        {
            server = new Socket(Util.serverIP, Util.serverPort);
            writer = new PrintWriter(server.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            networkListener = new NetworkListener();
            nlThread = new Thread(networkListener);
            nlThread.start();
        }
        catch (UnknownHostException e){}
        catch (IOException e){}
    }

    public static String messageComposer(Codes code, String message)
    {
        return new String(code.toString() + DELIMITER + message);
    }

    public static void login() // to server
    {
        if (writer == null || reader == null)
            Main.getController().dialogManager("Looks like the server isn't responding...", false);
        else
            writer.println(messageComposer(Codes.login, (Util.localUser + "|" + Util.localColor)));
    }
}