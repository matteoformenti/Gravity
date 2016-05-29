package gravity;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Message
{
    private GCP.Codes code;
    private List<String> payload = new ArrayList<>();

    public Message(GCP.Codes code, String payload)
    {
        this.code = code;
        StringTokenizer tokenizer = new StringTokenizer(payload, GCP.DELIMITER);
        if (payload!=null)
            while (tokenizer.hasMoreTokens())
                this.payload.add(tokenizer.nextToken());
    }

    public GCP.Codes getCode()
    {
        return code;
    }

    public void setCode(GCP.Codes code)
    {
        this.code = code;
    }

    public List<String> getPayload()
    {
        return payload;
    }

    public void setPayload(List<String> payload)
    {
        this.payload = payload;
    }
}
