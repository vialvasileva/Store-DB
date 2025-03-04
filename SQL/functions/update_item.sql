CREATE OR REPLACE FUNCTION update_item(
		item_id CHAR(3), 
		item_name TEXT, 
		item_price NUMERIC, 
		item_category TEXT, 
		item_amount INT) 
RETURNS VOID 
AS $$
BEGIN
	IF NOT EXISTS (SELECT 1 FROM items WHERE items.id = item_id) THEN
        RAISE EXCEPTION 'Товар с ID % не найден', item_id;
    END IF;

    UPDATE items 
    SET name = COALESCE(item_name, items.name),
        price = COALESCE(item_price, items.price),
        category = COALESCE(item_category, items.category),
        amount = COALESCE(item_amount, items.amount)
    WHERE items.id = item_id;
END;
$$ LANGUAGE plpgsql;
