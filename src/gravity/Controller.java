package gravity;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.List;

public class Controller
{
    public MaterialDesignIconView resizeImage;
    public AnchorPane topBar;
    public GridPane grid;
    public AnchorPane gridContainer;
    public JFXTextField usernameLabel;
    public JFXDialog dialog;
    public StackPane mainPane;
    public JFXDialogLayout dialogLayout;
    public Pane dialogPane;
    public StackPane centerStackPane;

    private double x;
    private double y;
    private boolean fullscreen = false;
    private List<Cell> cells = new ArrayList<>();
    public List<JFXDialog> dialogs = new ArrayList<>();
    public boolean myTurn = false;

    public void init()
    {
        dialogLayout.setPadding(new Insets(-1));
        dialog.setTransitionType(JFXDialog.DialogTransition.CENTER);
        dialog.show(centerStackPane);
        dialog.setOverlayClose(false);
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
    }

    public void close(ActionEvent actionEvent)
    {
        if (GCP.writer != null)
            GCP.writer.println(GCP.messageComposer(GCP.Codes.exit, "null"));
        System.exit(0);
    }

    public void resize(ActionEvent actionEvent)
    {
        if (!fullscreen)
        {
            Main.getStage().setWidth(Screen.getPrimary().getVisualBounds().getWidth());
            Main.getStage().setHeight(Screen.getPrimary().getVisualBounds().getHeight());
            resizeImage.setGlyphName("WINDOW_RESTORE");
            Main.getStage().centerOnScreen();
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
        dialog.setOverlayClose(true);
    }

    public void spawnBoard()
    {
        cells.removeAll(cells);
        gridContainer.getChildren().remove(grid);
        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setCenterShape(true);
        gridContainer.getChildren().add(grid);
        gridContainer.setTopAnchor(grid, 50.0);
        gridContainer.setBottomAnchor(grid, 50.0);
        gridContainer.setLeftAnchor(grid, 50.0);
        gridContainer.setRightAnchor(grid, 50.0);
        grid.setGridLinesVisible(true);
        for (int r = 0; r < 8; r++)
        {
            grid.getRowConstraints().add(new RowConstraints(30,30,Double.MAX_VALUE));
            for (int c = 0; c < 8; c++)
            {
                Cell cell = new Cell(r+""+c);
                cells.add(cell);
                grid.add(cell, c, r);
            }
        }
    }

    public void chooseColor(ActionEvent actionEvent)
    {
        JFXColorPicker picker = (JFXColorPicker)actionEvent.getSource();
        Util.localColor = picker.valueProperty().getValue().toString();
    }

    /**
     * Sends a connection request to the server
     * @param actionEvent Click event
     */
    public void connect(ActionEvent actionEvent)
    {
        if (usernameLabel.getText().length() > 0)
        {
            GCP.startGCP();
            Util.localUser = usernameLabel.getText();
            GCP.login();
        }
        else
            dialogManager("Please, input a username!", false);
    }

    public List<Cell> getCells()
    {
        return cells;
    }

    public JFXDialog dialogManager(String msg, boolean overlayClose)
    {
        JFXDialog d = new JFXDialog();
        JFXDialogLayout l = new JFXDialogLayout();
        d.setContent(l);
        l.setBody(new Label(msg));
        JFXButton closed = new JFXButton("OK");
        closed.setOnAction((event -> d.close()));
        l.setActions(closed);
        d.setTransitionType(JFXDialog.DialogTransition.CENTER);
        d.show(centerStackPane);
        d.setOverlayClose(overlayClose);
        dialogs.add(d);
        dialog.setId("no-id");
        return d;
    }

    public JFXDialog loadingDialog(String msg, String id, boolean overlayClose)
    {
        JFXDialog d = new JFXDialog();
        JFXDialogLayout l = new JFXDialogLayout();
        d.setContent(l);
        VBox box = new VBox();
        box.setFillWidth(true);
        box.setSpacing(20);
        box.getChildren().addAll(new Label(msg), new JFXSpinner());
        box.setAlignment(Pos.CENTER);
        l.setBody(box);
        d.setOverlayClose(overlayClose);
        d.setTransitionType(JFXDialog.DialogTransition.CENTER);
        d.show(centerStackPane);
        d.setId(id);
        dialogs.add(d);
        return d;
    }

    public JFXDialog matchOverDialog(String msg, String id)
    {
        JFXDialog d = new JFXDialog();
        JFXDialogLayout l = new JFXDialogLayout();
        d.setContent(l);
        HBox box = new HBox();
        box.setSpacing(20);
        l.setBody(new Label(msg));
        JFXButton b1 = new JFXButton("Close");
        b1.setOnAction((event -> Main.getController().close(null)));
        JFXButton b2 = new JFXButton("New match");
        b2.setOnAction((event -> {clearBoard(); GCP.writer.println(GCP.messageComposer(GCP.Codes.matchmaker, "null")); d.close();}));
        box.getChildren().addAll(b1, b2);
        l.setActions(box);
        d.setOverlayClose(false);
        d.setTransitionType(JFXDialog.DialogTransition.CENTER);
        d.show(centerStackPane);
        d.setId(id);
        dialogs.add(d);
        return d;
    }

    public void clearBoard()
    {
        cells.removeAll(cells);
        gridContainer.getChildren().remove(grid);
        grid = new GridPane();
    }
}
