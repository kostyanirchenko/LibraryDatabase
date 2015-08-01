package Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Kostya Nirchenko.
 *
 * @since 06.07.2015
 */
public class Books {

    private StringProperty id;
    private StringProperty name;
    private StringProperty author;
    private StringProperty genre;
    private StringProperty year;
    private StringProperty publisher;
    private StringProperty size;
    private StringProperty pressmark;

    public Books() {
        this(null, null, null, null, null, null, null, null);
    }

    public Books(String id, String name, String author, String genre,  String year, String publisher, String size, String pressmark) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.author = new SimpleStringProperty(author);
        this.genre = new SimpleStringProperty(genre);
        this.year = new SimpleStringProperty(year);
        this.publisher = new SimpleStringProperty(publisher);
        this.size = new SimpleStringProperty(size);
        this.pressmark = new SimpleStringProperty(pressmark);
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getAuthor() {
        return author.get();
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public StringProperty authorProperty() {
        return author;
    }

    public String getYear() {
        return year.get();
    }

    public void setYear(String year) {
        this.year.set(year);
    }

    public StringProperty yearProperty() {
        return year;
    }

    public String getGenre() { return genre.get(); }

    public void setGenre(String genre) { this.genre.set(genre); }

    public StringProperty genreProperty() { return genre; }

    public String getPublisher () { return publisher.get(); }

    public void setPublisher(String publisher) { this.publisher.set(publisher); }

    public StringProperty publisherProperty() {return publisher; }

    public String getSize() { return size.get(); }

    public void setSize(String size) {this.size.set(size); }

    public StringProperty sizeProperty() { return size; }

    public String getPressmark() {
        return pressmark.get();
    }

    public void setPressmark(String pressmark) {
        this.pressmark.set(pressmark);
    }

    public StringProperty pressmarkProperty() {
        return pressmark;
    }

}