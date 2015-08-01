package Controllers.views.library.edit;

import Models.Books;
import Models.Database;
import Models.Validate;
import org.controlsfx.dialog.Dialogs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Kostya Nirchenko.
 *
 * @since 08.07.2015
 */
public class EditController {

    public Button cancelButton;
    public Button editButton;
    public TextField nameField;
    public TextField authorField;
    public TextField yearField;
    public TextField genreField;
    public TextField publisherField;
    public TextField sizeField;
    public TextField pressmarkField;

    private Books books;
    private Stage editStage;
    private boolean editClicked = false;
    private String editId;

    @FXML
    private void initialize() {

    }

    public void setEditStage(Stage editStage) {
        this.editStage = editStage;
    }

    public void setEditId(String editId) {
        this.editId = editId;
    }

    public void setBooks(Books books) {
        this.books = books;
        nameField.setText(books.getName());
        authorField.setText(books.getAuthor());
        genreField.setText(books.getGenre());
        yearField.setText(books.getYear());
        publisherField.setText(books.getPublisher());
        sizeField.setText(books.getSize());
        pressmarkField.setText(books.getPressmark());
    }

    public boolean isEditClicked() {
        return editClicked;
    }

    public void handleEdit(ActionEvent actionEvent) {
        if(inputValidate()) {
            try {
                String oldAuthor = books.getAuthor();
                String newAuthor = authorField.getText();
                String oldPublisher = books.getPublisher();
                String newPublisher = publisherField.getText();
                Database.setStatement();
                Database.setResultSet(Database.select("authors",
                        "authorName",
                        "'" + newAuthor.trim() + "'"));
                ResultSet authorsResultSet = Database.getResultSet();
                Database.setStatement();
                Database.setResultSet(Database.select("publishers",
                        "publisherName",
                        "'" + newPublisher.trim() + "'"));
                ResultSet publishersResultSet = Database.getResultSet();
                if(newAuthor.equals(oldAuthor)) {
                    if(!newPublisher.equals(oldPublisher)) {
                        int publisherId = 0;
                        if(publishersResultSet.next()) {
                            publisherId = publishersResultSet.getInt(1);
                            String publishersBooksQuery = Database.setPreparedStatement("UPDATE",
                                    "bookspublishers",
                                    "publisherId = " + "?",
                                    "bookId", this.editId);
                            PreparedStatement publishersBooksStatement = Database.getPreparedStatement(publishersBooksQuery, false);
                            publishersBooksStatement.setInt(1, publisherId);
                            publishersBooksStatement.executeUpdate();
                        } else {
                            String newPublishersQuery = Database.setPreparedStatement("INSERT",
                                    "publishers",
                                    "?, ?",
                                    null,
                                    null);
                            PreparedStatement newPublishersStatement = Database.getPreparedStatement(newPublishersQuery, true);
                            newPublishersStatement.setInt(1, 0);
                            newPublishersStatement.setString(2, publisherField.getText());
                            newPublishersStatement.executeUpdate();
                            ResultSet rs = newPublishersStatement.getGeneratedKeys();
                            if(rs.next()) {
                                publisherId = rs.getInt(1);
                                System.out.println(publisherId);
                            }
                            rs.close();
                            String newPublishersBooksQuery = Database.setPreparedStatement("UPDATE",
                                    "bookspublishers",
                                    "publisherId = " + "?",
                                    "bookId", this.editId);
                            PreparedStatement newPublishersBooksStatement = Database.getPreparedStatement(newPublishersBooksQuery, false);
                            newPublishersBooksStatement.setInt(1, publisherId);
                            newPublishersBooksStatement.executeUpdate();
                        }
                        publishersResultSet.close();
                                            }
                    if(!yearField.getText().equals(books.getYear())) {
                        String yearUpdateQuery = Database.setPreparedStatement("UPDATE", "bookspublishers",
                                "publishingYear = " + "?",
                                "bookId", this.editId);
                        PreparedStatement yearUpdateStatement = Database.getPreparedStatement(yearUpdateQuery, false);
                        yearUpdateStatement.setInt(1, Integer.parseInt(yearField.getText()));
                        yearUpdateStatement.executeUpdate();
                    }
                } else {
                    int authorId = -1;
                    if(authorsResultSet.next()) {
                        authorId = authorsResultSet.getInt(1);
                        String authorsBooksQuery = Database.setPreparedStatement("UPDATE",
                                "authorsbooks",
                                "authorId = " + "?",
                                "bookId", this.editId);
                        PreparedStatement authorsBooksStatement = Database.getPreparedStatement(authorsBooksQuery, false);
                        authorsBooksStatement.setInt(1, authorId);
                        authorsBooksStatement.executeUpdate();
                    } else {
                        String newAuthorQuery = Database.setPreparedStatement("INSERT",
                                "authors",
                                "?, ?",
                                null,
                                null);
                        PreparedStatement newAuthorStatement = Database.getPreparedStatement(newAuthorQuery, true);
                        newAuthorStatement.setInt(1, 0);
                        newAuthorStatement.setString(2, authorField.getText());
                        newAuthorStatement.executeUpdate();
                        ResultSet rs = newAuthorStatement.getGeneratedKeys();
                        if(rs.next()) {
                            authorId = rs.getInt(1);
                            System.out.println(authorId);
                        }
                        rs.close();
                        String newAuthorsBooksQuery = Database.setPreparedStatement("UPDATE",
                                "authorsbooks",
                                "authorId = " + "?",
                                "bookId",
                                this.editId);
                        PreparedStatement newAuthorsBooksStatement = Database.getPreparedStatement(newAuthorsBooksQuery, false);
                        newAuthorsBooksStatement.setInt(1, authorId);
                        newAuthorsBooksStatement.executeUpdate();
                    }
                    authorsResultSet.close();
                }
                String query = Database.setPreparedStatement("UPDATE", "books",
                                "bookName = " + "? " +
                                ", genre = " + "? " +
                                ", bookSize = " + "? " +
                                ", bookPressmark = " + "?", "bookId", this.editId);
                PreparedStatement statement = Database.getPreparedStatement(query, false);
                statement.setString(1, nameField.getText());
                statement.setString(2, genreField.getText());
                statement.setInt(3, Integer.parseInt(sizeField.getText()));
                statement.setString(4, pressmarkField.getText());
                statement.executeUpdate();
                books.setName(nameField.getText());
                books.setAuthor(authorField.getText());
                books.setGenre(genreField.getText());
                books.setYear(yearField.getText());
                books.setPublisher(publisherField.getText());
                books.setSize(sizeField.getText());
                books.setPressmark(pressmarkField.getText());
            } catch (SQLException e) {
                e.printStackTrace();
                Dialogs.create()
                        .title("Упс, что-то пошло не так")
                        .masthead("Что-то пошло не так")
                        .message("База данных не найдена. Пожалуйста, установите СУБД MS SQL и прочтите файл README.txt")
                        .showError();
            }
            editClicked = true;
            editStage.close();
        }
    }

    public void handleCancel(ActionEvent actionEvent) {
        editStage.close();
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
            while(resultSet.next() && (!books.getPressmark().equals(pressmarkField.getText()))) {
                errorMessage += "Шифр книги " + pressmarkField.getText() + " уже используется " + "\n";
            }
        } catch (SQLException e) {
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