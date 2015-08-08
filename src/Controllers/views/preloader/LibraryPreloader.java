package Controllers.views.preloader;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

/**
 * Created by Kostya Nirchenko.
 *
 * Класс-предзагрузчик. Необходим для указания урла, логина и пароля для коннекта к БД.
 *
 * @since 08.08.2015
 */
public class LibraryPreloader extends Preloader {

    ProgressBar bar;
    Stage stage;

    private Scene createPreloader() {

    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.setScene(createPreloader());
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
}
