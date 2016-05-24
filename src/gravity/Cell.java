package gravity;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;

public class Cell extends StackPane
{
    private boolean selected;
    private boolean local = true;
    private int gravity;
    private JFXButton b = new JFXButton();

    public Cell(String ID)
    {
        if (ID != null)
        {
            this.getChildren().add(b);
            b.setRipplerFill(Paint.valueOf("#3F51B5"));
            b.setOnAction((event) -> Util.cellHandler(this, true));
            this.setPrefSize(30, 30);
            b.setPrefSize(30, 30);
            this.setId(ID);
            this.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            Util.applyGravity(this);
        }
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean local)
    {
        Platform.runLater(() ->
        {
            this.setPadding(new Insets(-1));
            MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.ADJUST);
            icon.setSize("25px");
            this.getChildren().add(icon);
            if (!local)
            {
                icon.setFill(Paint.valueOf(Util.remoteColor.substring(2, 8)));
                icon.setGlyphName(String.valueOf(MaterialDesignIcon.CLOSE));
                this.local = false;
            } else
                icon.setFill(Paint.valueOf(Util.localColor.substring(2, 8)));
            b.setDisable(true);
            selected = true;
        });
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

    public String getX()
    {
        return this.getId().charAt(0)+"";
    }

    public String getY()
    {
        return this.getId().charAt(1)+"";
    }
}
