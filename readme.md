# Jas Creates Co Management System

## App

## API
- You will need to obtain the application.properties file for your machine from another developer. This has secret information it it, and is required for the API layer to run, including DB connection info, JWT secrets, and connection strings.

## Database
### Adding new Permissions
- All stored procedures in the database belong to the `APP_USER` schema.
- See the file in the `sql` folder for information on how to add a new permission. In addition to using that file, you need to add it to the PermissionKeys.java file as well.

- To apply all listed permissions to the default role (Company Owner), use the `ASSIGN_DEFAULT_PERMISSIONS` role to do so.

