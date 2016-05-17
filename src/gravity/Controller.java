package gravity;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
    private Task<Void> serverListener;
    private JFXDialog waitingMatchDialog;
    /**
     * This method is called for components initialization
     */
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

    /**
     * This method is called when the user closes the program
     */
    public void close(ActionEvent actionEvent)
    {
        System.out.println(serverListener.isRunning());
        if (serverListener.isRunning())
            serverListener.cancel();
        GCP.writer.println(GCP.messageComposer(GCP.Codes.exit, "null"));
        Main.getStage().close();
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

    public void connect(ActionEvent actionEvent)
    {
        if (usernameLabel.getText().length() > 0)
        {
            Util.localUser = usernameLabel.getText();
            Util.gcp = new GCP();
            switch (Util.gcp.login())
            {
                case 1:
                    dialog.close();
                    waitingMatchDialog = loadingDialog("Waiting for a match", false);
                    startMatchMaker();
                    break;
                case -1:
                    alertManager("You tried to steal a username! Shame on you! Retry with a real username", 2);
                    break;
                case -2:
                    alertManager("Congratulations! You managed to break the whole code, we have no idea of what has happened... try asking the supercow what to do now...", 4);
                    break;
                case -3:
                    alertManager("I think we know what has happened... nobody wanna talk to you, neither our server!", 4);
                    break;
            }
        }
        else
            alertManager("Please, input a username!!", 1).setOverlayClose(false);
    }

    public void startMatchMaker()
    {
        GCP.writer.println(GCP.messageComposer(GCP.Codes.matchmaker, "null"));
        Task<Void> waitMatch = new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                GCP.Message msg = GCP.decodeIncoming();
                while (!msg.code.equals(GCP.Codes.match))
                    msg = GCP.decodeIncoming();
                Util.remoteColor = msg.payload.get(1);
                Util.remoteUser = msg.payload.get(0);
                Main.getController().startMatch();
                return null;
            }
        };
        Thread matchWaiter = new Thread(waitMatch);
        matchWaiter.start();
    }

    public void startMatch()
    {
        serverListener = new Task<Void>()
        {
            @Override
            protected Void call() throws Exception
            {
                GCP.Message msg = Util.gcp.decodeIncoming();
                while (!msg.code.equals(GCP.Codes.exit))
                {
                    if (msg.code.equals(GCP.Codes.move))
                        Util.paintCellService(msg.payload);
                    msg = Util.gcp.decodeIncoming();
                }
                return null;
            }
        };
        Thread t = new Thread(serverListener);
        t.start();
        waitingMatchDialog.close();
        System.out.println("start match close");
        Platform.runLater(() ->alertManager("You're in a match with "+ Util.remoteUser, 1));
        System.out.println("start match alert manager");
        Platform.runLater(()->spawnBoard());
        System.out.println("start match spawn board");
    }

    public List<Cell> getCells()
    {
        return cells;
    }

    public JFXDialog alertManager(String msg, int level)
    {
        JFXDialog alert = new JFXDialog();
        JFXDialogLayout alertLayout = new JFXDialogLayout();
        alert.setContent(alertLayout);
        alertLayout.setBody(new Label(msg));
        JFXButton closeAlert = new JFXButton("OK");
        closeAlert.setOnAction((event -> alert.close()));
        alertLayout.setActions(closeAlert);
        alert.setTransitionType(JFXDialog.DialogTransition.CENTER);
        alert.show(centerStackPane);
        return alert;
    }

    public JFXDialog loadingDialog(String msg, boolean overlayClose)
    {
        JFXDialog loading = new JFXDialog();
        JFXDialogLayout loadingLayout = new JFXDialogLayout();
        loading.setContent(loadingLayout);
        loadingLayout.setBody(new JFXSpinner());
        loadingLayout.setHeading(new Label(msg));
        loading.setOverlayClose(overlayClose);
        loading.setTransitionType(JFXDialog.DialogTransition.CENTER);
        loading.show(centerStackPane);
        return loading;
    }

    public Cell getCellFromCoords(int x, int y)
    {
        return cells.get((x*8)+y);
    }
}
