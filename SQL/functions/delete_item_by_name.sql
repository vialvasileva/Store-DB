CREATE OR REPLACE FUNCTION delete_item_by_name(item_name TEXT)
RETURNS VOID 
AS $$
BEGIN
	IF NOT EXISTS (SELECT 1 FROM items WHERE items.name = item_name) THEN
        RAISE EXCEPTION 'Товар "%" не найден', item_name;
    END IF;

    DELETE FROM items 
	WHERE name = item_name;
END;
$$ LANGUAGE plpgsql;
