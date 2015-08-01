package Controllers.views.library;

import Controllers.Main;
import Models.Books;
import Models.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.controlsfx.dialog.Dialogs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс-контроллер для работы с TableView.
 *
 * Created by Kostya Nirchenko.
 *
 * @since 06.07.2015
 */
public class LibraryController {

    public TableColumn<Books, String> nameColumn;
    public TableColumn<Books, String> authorColumn;
    public TableColumn<Books, String> genreColumn;
    public TableColumn<Books, String> yearColumn;
    public TableColumn<Books, String> publisherColumn;
    public TableColumn<Books, String> sizeColumn;
    public TableColumn<Books, String> pressmarkColumn;
    public TableView<Books> libraryTable;

    private Main main;

    private ObservableList<String> elements = FXCollections.observableArrayList("Названию", "Автору", "Шифру");

    public Button exitButton;
    public ComboBox<String> searchElements;
    public TextField searchField;
    public Button addBooksButton;
    public Button editBooksButton;
    public Button deleteBooksButton;

    public LibraryController() {

    }

    /**
     * Выполняет действие по нажатию на кноку {@link #exitButton}
     *
     * @param actionEvent
     */
    public void exitAction(ActionEvent actionEvent) {
        Database.close();
        Database.closeConnection();
        main.close();
    }

    /**
     * Выполняет действие по нажатию на кноку {@link #addBooksButton}
     *
     * @param actionEvent
     */
    public void addBooksAction(ActionEvent actionEvent) {
        Books tempBooks = new Books();
        boolean addClicked = main.showBooksAddDialog(tempBooks);
        if(addClicked) {
            main.getBooks().add(tempBooks);
            Dialogs.create()
                    .title("Добавление данных")
                    .masthead("Операция прошла успешно")
                    .message("Данные успешно добавлены!")
                    .showInformation();
        }
    }

    /**
     * Выполняет действие по нажатию на кноку {@link #editBooksButton}
     *
     * @param actionEvent
     */
    public void editBooksAction(ActionEvent actionEvent) {
        Books selectedBooks = libraryTable.getSelectionModel().getSelectedItem();
        String editId = libraryTable.getSelectionModel().getSelectedItem().getId();
        if(selectedBooks != null) {
            boolean editClicked = main.showBooksEditDialog(selectedBooks, editId);
            if(editClicked) {
                Dialogs.create()
                        .title("Редактирование")
                        .masthead("Операция прошла успешно")
                        .message("Данные успешно изменены!")
                        .showInformation();
            }
        } else {
            Dialogs.create()
                    .title("Элемент не выбран")
                    .masthead("Не выбрана книга")
                    .message("Пожалуйста, выберите книгу в таблице.")
                    .showWarning();
        }
    }

    /**
     * Выполняет действие по нажатию на кноку {@link #deleteBooksButton}
     *
     * @param actionEvent
     */
    public void deleteBooksAction(ActionEvent actionEvent) {
        int selectedIndex = libraryTable.getSelectionModel().getSelectedIndex();
        String selectedId = libraryTable.getSelectionModel().getSelectedItem().getId();
        String book = "";
        if(selectedIndex >= 0) {
            try {
                String authorId = "";
                String publisherId = "";
                Database.setStatement();
                Database.setResultSet(Database.select("bookspublishers", "bookId", selectedId));
                ResultSet pub = Database.getResultSet();
                while(pub.next()) {
                    publisherId = pub.getString("publisherId");
                }
                pub.close();
                Database.setStatement();
                Database.setResultSet(Database.select("authorsbooks", "bookId", selectedId));
                ResultSet rs = Database.getResultSet();
                while(rs.next()) {
                    authorId = rs.getString("authorId");
                }
                rs.close();
                String publisherQuery = Database.setPreparedStatement("DELETE","publishers", null, "publisherId", "?");
                PreparedStatement publisher = Database.getPreparedStatement(publisherQuery, false);
                publisher.setString(1, publisherId);
                publisher.executeUpdate();
                String query = Database.setPreparedStatement("DELETE", "books", null, "bookId", "?");
                PreparedStatement statement = Database.getPreparedStatement(query, false);
                statement.setString(1, selectedId);
                statement.executeUpdate();
                String authorQuery = Database.setPreparedStatement("DELETE", "authors", null, "authorId", "?");
                PreparedStatement author = Database.getPreparedStatement(authorQuery, false);
                author.setString(1, authorId);
                author.executeUpdate();
                Database.setStatement();
                Database.setResultSet(Database.select("books", "bookId", selectedId));
                ResultSet bookRs = Database.getResultSet();
                while(bookRs.next()) {
                    book = bookRs.getString("bookName");
                }
                bookRs.close();
                main.getBooks().remove(selectedIndex);
                Dialogs.create()
                        .title("Удалено")
                        .masthead("Книга " + book + " успешно удалена из базы данных")
                        .message("Удаление элемента " + book + " и всех связаных с ним записей успешно произведено.")
                        .showInformation();
            } catch(SQLException e) {
                e.printStackTrace();
                Dialogs.create()
                        .title("Упс, что-то пошло не так")
                        .masthead("Что-то пошло не так")
                        .message("База данных не найдена. Пожалуйста, установите СУБД MS SQL и прочтите файл README.txt")
                        .showError();
            }
        } else {
            Dialogs.create()
                    .title("Упс, книг то ведь уже нет")
                    .masthead("Не выбрана книга для удаления")
                    .message("Пожалуйста, выберите книгу в таблице")
                    .showWarning();
        }
    }

    /**
     * Инициализирует поле поиска в TableView.
     */
    @FXML
    public void initialize() {
        searchElements.setItems(elements);
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().genreProperty());
        yearColumn.setCellValueFactory(cellData -> cellData.getValue().yearProperty());
        publisherColumn.setCellValueFactory(cellData -> cellData.getValue().publisherProperty());
        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        pressmarkColumn.setCellValueFactory(cellData -> cellData.getValue().pressmarkProperty());
    }

    /**
     * Устанавливает сортировку по вводу в текстовое поле для таблицы
     *
     * @param bookses - список кнги для сортировки
     * @return SortedList - отсортированый список
     */
    private SortedList<Books> getSortedData(ObservableList<Books> bookses) {
        FilteredList<Books> filteredData = new FilteredList<>(bookses, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(books -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseData = newValue.toLowerCase();
                if (books.getName().toLowerCase().contains(lowerCaseData)) {
                    return true;
                } else if (books.getPublisher().toLowerCase().contains(lowerCaseData)) {
                    return true;
                }
                return false;
            });
        });
        // установим сортированый список
        SortedList<Books> sortedList = new SortedList<>(filteredData);
        sortedList.comparatorProperty().bind(libraryTable.comparatorProperty());
        return sortedList;
    }

    /**
     * Устанавливает главный класс, а так же устанавливает сортированый список в таблицу
     *
     * @param main
     */
    public void setMain(Main main) {
        this.main = main;
        libraryTable.setItems(getSortedData(main.getBooks()));
    }

    public TableView<Books> getLibraryTable() {
        return libraryTable;
    }
}