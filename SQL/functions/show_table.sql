CREATE OR REPLACE FUNCTION show_table(name_table TEXT)
RETURNS TABLE(
		id CHAR(3),
		name TEXT,
		price NUMERIC(10,2),
		category TEXT,
		amount INT)
AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = name_table) THEN
        RAISE EXCEPTION 'Таблица "%" не существует', name_table;
    END IF;

    RETURN QUERY EXECUTE format('SELECT * FROM %I ORDER BY CAST(items.id AS INTEGER)', name_table);
END;
$$ LANGUAGE plpgsql;
