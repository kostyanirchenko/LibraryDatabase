package Controllers.views.library.add;

import Models.Books;
import Models.Validate;
import Models.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Контроллер для добавления записи в базу данных.
 *
 * Created by Kostya Nirchenko.
 *
 * @since 06.07.2015
 */
public class AddController {

    public TextField nameField;
    public TextField authorField;
    public TextField yearField;
    public TextField genreField;
    public TextField publisherField;
    public TextField sizeField;
    public TextField pressmarkField;

    public Button addButton;
    public Button cancelButton;

    private Books books;
    private Stage addStage;
    private boolean addClicked = false;

    @FXML
    private void initialize() {

    }

    public void setAddStage(Stage addStage) {
        this.addStage = addStage;
    }

    public void setBooks(Books books) {
        this.books = books;
    }

    public boolean isAddClicked() { return addClicked; }

    /**
     * При нажатии на кнопку {@link #addButton} проверяет на валидность все поля {@link #inputValidate()} в окне {@link #addStage}
     * и устанавливает в {@link Books} новое значение, после чего генерирует запрос в {@link Database} на добавление данных в базу данных.
     * При ошибке в синтаксисе запроса данные будут добавлены в {@link Books}, но не будут добавлены в базу данных.
     *
     * @param actionEvent
     */
    public void addBookAction(ActionEvent actionEvent) {
        if(inputValidate()) {
            if(checkBooks()) {
                try {
                    String query = Database.setPreparedStatement("INSERT",
                            "books",
                            "?, ?, ?, ?, ?",
                            null,
                            null);
                    PreparedStatement statement = Database.getPreparedStatement(query, true);
                    statement.setInt(1, 0);
                    statement.setString(2, nameField.getText());
                    statement.setString(3, genreField.getText());
                    statement.setInt(4, Integer.parseInt(sizeField.getText()));
                    statement.setString(5, pressmarkField.getText());
                    statement.executeUpdate();
                    int generatedKey = -1;
                    ResultSet rs = statement.getGeneratedKeys();
                    if (rs.next()) {
                        generatedKey = rs.getInt(1);
                    }
                    rs.close();

                    Database.setStatement();
                    Database.setResultSet(Database.select("authors",
                            "authorName",
                            "'" + authorField.getText() + "'"));
                    ResultSet checkAuthors = Database.getResultSet();
                    if(checkAuthors.next()) {
                        int authorId = checkAuthors.getInt(1);
                        String authorsBooksQuery = Database.setPreparedStatement("INSERT",
                                "authorsbooks",
                                "?, ?, ?",
                                null,
                                null);
                        PreparedStatement authorsBooksStatement = Database.getPreparedStatement(authorsBooksQuery, false);
                        authorsBooksStatement.setInt(1, 0);
                        authorsBooksStatement.setInt(2, generatedKey);
                        authorsBooksStatement.setInt(3, authorId);
                        authorsBooksStatement.executeUpdate();
                        checkAuthors.close();
                    } else {
                        String authorsQuery = Database.setPreparedStatement("INSERT",
                                "authors",
                                "?, ?",
                                null,
                                null);
                        PreparedStatement authorsStatement = Database.getPreparedStatement(authorsQuery, true);
                        authorsStatement.setInt(1, 0);
                        authorsStatement.setString(2, authorField.getText());
                        authorsStatement.executeUpdate();
                        int generatedAuthorsId = -1;
                        ResultSet authorsRs = authorsStatement.getGeneratedKeys();
                        if (authorsRs.next()) {
                            generatedAuthorsId = authorsRs.getInt(1);
                        }
                        authorsRs.close();
                        String authorsBooksQuery = Database.setPreparedStatement("INSERT",
                                "authorsbooks",
                                "?, ?, ?",
                                null,
                                null);
                        PreparedStatement authorsBooksStatement = Database.getPreparedStatement(authorsBooksQuery, false);
                        authorsBooksStatement.setInt(1, 0);
                        authorsBooksStatement.setInt(2, generatedKey);
                        authorsBooksStatement.setInt(3, generatedAuthorsId);
                        authorsBooksStatement.executeUpdate();
                    }
                    Database.setStatement();
                    Database.setResultSet(Database.select("publishers",
                            "publisherName",
                            "'" + publisherField.getText() + "'"));
                    ResultSet checkPublishers = Database.getResultSet();
                    if(checkPublishers.next()) {
                        int publisherId = checkPublishers.getInt(1);
                        String booksPublishersQuery = Database.setPreparedStatement("INSERT",
                                "bookspublishers",
                                "?, ?, ?, ?",
                                null,
                                null);
                        PreparedStatement booksPublishersStatement = Database.getPreparedStatement(booksPublishersQuery, false);
                        booksPublishersStatement.setInt(1, 0);
                        booksPublishersStatement.setInt(2, Integer.parseInt(yearField.getText()));
                        booksPublishersStatement.setInt(3, generatedKey);
                        booksPublishersStatement.setInt(4, publisherId);
                        booksPublishersStatement.executeUpdate();
                        checkPublishers.close();
                    } else {
                        String publishersQuery = Database.setPreparedStatement("INSERT",
                                "publishers",
                                "?, ?",
                                null,
                                null);
                        PreparedStatement publisherStatement = Database.getPreparedStatement(publishersQuery, true);
                        publisherStatement.setInt(1, 0);
                        publisherStatement.setString(2, publisherField.getText());
                        publisherStatement.executeUpdate();
                        int publisherId = -1;
                        ResultSet publisherRs = publisherStatement.getGeneratedKeys();
                        while (publisherRs.next()) {
                            publisherId = publisherRs.getInt(1);
                        }
                        publisherRs.close();
                        String booksPublishersQuery = Database.setPreparedStatement("INSERT",
                                "bookspublishers",
                                "?, ?, ?, ?",
                                null,
                                null);
                        PreparedStatement booksPublishersStatement = Database.getPreparedStatement(booksPublishersQuery, false);
                        booksPublishersStatement.setInt(1, 0);
                        booksPublishersStatement.setInt(2, Integer.parseInt(yearField.getText()));
                        booksPublishersStatement.setInt(3, generatedKey);
                        booksPublishersStatement.setInt(4, publisherId);
                        booksPublishersStatement.executeUpdate();
                    }

                    books.setId(Integer.toString(generatedKey));
                    books.setName(nameField.getText());
                    books.setAuthor(authorField.getText());
                    books.setGenre(genreField.getText());
                    books.setYear(yearField.getText());
                    books.setPublisher(publisherField.getText());
                    books.setSize(sizeField.getText());
                    books.setPressmark(pressmarkField.getText());
                    addClicked = true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    Dialogs.create()
                            .title("Упс, что-то пошло не так")
                            .masthead("Что-то пошло не так")
                            .message("База данных не найдена. Пожалуйста, установите СУБД MS SQL и прочтите файл README.txt")
                            .showError();
                }
                addStage.close();
            }
        }
    }

    public void cancelAction(ActionEvent actionEvent) {
        addStage.close();
    }

    private boolean inputValidate() {
        String errorMessage = "";
        if(!Validate.fieldValidate(nameField.getText())) {
            errorMessage += "Нужно заполнить поле Название \n";
        }
        if(!Validate.fieldValidate(authorField.getText())) {
            errorMessage += "Нужно заполнить поле Автор \n";
        }
        if(!Validate.fieldValidate(genreField.getText())) {
            errorMessage += "Нужно заполнить поле Жанр \n";
        }
        if(!Validate.fieldValidate(yearField.getText()) || !Validate.numericFieldValidate(yearField.getText())) {
            errorMessage += "Нужно заполнить поле Год издания \n";
        }
        if(!Validate.fieldValidate(publisherField.getText())) {
            errorMessage += "Нужно заполнить поле Издатель \n";
        }
        if(!Validate.fieldValidate(sizeField.getText()) || !Validate.isNumeric(sizeField.getText())) {
            errorMessage += "Нужно заполнить поле Количество страниц \n";
        }
        if(!Validate.isNumeric(sizeField.getText())) {
            errorMessage += "В поле Количество страниц должно быть численное значение \n";
        }
        if(!Validate.isNumeric(yearField.getText())) {
            errorMessage += "Поле Год издания не должно содержать больше 4-х чисел \n";
        }
        if(!Validate.fieldValidate(pressmarkField.getText())) {
            errorMessage += "Нужно заполнить поле Шифр книги \n";
        }
        if(!Validate.numericFieldValidate(pressmarkField.getText())) {
            errorMessage += "Поле Шифр книги должно содержать не более 4-х символов";
        }
        try {
            Database.setResultSet(Database.select("books", "bookPressmark", pressmarkField.getText()));
            ResultSet resultSet = Database.getResultSet();
            while(resultSet.next()) {
                errorMessage += "Шифр книги " + pressmarkField.getText() + " уже используется " + "\n";
            }
        } catch (SQLException e) {

        }
        if(errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.create()
                    .title("Заполнены не все поля")
                    .masthead("Пожалуйста, заполните все поля")
                    .message(errorMessage)
                    .showError();
            return false;
        }
    }

    private boolean checkBooks() {
        String errorMessage = "";
        try {
            Database.setStatement();
            Database.setResultSet(Database.select("books", "bookName", "'" + nameField.getText() + "'"));
            ResultSet checkBooks = Database.getResultSet();
            while(checkBooks.next()) {
                errorMessage += "Книга с таким названием уже есть в базе данных \n";
                break;
            }
            checkBooks.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        if(errorMessage.length() == 0) {
            return true;
        } else {
            Dialogs.create()
                    .title("Заполнены не все поля")
                    .masthead("Пожалуйста, заполните все поля")
                    .message(errorMessage)
                    .showError();
            return false;
        }
    }
}
