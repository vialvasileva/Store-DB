import java.math.BigDecimal;
import java.sql.*;


/**
 * Класс Database предоставляет методы для взаимодействия с базой данных PostgresSQL:
 * <p> - Аутентификация пользователя
 * <p> - Выполнение хранимых функций
 * <p> - Вызов SQL-функций
 */
public class Database {
    private static final String URL = "jdbc:postgresql://localhost:5432/store";  // Подключение к БД "store"
    private static String currentUser;
    private static String currentPassword;
    private static boolean isAdmin = false;


    /**
     * Аутентифицирует пользователя, проверяя возможность подключения к базе данных.
     * <p> Если аутентификация успешна, сохраняет учетные данные текущего пользователя.
     *
     * @param user     Имя пользователя
     * @param password Пароль пользователя
     * @return true, если аутентификация успешна, иначе false
     */
    public static boolean authenticate(String user, String password) {
        Connection conn = null;
        try {
            // Подключение к БД
            conn = DriverManager.getConnection(URL, user, password);

            // Текущие учетные данные
            currentUser = user;
            currentPassword = password;

            // Проверка роли пользователя
            isAdmin = password.startsWith("admin");

            // Успешная аутентификация
            return true;
        } catch (SQLException e) {
            // Ошибка при подключении
            return false;
        } finally {
            // Закрытие соединения
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }


    /**
     * Проверяет, является ли текущий пользователь администратором.
     *
     * @return true, если пользователь администратор, иначе false
     */
    public static boolean isAdmin() {
        return isAdmin;
    }


    /**
     * Выполняет хранимую функцию в базе данных, передавая ей параметры.
     * <p> Используется для вызова SQL-функций, которые не возвращают результат.
     *
     * @param query  Имя хранимой функции с параметрами
     * @param params Массив параметров, передаваемых в функцию
     * @throws SQLException Если возникает ошибка при выполнении SQL-запроса
     */
    private static void executeStoredFunction(String query, Object... params) throws SQLException {
        // Подключение к БД и подготовка запроса через SELECT
        try (Connection conn = DriverManager.getConnection(URL, currentUser, currentPassword);
             PreparedStatement stmt = conn.prepareStatement("SELECT " + query)) {

            // Заполнение параметров запроса
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            // Выполнение запроса
            stmt.execute();
        }
    }


    /**
     * Выполняет хранимую функцию в базе данных, передавая ей параметры.
     * <p> Используется для вызова SQL-функций, которые возвращают таблицу.
     *
     * @param query  Имя хранимой функции с параметром
     * @param param  Значение параметра, передаваемого в функцию
     * @return ResultSet с результатом запроса или null при ошибке
     */
    private static ResultSet executeTableQuery(String query, String param) {
        try {
            // Подключение к БД
            Connection conn = DriverManager.getConnection(URL, currentUser, currentPassword);

            // Подготовка запроса через SELECT
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + query);

            // Заполнение параметров запроса
            stmt.setString(1, param);

            // Выполнение запроса
            return stmt.executeQuery();

        } catch (SQLException e) {
            // Ошибка при выполнении запроса
            System.out.println(e.getMessage());
            return null;
        }
    }


    // =============== Взаимодействие с БД через хранимые функции ================

    // Добавить товар
    public static void addItem(String id, String name, Double price, String category, Integer amount) throws SQLException {
        executeStoredFunction("add_item(?, ?, ?, ?, ?)", id, name, price != null ? BigDecimal.valueOf(price) : null, category, amount);
    }

    // Обновить товар
    public static void updateItem(String id, String name, Double price, String category, Integer amount) throws SQLException {
        executeStoredFunction("update_item(?, ?, ?, ?, ?)", id, name, price != null ? BigDecimal.valueOf(price) : null, category, amount);
    }

    // Удалить товар
    public static void deleteItemByName(String name) throws SQLException {
        executeStoredFunction("delete_item_by_name(?)", name);
    }

    // Найти товар по названию
    public static ResultSet searchItemByName(String name) throws SQLException {
        return executeTableQuery("search_item_by_name(?)", name);
    }

    // Создать таблицу
    public static void createTable(String tableName, String columnsStructure) throws SQLException {
        executeStoredFunction("create_table(?, ?)", tableName, columnsStructure);
    }

    // Очистить таблицу
    public static void clearTable(String tableName) throws SQLException {
        executeStoredFunction("clear_table(?)", tableName);
    }

    // Показать таблицу
    public static ResultSet showTable(String tableName) throws SQLException {
        return executeTableQuery("show_table(?)", tableName);
    }

    // Создать БД
    public static void createDatabase(String dbName) throws SQLException {
        executeStoredFunction("create_db(?)", dbName);
    }

    // Удалить БД
    public static void deleteDatabase(String dbName) throws SQLException {
        executeStoredFunction("delete_db(?)", dbName);
    }

    // Создать пользователя
    public static void createUser(String role, String username, String password) throws SQLException {
        executeStoredFunction("create_user(?, ?, ?)", role, username, password);
    }

    // Удалить пользователя
    public static void deleteUser(String username) throws SQLException {
        executeStoredFunction("delete_user(?)", username);
    }
}
