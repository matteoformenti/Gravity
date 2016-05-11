package gravity;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application
{
    public static void main(String args[]){launch(args);}

    private static Controller controller;
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layouts/MainLayout.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root, 600, 400));
        stage.initStyle(StageStyle.UNDECORATED);
        controller.init();
        stage.show();
    }

    public static Controller getController()
    {
        return controller;
    }

    public static Stage getStage()
    {
        return stage;
    }
}
