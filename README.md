# Scissors Paper Rock Game

## This is a backend application for Scissor paper rock game.

### Functions (Player)
* Signup user
* Login user
* Logout user

### Functions (Game)
* play game
* Calculate win rate (total game count, total win count, total loss count, total draw count)
* Set winning percentage (only for admin account)

### Create an Admin account
* It is not possible to create an admin account from Frontend because it should be assigned to
only some users decided by administrator. So, to create an admin user, Postman should be used.
Please use below CURL to create one and then login with this account then Admin page will be displayed.
```
curl --location 'http://localhost:8080/api/v1/player/signup' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": string (email type),
    "firstName": string,
    "lastName": string,
    "password": string,
    "role": "ADMIN"
}'
```