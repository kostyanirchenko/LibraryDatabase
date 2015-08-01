package Controllers;

import Controllers.views.library.LibraryController;
import Controllers.views.library.add.AddController;
import Controllers.views.library.edit.EditController;
import Controllers.views.menu.MenuController;
import Models.Books;
import Models.Database;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Главный класс запуска приложения.
 * Created by Kostya Nirchenko.
 *
 * @since 06.07.2015
 */
public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<Books> books = FXCollections.observableArrayList();

    public Main() {

    }

    /**
     * Устанавливает соединение с базой данных при запуске приложения
     *
     * @param primaryStage - главное окно приложения
     * @throws Exception - исключение, в случае если загрузка прошла неудачно
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Database.setConnection();
            Database.setStatement();
            Database.setResultSet(Database.select("books", "", ""));
            ResultSet resultSet = Database.getResultSet();
            while (resultSet.next()) {
                Database.setStatement();
                Database.setResultSet(Database.select("bookspublishers", "bookId", resultSet.getString("bookId")));
                ResultSet publishersId = Database.getResultSet();
                String authorName = "";
                String publisherName = "";
                String publishingYear = "";
                while(publishersId.next()) {
                    Database.setStatement();
                    Database.setResultSet(Database.select("publishers", "publisherId", publishersId.getString("publisherId")));
                    ResultSet publishers = Database.getResultSet();
                    while(publishers.next()) {
                        publisherName = publishers.getString("publisherName");
                    }
                    publishers.close();
                    publishingYear = publishersId.getString("publishingYear");
                }
                publishersId.close();
                Database.setStatement();
                Database.setResultSet(Database.select("authorsbooks", "bookId", resultSet.getString("bookId")));
                ResultSet authorsId = Database.getResultSet();
                while(authorsId.next()) {
                    Database.setStatement();
                    Database.setResultSet(Database.select("authors", "authorId", authorsId.getString("authorId")));
                    ResultSet author = Database.getResultSet();
                    while(author.next()) {
                        authorName = author.getString("authorName");
                    }
                    author.close();
                }
                authorsId.close();
                books.add(new Books(
                                resultSet.getString("bookId"),
                                resultSet.getString("bookName"),
                                authorName,
                                resultSet.getString("genre"),
                                publishingYear,
                                publisherName,
                                resultSet.getString("bookSize"),
                                resultSet.getString("bookPressmark")
                        )
                );
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Dialogs.create()
                    .title("Упс, что-то пошло не так")
                    .masthead("Что-то пошло не так")
                    .message("База данных не найдена. Пожалуйста, установите СУБД MS SQL и прочтите файл README.txt")
                    .showError();
        }
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("База данных личной библиотеки");
        this.primaryStage.getIcons().add(new Image(/*"file:resources/images/1436962983_library.png"*/Main.class.getResourceAsStream("views/images/1436962983_library.png")));
        initRootLayout();
        showLibrary();
    }

    /**
     * Устанавливает родительский менеджер компановки и загружает меню
     */
    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("views/menu/Menu.fxml"));
            rootLayout = (BorderPane) loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            MenuController menuController = loader.getController();
            menuController.setMain(this);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch(IOException e) {

        }
    }

    /**
     * Устанавливает в родительский менеджер компановки табличное отображение и загружает контроллер {@link LibraryController}
     */
    public void showLibrary() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("views/library/Library.fxml"));
            AnchorPane library = (AnchorPane) loader.load();
            rootLayout.setCenter(library);
            LibraryController libraryController = loader.getController();
            libraryController.setMain(this);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает окно на изменение книги в базе данных {@link LibraryController#editBooksAction(ActionEvent)}
     * @param books - экземпляр класса {@link Books}
     * @return boolean - true (если нажата кнопка "Изменить" и загрузка ресурса и контроллера прошла успешно)
     */
    public boolean showBooksEditDialog(Books books, String editId) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("views/library/edit/EditBook.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage editStage = new Stage();
            editStage.setTitle("Редактирование");
            editStage.getIcons().add(new Image(/*"file:resources/images/1436962959_icon-136-document-edit.png"*/Main.class.getResourceAsStream("views/images/1436962959_icon-136-document-edit.png")));
            editStage.initModality(Modality.WINDOW_MODAL);
            editStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            editStage.setScene(scene);
            EditController editController = loader.getController();
            editController.setEditStage(editStage);
            editController.setBooks(books);
            editController.setEditId(editId);
            editStage.showAndWait();
            return editController.isEditClicked();
        } catch(IOException e) {
            return false;
        }
    }

    /**
     * Создает окно на добавление новой книги в базу данных {@link LibraryController#addBooksAction(ActionEvent)}
     * @param books - экземпляр класса {@link Books}
     * @return boolean - true (если нажата кнопка "Добавить" и загрузка ресурса и контроллера прошла успешно)
     */
    public boolean showBooksAddDialog(Books books) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("views/library/add/AddBook.fxml"));
            AnchorPane page = (AnchorPane) loader.load();
            Stage addStage = new Stage();
            addStage.setTitle("Добавление");
            addStage.getIcons().add(new Image(/*"file:resources/images/1436962917_icon-81-document-add.png"*/Main.class.getResourceAsStream("views/images/1436962917_icon-81-document-add.png")));
            addStage.initModality(Modality.WINDOW_MODAL);
            addStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            addStage.setScene(scene);
            AddController addController = loader.getController();
            addController.setAddStage(addStage);
            addController.setBooks(books);
            addStage.showAndWait();
            return addController.isAddClicked();
        } catch (IOException e) {
            return false;
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ObservableList<Books> getBooks() {
        return books;
    }

    public void setBooks(Books books) {
        this.books.setAll(books);
    }

    public void close() {
        primaryStage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }

}