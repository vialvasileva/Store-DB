CREATE OR REPLACE FUNCTION clear_table(name_table TEXT)
RETURNS VOID 
AS $$
BEGIN
	IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = name_table) THEN
		RAISE EXCEPTION 'Таблица с именем "%" не существует', name_table;
	ELSE
		EXECUTE format('DELETE FROM %I', name_table);
	END IF;
END;
$$ LANGUAGE plpgsql;
