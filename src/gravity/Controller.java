package gravity;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.List;

public class Controller
{
    public MaterialDesignIconView resizeImage;
    public AnchorPane topBar;
    public GridPane grid;
    public AnchorPane gridContainer;
    public JFXTextField serverIPLabel;
    public JFXTextField usernameLabel;
    public JFXDialog dialog;
    public StackPane mainPane;
    public JFXDialogLayout dialogLayout;
    public Pane dialogPane;

    private double x;
    private double y;
    private boolean fullscreen = false;
    private List<JFXButton> buttons = new ArrayList<>();
    private List<RowConstraints> rowConstraints = new ArrayList<>();

    public void init()
    {
        dialogLayout.setPadding(new Insets(-1));
        topBar.setOnMousePressed((event) ->
        {
                if (event.getButton() == MouseButton.PRIMARY) {
                    x = event.getSceneX();
                    y = event.getSceneY();
                }
        });

        topBar.setOnMouseDragged((event) ->
        {
                if (event.getButton() == MouseButton.PRIMARY) {
                    Main.getStage().setX(event.getScreenX() - x);
                    Main.getStage().setY(event.getScreenY() - y);
                }
        });
        populateBoard();
    }

    public void close(ActionEvent actionEvent)
    {
        Main.getStage().close();
    }

    public void resize(ActionEvent actionEvent)
    {
        if (!fullscreen)
        {
            Main.getStage().setWidth(Screen.getPrimary().getVisualBounds().getWidth());
            Main.getStage().setHeight(Screen.getPrimary().getVisualBounds().getHeight());
            resizeImage.setGlyphName("WINDOW_RESTORE");
            fullscreen = true;
        }
        else
        {
            Main.getStage().setWidth(600);
            Main.getStage().setHeight(400);
            Main.getStage().centerOnScreen();
            resizeImage.setGlyphName("WINDOW_MAXIMIZE");
            fullscreen = false;
        }
    }

    public void iconify(ActionEvent actionEvent)
    {
        Main.getStage().setIconified(true);
    }

    public void showMenu(ActionEvent actionEvent)
    {
        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        dialog.show(mainPane);
    }

    public void populateBoard()
    {
        grid.setGridLinesVisible(true);
        for (int r = 0; r < 8; r++)
        {
            grid.getRowConstraints().add(new RowConstraints(30,30,Double.MAX_VALUE));
            for (int c = 0; c < 8; c++)
            {
                JFXButton button = new JFXButton("");
                button.setRipplerFill(Paint.valueOf("#3F51B5"));
                button.setId(r + "-" + c);
                button.setPrefSize(30,30);
                button.setOnAction((event) -> System.out.println(event.getSource()));
                buttons.add(button);
                grid.add(button, c, r);
            }
        }
    }

    public void isServerBox(ActionEvent actionEvent)
    {
    }

    public void chooseColor(ActionEvent actionEvent)
    {
    }

    public void connect(ActionEvent actionEvent)
    {
    }
}