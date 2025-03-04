CREATE OR REPLACE FUNCTION create_table(name_table TEXT, columns_structure TEXT)
RETURNS VOID
AS $$
BEGIN
	IF EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = name_table) THEN
		RAISE EXCEPTION 'Таблица с именем "%" уже существует', name_table;
	ELSE
    	EXECUTE format('CREATE TABLE %I (%s)', name_table, columns_structure);
	END IF;
END;
$$ LANGUAGE plpgsql;
