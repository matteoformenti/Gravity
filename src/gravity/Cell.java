package gravity;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.geometry.Insets;
import javafx.scene.paint.Paint;

public class Cell extends JFXButton
{
    private boolean selected;
    private boolean local = true;
    private int gravity;

    public Cell(String ID)
    {
        if (ID != null)
        {
            this.setRipplerFill(Paint.valueOf("#3F51B5"));
            this.setOnAction((event) -> Settings.cellHandler(this, true));
            this.setPrefSize(30, 30);
            this.setId(ID);
            this.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            Settings.applyGravity(this);
        }
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean local)
    {
        this.setPadding(new Insets(-1));
        if (!local)
        {
            this.setStyle("-fx-background-color: #"+Settings.remoteColor.substring(2,8)+";");
            this.local = false;
        }
        else
            this.setStyle("-fx-background-color: #"+Settings.localColor.substring(2,8)+";");
        this.setDisable(true);
        selected = true;
        this.getChildren().add(new MaterialDesignIconView(MaterialDesignIcon.CLOSE));
    }


    public boolean isLocal()
    {
        return local;
    }

    public int getGravity()
    {
        return gravity;
    }

    public void setGravity(int gravity)
    {
        this.gravity = gravity;
    }
}
