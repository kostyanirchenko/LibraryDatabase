package Models;

/**
 * Класс, реализующий базовую валидацию.
 *
 * Created by Kostya Nirchenko.
 *
 * @since 13.07.2015
 */
public class Validate {

    /**
     * Проверяет, заполнены ли текстовые поля в формах на добавление и изменение данных в БД.
     * Текстовые поля необходимо передавать методу, применив метод getText().
     *
     * @param field - текстовое поле для проверки
     * @return boolean - true (в случае если проверка успешна), false (если поля не заполнены, либо имеют пустую строку)
     */
    public static boolean fieldValidate(String field) {
        if(field.length() == 0 || field == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Проверяет на длинну текстовые поля, информация из которых заносится в базу данных в столбцы,
     * ограниченные четырьмя символами (например bookYear, bookPressmark).
     * Текстовые поля необходимо передавать методу, применив метод getText().
     *
     * @param field - текстовое поле для проверки
     * @return boolean - true (в случае если проверка успешна), false (если длинна символов в строке превышает 4 символа)
     */
    public static boolean numericFieldValidate(String field) {
        if(field.length() > 4) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Проверяет на валидность поля, в которых должны быть численные значения.
     * Текстовые поля необходимо передавать методу, применив метод getText().
     *
     * @param field - текстовое поле для проверки
     * @return boolean - true (если текстовое поле содержит числа), false (если встречается хотя бы один символ)
     */
    public static boolean isNumeric(String field) {
        if (field == null || field.length() == 0) return false;

        int i = 0;
        if (field.charAt(0) == '-') {
            if (field.length() == 1) {
                return false;
            }
            i = 1;
        }

        char c;
        for (; i < field.length(); i++) {
            c = field.charAt(i);
            if (!(c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }
}
