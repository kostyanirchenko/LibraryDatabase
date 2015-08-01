package Models;

import java.sql.*;

/**
 * Класс для организации работы с базой данных.
 *
 * Содержит методы для получения данных из БД, обновления данных, удаления.
 *
 * Created by Kostya Nirchenko.
 *
 * @since 06.07.2015
 */
public class Database {
    public static Connection connection;
    public static final String url = "jdbc:mysql://localhost:3306/test";
    public static Statement statement;
    public static ResultSet resultSet;
    public static PreparedStatement preparedStatement;

    /**
     * Проверяет, установлено ли соединение с базой данных.
     *
     * @return boolean - флаг, установлено ли соединение
     */
    public static boolean checkConnection() {
        if(connection == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Устанавливает соединение с базой данных по заданому URL, логину и паролю.
     */
    public static void setConnection() {
        String url = "jdbc:mysql://localhost:3306/test";
        String login = "root";
        String password = "nirchenkokostya";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, login, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static Statement getStatement() {
        return statement;
    }

    /**
     * Устанавливает запрос к базе данных.
     */
    public static void setStatement() {
        try {
            if(checkConnection()) {
                statement = connection.createStatement();
            }
            else {
                return;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Устанавливает подготовленный запрос к базе данных.
     *
     * @param type - тип запроса (insert, update, delete)
     * @param table - таблица в базе данных
     * @param param - набор параметров
     * @param field - поле (используется для обновления и удаления) для проверки условия
     * @param line - условие
     * @return String query - строка с запросом
     * @throws SQLException - исключение, в случае если неверный набор параметров
     */
    public static String setPreparedStatement(String type, String table, String param, String field, String line) throws SQLException {
        String query = "";
        if(type.equals("INSERT")) {
            query += type + " INTO " + table + " VALUE (" + param + ")";
        }
        if(type.equals("UPDATE")) {
            query += type + " " + table + " SET " + param + " WHERE " + field + " = " + line;
        }
        if(type.equals("DELETE")) {
            query += type + " FROM " + table + " WHERE " + field + " = " + line;
        }
//        preparedStatement = connection.prepareStatement(query);
        return query;
    }

    /**
     * Устанавливает подготовленный запрос к базе данных. Предусматривает возможность возвращения сгенерированого БД ключа.
     * Для получения сгенерированого первичного ключа из БД необходимо указать {@param getGeneratedKeys} true. Если параметр указан false -
     * устанавливается подготовленный запрос без возвращения сгенерированого ключа.
     *
     * @param query - строка-запрос (генерируется в {@link Database#select(String, String, String)})
     * @param getGeneratedKeys - логическое значение для получения сгенерированого БД первичного ключа (true - вернуть, false - нет)
     * @return PreparedStatement - установленный параметризированый запрос
     * @throws SQLException - исключение, в случае, если ошибочный набор параметров запроса, несоответствие указаной в запросе таблицы и ее полей.
     */
    public static PreparedStatement getPreparedStatement(String query, boolean getGeneratedKeys) throws SQLException {
        if(!query.isEmpty()) {
            if(!getGeneratedKeys) {
                preparedStatement = connection.prepareStatement(query);
                return preparedStatement;
            } else {
                preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                return preparedStatement;
            }
        } else {
            throw new SQLException("Не указана строка запроса");
        }
    }

    /**
     * Устанавливает запрос к базе данных.
     *
     * @param query - строка с запросом
     * @throws SQLException - исключение, в случае если неверные параметры запроса
     */
    public static void setResultSet(String query) throws SQLException {
        if(!query.isEmpty()) {
            resultSet = statement.executeQuery(query);
        } else {
            throw new SQLException("Не указана строка запроса");
        }
    }

    public static ResultSet getResultSet() {
        return resultSet;
    }

    /**
     * Создает строку-запрос на выборку из указаной таблицы.
     * Для выбора всех значений следует передать методу следующее: select({@param table}, "", ""); где: {@param table} - необходимая таблица
     * Если необходимо выбрать из БД записи, в которых определенный столбец содержит определенное значение,
     * следует передать методу следующее: select({@param table}, {@param field}, {@param element}); где: {@param table} - необходимая таблица, {@param field} - поле для поиска, {@param element} - элемент для поиска
     *
     * @param table - строка с именем таблицы в базе данных
     * @param field - поле для поиска
     * @param element - предмет поиска
     * @return String select - строка с запросом
     */
    public static String select(String table, String field, String element) {
        String select = "";
        if(!table.isEmpty()) {
            if(field.equals("") && element.equals("")) {
                select = "SELECT * FROM " + table;
                return select;
            } else {
                select = "SELECT * FROM " + table + " WHERE " + field + " = " /*+ "'"*/ + element /*+ "'"*/;
                return select;
            }
        }
        return select;
    }

    /**
     * Закрывает все запросы к базе данных.
     */
    public static void close() {
        if(statement != null) {
            try {
                statement.close();
            } catch (SQLException ignore) {

            }
        }
        if(preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException ignore) {

            }
        }
        if(resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException ignore) {

            }
        }
    }

    /**
     * Закрывает соединение с базой данных.
     */
    public static void closeConnection() {
        if(connection != null) {
            try {
                connection.close();
            } catch (SQLException ignore) {

            }
        }
    }
}