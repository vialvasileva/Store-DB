CREATE OR REPLACE FUNCTION delete_user(username TEXT)
RETURNS VOID 
AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = username) THEN
        RAISE EXCEPTION 'Пользователь "%" не существует', username;
    END IF;

    PERFORM pg_terminate_backend(pid) FROM pg_stat_activity WHERE usename = username;

    EXECUTE format('DROP USER %I', username);
END;
$$ LANGUAGE plpgsql;
