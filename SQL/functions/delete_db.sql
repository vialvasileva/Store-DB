CREATE OR REPLACE FUNCTION delete_db(db_name TEXT)
RETURNS VOID 
AS $$
BEGIN
	EXECUTE format('SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = %L', db_name);

	PERFORM dblink_connect('conn', 'dbname=postgres user=admin_user password=admin_password');

	PERFORM dblink_exec('conn', 'DROP DATABASE ' || quote_ident(db_name));

	PERFORM dblink_disconnect('conn');
END;
$$ LANGUAGE plpgsql;
