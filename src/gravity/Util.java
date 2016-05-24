package gravity;

import java.util.ArrayList;
import java.util.List;

public class Util
{
    public static String localColor = "0x3F51B5";
    public static String remoteColor;
    public static String localUser;
    public static String remoteUser = "0xFF9800";
    public static String serverIP = "192.168.1.101";
    public static int serverPort = 6969;
    public static List<Thread> threads = new ArrayList<>();
    public static boolean close;

    public static int gravityMatrix[][] = {
            {0,0,0,0,0,0,0,0},
            {0,5,1,1,1,1,6,0},
            {0,4,5,1,1,6,3,0},
            {0,4,4,5,6,3,3,0},
            {0,4,4,8,7,3,3,0},
            {0,4,8,2,2,7,3,0},
            {0,8,2,2,2,2,7,0},
            {0,0,0,0,0,0,0,0},};

    public static void cellHandler(Cell c, boolean local)
    {
	if (local && Main.getController().myTurn)
	{
	    while (c.getGravity() != 0)
	    {
		    if (getNext(c).isSelected()) break;
		        c=getNext(c);
	    }
        c.setSelected(local);
	    GCP.sendMove(Integer.parseInt(c.getX()), Integer.parseInt(c.getY()));
	    Main.getController().myTurn = false;
	}
	else if (local && !Main.getController().myTurn)
	    Main.getController().dialogManager("It is not your turn!!! Stop clicking this table!!!", true);
	else if (!local)
	{
	    c.setSelected(local);
	}
    }

    public static Cell getNext(Cell c)
    {
	try{return Main.getController().getCells().get(getCellN(c)+getDeltaFromGravity(c.getGravity()));}
        catch (Exception e)
        {
            return new Cell(null);
        }
    }

    public static int getDeltaFromGravity(int gravity)
    {
        switch (gravity)
        {
            case 0:
                return 0;
            case 1:
                return -8;
            case 2:
                return 8;
            case 3:
                return 1;
            case 4:
                return -1;
            case 5:
                return -9;
            case 6:
                return -7;
            case 7:
                return 9;
            case 8:
                return 7;
        }
        return 0;
    }

    public static void applyGravity(Cell cell)
    {
        int r = Integer.parseInt(String.valueOf(cell.getId().charAt(0)));
        int c = Integer.parseInt(String.valueOf(cell.getId().charAt(1)));
        cell.setGravity(gravityMatrix[r][c]);
    }

    public static int getCellN(Cell cell)
    {
        return Integer.parseInt(String.valueOf(cell.getId().charAt(0)))*8+Integer.parseInt(String.valueOf(cell.getId().charAt(1)));
    }

    public static void cellPainter(List<String> payload)
    {
        int x = Integer.parseInt(payload.get(0));
        int y = Integer.parseInt(payload.get(1));
        cellHandler(Main.getController().getCells().get((x*8)+y), false);
    }
}
