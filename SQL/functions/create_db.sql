CREATE OR REPLACE FUNCTION create_db(db_name TEXT)
RETURNS VOID 
AS $$
BEGIN
	IF EXISTS (SELECT 1 FROM pg_database WHERE datname = db_name) THEN
		RAISE EXCEPTION 'База данных "%" уже существует', db_name;
	ELSE
		PERFORM dblink_connect('conn', 'dbname=postgres user=admin_user password=admin_password');
	
		PERFORM dblink_exec('conn', 'CREATE DATABASE ' || quote_ident(db_name));

		PERFORM dblink_disconnect('conn');
	END IF;
END;
$$ LANGUAGE plpgsql;
