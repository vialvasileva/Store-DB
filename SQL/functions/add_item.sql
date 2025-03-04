CREATE OR REPLACE FUNCTION add_item(
		item_id CHAR(3),
		item_name TEXT, 
		item_price NUMERIC, 
		item_category TEXT, 
		item_amount INT) 
RETURNS VOID 
AS $$
BEGIN
	IF EXISTS (SELECT 1 FROM items WHERE items.id = item_id) THEN
		RAISE EXCEPTION 'Товар с ID % уже существует', item_id;
    END IF;

    INSERT INTO items 
    VALUES (item_id, item_name, item_price, item_category, item_amount); 
END;
$$ LANGUAGE plpgsql;
