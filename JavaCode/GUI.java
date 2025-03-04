import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Класс GUI представляет графический интерфейс приложения БД Store:
 * <p> - Окно авторизации пользователя
 * <p> - Главное окно с таблицей товаров
 * <p> - Панель управления с кнопками
 * <p> - Взаимодействие с классом Database для выполнения SQL-запросов
 */
public class GUI {
    private static JFrame frame;  // Главное окно
    private static DefaultTableModel tableModel;  // Модель данных для таблицы


    /**
     * Конструктор GUI.
     * <p> Запускает окно авторизации при запуске приложения.
     */
    public GUI() {
        showLoginWindow();
    }


    /**
     * Создаёт и отображает окно авторизации пользователя.
     * <p> Если аутентификация успешна, открывается главное окно приложения.
     */
    private static void showLoginWindow() {
        // Настройки окна авторизации
        JFrame loginFrame = new JFrame("Авторизация");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(300, 150);
        loginFrame.setLayout(new GridBagLayout());

        // Отступы в пикселях
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Заголовок "Логин"
        JLabel userLabel = new JLabel("Логин:");
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        loginFrame.add(userLabel, gbc);

        // Поле "Логин"
        JTextField userField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        loginFrame.add(userField, gbc);

        // Заголовок "Пароль"
        JLabel passLabel = new JLabel("Пароль:");
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        loginFrame.add(passLabel, gbc);

        // Поле "Пароль"
        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        loginFrame.add(passField, gbc);

        // Кнопка "Войти"
        JButton loginButton = new JButton("Войти");
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginFrame.add(loginButton, gbc);

        // Обработчик нажатия кнопки "Войти"
        loginButton.addActionListener(_ -> {
            // Данные пользователя
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            // Авторизация
            if (Database.authenticate(user, pass)) {
                // Закрытие окна авторизации
                loginFrame.dispose();

                // Открытие главного окна
                showMainWindow();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Ошибка входа!");
            }
        });

        // Отображение окна авторизации
        loginFrame.setVisible(true);
    }


    /**
     * Создаёт и отображает главное окно приложения.
     * <p> Показывает таблицу товаров и панель управления (разную для админа и гостя).
     */
    private static void showMainWindow() {
        // Настройки главного окна
        frame = new JFrame("Store");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Создание панели кнопок
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3));

        // Создание кнопки поиска товара и добавление в панель
        JButton searchButton = new JButton("Поиск товаров");
        panel.add(searchButton);

        if (Database.isAdmin()) {
            // Создание кнопок управления для админа
            JButton addItemButton = new JButton("Добавить товар");
            JButton updateItemButton = new JButton("Обновить товар");
            JButton deleteItemButton = new JButton("Удалить товар");
            JButton createTableButton = new JButton("Создать таблицу");
            JButton clearTableButton = new JButton("Очистить таблицу");
            JButton createDbButton = new JButton("Создать БД");
            JButton deleteDbButton = new JButton("Удалить БД");
            JButton createUserButton = new JButton("Создать пользователя");
            JButton deleteUserButton = new JButton("Удалить пользователя");

            // Добавление кнопок в панель
            panel.add(addItemButton);
            panel.add(updateItemButton);
            panel.add(deleteItemButton);
            panel.add(createTableButton);
            panel.add(clearTableButton);
            panel.add(createDbButton);
            panel.add(deleteDbButton);
            panel.add(createUserButton);
            panel.add(deleteUserButton);

            // =============== Обработчики событий ===============

            // Кнопка "Добавить товар"
            addItemButton.addActionListener(_ -> {
                // Текстовые поля для ввода данных
                JTextField idField = new JTextField();
                JTextField nameField = new JTextField();
                JTextField priceField = new JTextField();
                JTextField categoryField = new JTextField();
                JTextField amountField = new JTextField();

                // Объект с полями для диалогового окна
                Object[] fields = {
                        "ID:", idField,
                        "Название:", nameField,
                        "Цена:", priceField,
                        "Категория:", categoryField,
                        "Количество:", amountField
                };

                // Диалоговое окно для ввода данных
                int result = JOptionPane.showConfirmDialog(frame, fields, "Добавить товар", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    // Сохранение данных пользователя
                    String id = idField.getText().trim();
                    String name = nameField.getText().trim();
                    Double price = null;
                    String category = categoryField.getText().trim().isEmpty() ? null : categoryField.getText().trim();
                    Integer amount = null;

                    try {
                        // Проверка, что ID не пустой
                        if (id.isEmpty()) {
                            JOptionPane.showMessageDialog(frame, "ID не может быть пустым!");
                            return;
                        }

                        // Проверка, что название не пустое
                        if (name.isEmpty()) {
                            JOptionPane.showMessageDialog(frame, "Название не может быть пустым!");
                            return;
                        }

                        // Проверка, что цена не отрицательная
                        if (!priceField.getText().trim().isEmpty()) {
                            price = Double.valueOf(priceField.getText().trim());
                            if (price < 0) {
                                JOptionPane.showMessageDialog(frame, "Цена не может быть отрицательной!");
                                return;
                            }
                        }

                        // Проверка, что количество не отрицательное
                        if (!amountField.getText().trim().isEmpty()) {
                            amount = Integer.valueOf(amountField.getText().trim());
                            if (amount < 0) {
                                JOptionPane.showMessageDialog(frame, "Количество товара не может быть отрицательным!");
                                return;
                            }
                        }

                        // Добавление товара
                        Database.addItem(id, name, price, category, amount);
                        JOptionPane.showMessageDialog(frame, "Товар успешно добавлен!");
                    }
                    catch (NumberFormatException e) {
                        // Ошибка ввода чисел
                        JOptionPane.showMessageDialog(frame, "Некорректный формат числа.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                    catch (SQLException e) {
                        // Ошибка выполнения операции
                        JOptionPane.showMessageDialog(frame, e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }

                // Обновление таблицы
                loadTable();
            });

            // Кнопка "Обновить товар"
            updateItemButton.addActionListener(_ -> {
                // Текстовые поля для ввода данных
                JTextField idField = new JTextField();
                JTextField nameField = new JTextField();
                JTextField priceField = new JTextField();
                JTextField categoryField = new JTextField();
                JTextField amountField = new JTextField();

                // Объект с полями для диалогового окна
                Object[] fields = {
                        "ID:", idField,
                        "Название:", nameField,
                        "Цена:", priceField,
                        "Категория:", categoryField,
                        "Количество:", amountField
                };

                // Диалоговое окно для ввода данных
                int result = JOptionPane.showConfirmDialog(frame, fields, "Изменить товар", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    // Сохранение данных пользователя
                    String id = idField.getText().trim();
                    String name = nameField.getText().trim().isEmpty() ? null : nameField.getText().trim();
                    Double price = null;
                    String category = categoryField.getText().trim().isEmpty() ? null : categoryField.getText().trim();
                    Integer amount = null;

                    try {
                        // Проверка, что ID не пустой
                        if (id.isEmpty()) {
                            JOptionPane.showMessageDialog(frame, "ID не может быть пустым!");
                            return;
                        }

                        // Проверка, что цена не отрицательная
                        if (!priceField.getText().trim().isEmpty()) {
                            price = Double.valueOf(priceField.getText().trim());
                            if (price < 0) {
                                JOptionPane.showMessageDialog(frame, "Цена не может быть отрицательной!");
                                return;
                            }
                        }

                        // Проверка, что количество не отрицательное
                        if (!amountField.getText().trim().isEmpty()) {
                            amount = Integer.valueOf(amountField.getText().trim());
                            if (amount < 0) {
                                JOptionPane.showMessageDialog(frame, "Количество товара не может быть отрицательным!");
                                return;
                            }
                        }

                        // Обновление товара
                        Database.updateItem(id, name, price, category, amount);
                        JOptionPane.showMessageDialog(frame, "Товар успешно обновлен!");
                    }
                    catch (NumberFormatException e) {
                        // Ошибка ввода чисел
                        JOptionPane.showMessageDialog(frame, "Некорректный формат числа.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                    catch (SQLException e) {
                        // Ошибка выполнения операции
                        JOptionPane.showMessageDialog(frame, e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }

                // Обновление таблицы
                loadTable();
            });

            // Кнопка "Удалить товар"
            deleteItemButton.addActionListener(_ -> {
                // Окно для ввода названия товара
                String name = JOptionPane.showInputDialog(frame, "Введите название товара для удаления:");

                // Пользователь нажал "Cancel"
                if (name == null) {
                    return;
                }

                // Пользователь нажал "OK" и ничего не ввел
                if (name.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Название товара не может быть пустым!");
                    return;
                }

                try {
                    // Удаление товара по названию
                    Database.deleteItemByName(name.trim());
                    JOptionPane.showMessageDialog(frame, "Товар \"" + name + "\" успешно удалён.");
                }
                catch (SQLException e) {
                    // Ошибка выполнения операции
                    JOptionPane.showMessageDialog(frame, e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }

                // Обновление таблицы
                loadTable();
            });

            // Кнопка "Создать таблицу"
            createTableButton.addActionListener(_ -> {
                // Текстовые поля для ввода данных
                JTextField nameField = new JTextField();
                JTextField structureField = new JTextField();

                // Объект с полями для диалогового окна
                Object[] fields = {
                        "Название:", nameField,
                        "Структура:", structureField,
                };

                // Диалоговое окно для ввода данных
                int result = JOptionPane.showConfirmDialog(frame, fields, "Создать таблицу", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    // Сохранение данных пользователя
                    String name = nameField.getText().trim();
                    String structure = structureField.getText().trim();

                    // Проверка, что название или структура не пустые
                    if (name.isEmpty() || structure.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Название и структура таблицы не могут быть пустыми!");
                        return;
                    }

                    try {
                        // Создание таблицы
                        Database.createTable(name, structure);
                        JOptionPane.showMessageDialog(frame, "Таблица \"" + name + "\" успешно создана.");
                    }
                    catch (SQLException e) {
                        // Ошибка выполнения операции
                        JOptionPane.showMessageDialog(frame, e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Кнопка "Очистить таблицу"
            clearTableButton.addActionListener(_ -> {
                // Окно для ввода названия таблицы
                String tableName = JOptionPane.showInputDialog(frame, "Введите название таблицы для очищения:");

                // Пользователь нажал "Cancel"
                if (tableName == null) {
                    return;
                }

                // Пользователь нажал "OK" и ничего не ввел
                if (tableName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Название таблицы не может быть пустым!");
                    return;
                }

                // Подтверждение очищения
                int confirm = JOptionPane.showConfirmDialog(frame, "Вы уверены?", "Очистка таблицы " + tableName, JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        // Очищение таблицы
                        Database.clearTable(tableName.trim());
                        JOptionPane.showMessageDialog(frame, "Таблица \"" + tableName + "\" успешно очищена.");
                    }
                    catch (SQLException e) {
                        // Ошибка выполнения операции
                        JOptionPane.showMessageDialog(frame, e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }

                // Обновление таблицы
                loadTable();
            });

            // Кнопка "Создать БД"
            createDbButton.addActionListener(_ -> {
                // Окно для ввода названия БД
                String dbName = JOptionPane.showInputDialog(frame, "Введите название новой базы данных:");

                // Пользователь нажал "Cancel"
                if (dbName == null) {
                    return;
                }

                // Пользователь нажал "OK" и ничего не ввел
                if (dbName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Название базы данных не может быть пустым!");
                    return;
                }

                try {
                    // Создание БД
                    Database.createDatabase(dbName.trim());
                    JOptionPane.showMessageDialog(frame, "База данных \"" + dbName + "\" успешно создана.");
                }
                catch (SQLException e) {
                    // Ошибка выполнения операции
                    JOptionPane.showMessageDialog(frame, e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Кнопка "Удалить БД"
            deleteDbButton.addActionListener(_ -> {
                // Окно для ввода названия БД
                String dbName = JOptionPane.showInputDialog(frame, "Введите название базы данных для удаления:");

                // Пользователь нажал "Cancel"
                if (dbName == null) {
                    return;
                }

                // Пользователь нажал "OK" и ничего не ввел
                if (dbName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Название базы данных не может быть пустым!");
                    return;
                }

                // Подтверждение удаления
                int confirm = JOptionPane.showConfirmDialog(frame, "Вы уверены?", "Удаление базы данных " + dbName, JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        // Удаление БД
                        Database.deleteDatabase(dbName.trim());
                        JOptionPane.showMessageDialog(frame, "База данных \"" + dbName + "\" успешно удалена.");
                    }
                    catch (SQLException e) {
                        // Ошибка выполнения операции
                        JOptionPane.showMessageDialog(frame, e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Кнопка "Создать пользователя"
            createUserButton.addActionListener(_ -> {
                // Варианты ролей для создания пользователя
                Map<String, String> roleMap = new LinkedHashMap<>();
                roleMap.put("Админ", "admin");
                roleMap.put("Гость", "guest");

                // Выборные и текстовые поля для ввода данных
                JComboBox<String> roleBox = new JComboBox<>(roleMap.keySet().toArray(new String[0]));
                JTextField userNameField = new JTextField();
                JTextField passwordField = new JTextField();

                // Объект с полями для диалогового окна
                Object[] fields = {
                        "Роль:", roleBox,
                        "Имя пользователя:", userNameField,
                        "Пароль:", passwordField
                };

                // Диалоговое окно для ввода данных
                int result = JOptionPane.showConfirmDialog(frame, fields, "Создать пользователя", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    // Сохранение данных пользователя
                    String username = userNameField.getText().trim();
                    String password = passwordField.getText().trim();
                    String selectedRole = roleMap.get((String) roleBox.getSelectedItem());

                    // Проверка, что логин или пароль не пустые
                    if (username.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Имя пользователя и пароль не могут быть пустыми!");
                        return;
                    }

                    try {
                        // Создание пользователя
                        Database.createUser(selectedRole, username, password);
                        JOptionPane.showMessageDialog(frame, "Пользователь \"" + userNameField.getText() + "\" успешно добавлен.");
                    }
                    catch (SQLException e) {
                        // Ошибка выполнения операции
                        JOptionPane.showMessageDialog(frame, e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Кнопка "Удалить пользователя"
            deleteUserButton.addActionListener(_ -> {
                // Окно для ввода имени пользователя
                String username = JOptionPane.showInputDialog(frame, "Введите имя пользователя для удаления:");

                // Пользователь нажал "Cancel"
                if (username == null) {
                    return;
                }

                // Пользователь нажал "OK" и ничего не ввел
                if (username.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Имя пользователя не может быть пустым!");
                    return;
                }

                // Подтверждение удаления
                int confirm = JOptionPane.showConfirmDialog(frame, "Вы уверены?", "Удаление пользователя " + username, JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        // Удаление пользователя
                        Database.deleteUser(username.trim());
                        JOptionPane.showMessageDialog(frame, "Пользователь \"" + username + "\" успешно удалён.");
                    }
                    catch (SQLException e) {
                        // Ошибка выполнения операции
                        JOptionPane.showMessageDialog(frame, e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }

        // Кнопка "Поиск товара"
        searchButton.addActionListener(_ -> {
            // Окно для ввода названия товара
            String name = JOptionPane.showInputDialog(frame, "Введите название товара для поиска:");

            // Пользователь нажал "Cancel"
            if (name == null) {
                return;
            }

            // Пользователь нажал "OK" и ничего не ввел
            if (name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Название товара не может быть пустым!");
                return;
            }

            // Модель таблицы для отображения результатов поиска
            DefaultTableModel searchTableModel = new DefaultTableModel();
            searchTableModel.setColumnIdentifiers(new String[]{"ID", "Название", "Цена", "Категория", "Количество"});

            // Поиск товаров
            try (ResultSet rs = Database.searchItemByName(name.trim())) {
                // Флаг наличия результатов
                boolean hasResults = false;

                // Добавление найденных товаров в таблицу
                while (rs.next()) {
                    // Обновление флага
                    hasResults = true;

                    searchTableModel.addRow(new Object[]{
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getBigDecimal("price"),
                            rs.getString("category"),
                            rs.getInt("amount")
                    });
                }

                // Сообщение, что товары не найдены
                if (!hasResults) {
                    JOptionPane.showMessageDialog(frame, "Товары не найдены!");
                    return;
                }
            } catch (SQLException e) {
                // Ошибка выполнения операции
                JOptionPane.showMessageDialog(frame, "Ошибка обработки данных: " + e.getMessage());
            }

            // Новое окно для отображения результатов поиска
            JFrame resultFrame = new JFrame("Результаты поиска");
            resultFrame.setSize(600, 400);
            resultFrame.setLayout(new BorderLayout());

            // Добавление таблицы результатов в окно
            JTable searchTable = new JTable(searchTableModel);
            JScrollPane scrollPane = new JScrollPane(searchTable);
            resultFrame.add(scrollPane, BorderLayout.CENTER);

            // Отображение окна
            resultFrame.setVisible(true);
        });

        // Настройки таблицы
        tableModel = new DefaultTableModel();
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Добавление панели кнопок и таблицы в окно
        frame.add(panel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Отображение главного окна
        frame.setVisible(true);

        // Отображение таблицы БД
        loadTable();
    }


    /**
     * Загружает данные из базы данных и отображает их в таблице.
     */
    private static void loadTable() {
        // Очистка текущих строк в таблице
        tableModel.setRowCount(0);

        // Установка заголовков колонок в таблице
        tableModel.setColumnIdentifiers(new String[]{"ID", "Название", "Цена", "Категория", "Количество"});

        // Отображение таблицы
        try (ResultSet rs = Database.showTable("items")) {
            // Флаг наличия результатов
            boolean hasResults = false;

            // Добавление найденных товаров в таблицу
            while (rs.next()) {
                // Обновление флага
                hasResults = true;

                tableModel.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getBigDecimal("price"),
                        rs.getString("category"),
                        rs.getInt("amount")
                });
            }

            // Сообщение, что данные не найдены
            if (!hasResults) {
                JOptionPane.showMessageDialog(frame, "Данные не найдены!");
            }
        } catch (SQLException e) {
            // Ошибка выполнения операции
            JOptionPane.showMessageDialog(frame, "Ошибка обработки данных: " + e.getMessage());
        }
    }
}
