CREATE OR REPLACE FUNCTION search_item_by_name(item_name TEXT)
RETURNS TABLE(
		id CHAR(3),
		name TEXT,
		price NUMERIC(10,2),
		category TEXT,
		amount INT) 
AS $$
BEGIN
    RETURN QUERY SELECT * FROM items 
	WHERE items.name ILIKE '%' || item_name || '%';
END;
$$ LANGUAGE plpgsql;
