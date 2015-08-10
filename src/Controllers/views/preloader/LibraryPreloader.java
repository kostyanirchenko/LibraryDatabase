package Controllers.views.preloader;

import Models.Database;
import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Kostya Nirchenko.
 *
 * Класс-предзагрузчик. Необходим для указания урла, логина и пароля для коннекта к БД.
 *
 * @since 08.08.2015
 */
public class LibraryPreloader extends Preloader {

    public PasswordField passField;
    public TextField loginField;
    public TextField urlField;
    public Button connectButton;
    public Button exitButton;
    public ProgressBar bar;
    Stage stage;

    /*private Scene createPreloader() {

    }*/

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
//        stage.setScene(createPreloader());
        stage.show();
    }

    public void handleProgressNotification(ProgressNotification progressNotification) {
        bar.setProgress(progressNotification.getProgress());
    }

    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if(stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
    }

    public void handleConnect(ActionEvent actionEvent) {
        if(!urlField.getText().isEmpty() && !urlField.getText().equals("")) {
            String url = urlField.getText();
            Database.setUrl(url);
            urlField.setStyle("-fx-control-border-color: green");
            if(!loginField.getText().isEmpty() && !loginField.getText().equals("")) {
                String login = loginField.getText();
                Database.setLogin(login);
                loginField.setStyle("-fx-control-border-color: green");
                if(!passField.getText().isEmpty() && !passField.getText().equals("")) {
                    String pass = passField.getText();
                    Database.setPass(pass);
                    passField.setStyle("-fx-control-border-color: green");
                } else {
                    passField.setStyle("-fx-control-border-color: red");
                }
            } else {
                loginField.setStyle("-fx-control-border-color: red");
            }
        } else {
            urlField.setStyle("-fx-control-border-color: red");
        }
    }

    public void handleExit(ActionEvent actionEvent) {
    }
}
