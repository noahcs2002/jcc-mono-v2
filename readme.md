# Jas Creates Co Management System

## App

## API

## Maintenance
There are a few maintenance scripts provided for convenience.

- `rotate_jwt.sh` will rotate the JWT secret stored in the `application.properties` file for the v1 API. This should be done semi-regularly

## Database

### Adding new Permissions
- All stored procedures in the database belong to the `APP_USER` schema.
- To add a new permission to the system, run the stored proc `INSERT_PERMISSION()`. This procedure takes the following params:
    - `p\_key` : The key ('`CREATE_PRODUCT`')
    - `p\description` : The description ('`Creates a new product`')
    - `p\domain` : The domain ('`PRODUCT`')
    - `p\_role_name` : The name of the role to grant this to ('`Company Owner`')

- To apply all listed permissions to the default role (Company Owner), use the `ASSIGN_DEFAULT_PERMISSIONS` role to do so.

