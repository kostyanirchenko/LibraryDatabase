package Controllers.views.menu;

import Controllers.Main;
import Models.Books;
import Models.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.controlsfx.dialog.Dialogs;

import java.io.IOException;

/**
 * Класс-контроллер для работы с меню
 * Created by Kostya Nirchenko on 06.07.2015.
 */
public class MenuController {

    private Main main;

    public void setMain(Main main) {
        this.main = main;
    }

    @FXML
    private void handleNew() {
        Books tmp = new Books();
        boolean addClicked = main.showBooksAddDialog(tmp);
        if(addClicked) {
            main.getBooks().add(tmp);
        }
    }

    @FXML
    private void handleSave() {

    }

    @FXML
    private void handleDelete() {

    }

    public void handleEdit(ActionEvent actionEvent) {

    }

    @FXML
    private void handleAbout() {
        Dialogs.create()
                .title("База данных персональной библиотеки")
                .masthead("О программе")
                .message("Программа создана студентом 3-го курса " +
                        "Экономико-технологического техникума Херсонского национального технического университета " +
                        "Нырченко Константином Сергеевичем как задание по учебной практике.\n" +
                        "В программе использованы технологии JDBC, MySQL, JavaFX \n" +
                        "Вебсайт: http://vk.com/ettkntu \n" +
                        "Приятного использования \n" +
                        "© Нырченко К.С 2015")
                .showInformation();
    }

    @FXML
    private void handleExit() {
        Database.closeConnection();
        Database.close();
        main.close();
    }
}
