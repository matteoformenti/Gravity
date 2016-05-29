package gravity;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class NetworkListener extends Task<Void>
{
    private BufferedReader reader = GCP.reader;
    private static String DELIMITER = GCP.DELIMITER;

    @Override
    protected Void call() throws Exception
    {
        while (!Util.close)
        {
            StringTokenizer tokenizer;
            try
            {
                String in = reader.readLine();
                tokenizer = new StringTokenizer(in, DELIMITER);
                GCP.Codes code = GCP.Codes.valueOf(tokenizer.nextToken());
                String s = "";
                while (tokenizer.hasMoreTokens())
                    s+=tokenizer.nextToken()+DELIMITER;
                Message m = new Message(code, s);
                switch (m.getCode())
                {
                    case login:
                        break;
                    case login_ok:
                        GCP.loginOk();
                        break;
                    case login_usr:
                        GCP.loginUsr();
                        break;
                    case matchmaker:
                        break;
                    case matchmaker_queue:
                        GCP.matchmakerQueue();
                        break;
                    case match:
                        GCP.match(m);
                        break;
                    case match_over:
                        GCP.matchOver(m);
                        break;
                    case match_error:
                        GCP.matchError(m);
                        break;
                    case error:
                        GCP.error(m);
                        break;
                    case exit:
                        break;
                    case move:
                        GCP.move(m);
                        break;
                    case turn:
                        GCP.turn(m);
                        break;
                }
            } catch (IOException e)
            {
                return null;
            }
        }
        return null;
    }
}
