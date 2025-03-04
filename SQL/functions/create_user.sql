CREATE OR REPLACE FUNCTION create_user(user_role TEXT, user_name TEXT, user_password TEXT) 
RETURNS VOID 
AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = user_name) THEN
        RAISE EXCEPTION 'Пользователь "%" уже существует', user_name;
    END IF;

    EXECUTE format('CREATE USER %I WITH PASSWORD %L', user_name, user_password);

    IF user_role = 'admin' THEN
        EXECUTE format('GRANT admin_user TO %I', user_name);
    ELSIF user_role = 'guest' THEN
        EXECUTE format('GRANT guest_user TO %I', user_name);
    END IF;
END;
$$ LANGUAGE plpgsql;
