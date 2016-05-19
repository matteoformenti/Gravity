package gravity;

import java.util.ArrayList;
import java.util.List;

public class Util
{
    public static String localColor = "0x3F51B5";
    public static String remoteColor;
    public static String localUser;
    public static String remoteUser = "0xFF9800";
    public static String serverIP = "127.0.0.1";
    public static int serverPort = 6969;
    public static GCP gcp;
    public static List<Thread> threads = new ArrayList<>();

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
        if (Main.getController().myTurn || !local)
        {
            switch (c.getGravity())
            {
                case 0:
                    c.setSelected(local);
                    break;
                case 1:
                    while (c.getGravity() != 0)
                    {
                        if (top(c).isSelected())
                            break;
                        c = top(c);
                    }
                    c.setSelected(local);
                    break;
                case 2:
                    while (c.getGravity() != 0)
                    {
                        if (bottom(c).isSelected())
                            break;
                        c = bottom(c);
                    }
                    c.setSelected(local);
                    break;
                case 3:
                    while (c.getGravity() != 0)
                    {
                        if (right(c).isSelected())
                            break;
                        c = right(c);
                    }
                    c.setSelected(local);
                    break;
                case 4:
                    while (c.getGravity() != 0)
                    {
                        if (left(c).isSelected())
                            break;
                        c = left(c);
                    }
                    c.setSelected(local);
                    break;
                case 5:
                    while (c.getGravity() != 0)
                    {
                        if (topLeft(c).isSelected())
                            break;
                        c = topLeft(c);
                    }
                    c.setSelected(local);
                    break;
                case 6:
                    while (c.getGravity() != 0)
                    {
                        if (topRight(c).isSelected())
                            break;
                        c = topRight(c);
                    }
                    c.setSelected(local);
                    break;
                case 7:
                    while (c.getGravity() != 0)
                    {
                        if (bottomRight(c).isSelected())
                            break;
                        c = bottomRight(c);
                    }
                    c.setSelected(local);
                    break;
                case 8:
                    while (c.getGravity() != 0)
                    {
                        if (bottomLeft(c).isSelected())
                            break;
                        c = bottomLeft(c);
                    }
                    c.setSelected(local);
                    break;
            }
            Main.getController().myTurn = true;
            if (local)
            {
                GCP.writer.println(GCP.messageComposer(GCP.Codes.move, c.getX() + GCP.DELIMITER + c.getY()));
                Main.getController().myTurn = false;
            }
        }
        else
            Main.getController().alertManager("It is not your turn!!! Stop clicking this ****** table!!! ",1);
    }

    public static void applyGravity(Cell cell)
    {
        int r = Integer.parseInt(String.valueOf(cell.getId().charAt(0)));
        int c = Integer.parseInt(String.valueOf(cell.getId().charAt(1)));
        cell.setGravity(gravityMatrix[r][c]);
    }

    public static Cell top(Cell cell)
    {
        try{return Main.getController().getCells().get(getCellN(cell)-8);}
        catch (Exception e)
        {
            return new Cell(null);
        }
    }

    public static Cell bottom(Cell cell)
    {
        try{return Main.getController().getCells().get(getCellN(cell)+8);}
        catch (Exception e)
        {
            return new Cell(null);
        }
    }

    public static Cell left(Cell cell)
    {
        try{return Main.getController().getCells().get(getCellN(cell)-1);}
        catch (Exception e)
        {
            return new Cell(null);
        }
    }

    public static Cell right(Cell cell)
    {
        try{return Main.getController().getCells().get(getCellN(cell)+1);}
        catch (Exception e)
        {
            return new Cell(null);
        }
    }

    public static Cell topLeft(Cell cell)
    {
        return Main.getController().getCells().get(getCellN(cell)-9);
    }

    public static Cell topRight(Cell cell)
    {
        return Main.getController().getCells().get(getCellN(cell)-7);
    }

    public static Cell bottomLeft(Cell cell)
    {
        return Main.getController().getCells().get(getCellN(cell)+7);
    }

    public static Cell bottomRight(Cell cell)
    {
        return Main.getController().getCells().get(getCellN(cell)+9);
    }

    public static int getCellN(Cell cell)
    {
        return Integer.parseInt(String.valueOf(cell.getId().charAt(0)))*8+Integer.parseInt(String.valueOf(cell.getId().charAt(1)));
    }

    public static void paintCellService(List<String> payload)
    {
        int x = Integer.parseInt(payload.get(0));
        int y = Integer.parseInt(payload.get(1));
        cellHandler(Main.getController().getCellFromCoords(x,y), false);
    }
}
