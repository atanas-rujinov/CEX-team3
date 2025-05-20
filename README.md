1. DELETE /account
   
| ID    | Description                         | Preconditions                                                | Steps                                                                            | Expected Result                                            |
| ----- | ----------------------------------- | ------------------------------------------------------------ | -------------------------------------------------------------------------------- | ---------------------------------------------------------- |
| DA-01 | **Happy**: correct password deletes | User `alice` exists with password `secret` (encrypted in DB) | 1. `DELETE /account`  <br>2. Body: `{ "username":"alice", "password":"secret" }` | 204 No Content; user `alice` no longer in database         |
| DA-02 | **Negative**: wrong password        | Same as DA-01                                                | 1. `DELETE /account`  <br>2. Body: `{ "username":"alice", "password":"wrong" }`  | 401 Unauthorized; account **not** deleted                  |
| DA-03 | **Negative**: non-existent user     | No user `bob`                                                | 1. `DELETE /account`  <br>2. Body: `{ "username":"bob", "password":"anything" }` | 404 Not Found (or 401 if your API masks “not found”)       |
| DA-04 | **Negative**: missing fields        | –                                                            | 1. `DELETE /account`  <br>2. Body: `{ "username":"alice" }`                      | 400 Bad Request; error JSON explaining “password required” |


2. POST /account/edit-username

| ID    | Description                                | Preconditions                               | Steps                                                                                                  | Expected Result                                                |
| ----- | ------------------------------------------ | ------------------------------------------- | ------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------- |
| EU-01 | **Happy**: change to unused username       | User `alice` exists; new `bob` does **not** | 1. `POST /account/edit-username`  <br>2. Body: `{ "currentUsername":"alice", "newUsername":"bob" }`    | 200 OK; verify in GET/user or DB that `alice` → `bob`          |
| EU-02 | **Negative**: new username already exists  | Both `alice` and `charlie` exist            | 1. `POST /account/edit-username` <br>2. Body: `{ "currentUsername":"alice", "newUsername":"charlie" }` | 409 Conflict; JSON `error: "Username already in use"`          |
| EU-03 | **Negative**: current user not found       | No user `dave`                              | 1. `POST /account/edit-username` <br>2. Body: `{ "currentUsername":"dave", "newUsername":"davo" }`     | 404 Not Found                                                  |
| EU-04 | **Negative**: invalid new username (empty) | User `alice` exists                         | 1. `POST /account/edit-username` <br>2. Body: `{ "currentUsername":"alice", "newUsername":"" }`        | 400 Bad Request; JSON `error: "newUsername must not be empty"` |


3. POST /account/login

| ID    | Description                       | Preconditions                          | Steps                                                                                | Expected Result                                               |
| ----- | --------------------------------- | -------------------------------------- | ------------------------------------------------------------------------------------ | ------------------------------------------------------------- |
| LI-01 | **Happy**: valid credentials      | User `alice` exists, password `secret` | 1. `POST /account/login`  <br>2. Body: `{ "username":"alice", "password":"secret" }` | 200 OK; JSON `{ "message":"Login successful", "token": "…" }` |
| LI-02 | **Negative**: wrong password      | User `alice` exists                    | 1. `POST /account/login`  <br>2. Body: `{ "username":"alice", "password":"wrong" }`  | 401 Unauthorized; JSON `error: "Invalid credentials"`         |
| LI-03 | **Negative**: user does not exist | No user `eve`                          | 1. `POST /account/login`  <br>2. Body: `{ "username":"eve", "password":"anything" }` | 404 Not Found (or 401 to avoid user enumeration)              |
| LI-04 | **Negative**: missing fields      | –                                      | 1. `POST /account/login`  <br>2. Body: `{ "username":"alice" }`                      | 400 Bad Request; JSON `error: "password required"`            |


4. POST /account/edit-password

| ID    | Description                                                  | Preconditions                           | Steps                                                                                                                     | Expected Result                                                    |
| ----- | ------------------------------------------------------------ | --------------------------------------- | ------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------ |
| EP-01 | **Happy**: correct current password → change to new          | User `alice` exists, old password `old` | 1. `POST /account/edit-password`  <br>2. Body: `{ "username":"alice","currentPassword":"old","newPassword":"newSecret" }` | 200 OK; verify in DB that `alice`’s password hash changed          |
| EP-02 | **Negative**: wrong current password                         | User `alice` exists                     | 1. `POST /account/edit-password`  <br>2. Body: `{ "username":"alice","currentPassword":"bad","newPassword":"newSecret" }` | 401 Unauthorized; no change in DB                                  |
| EP-03 | **Negative**: new password fails validation (e.g. too short) | User `alice` exists                     | 1. `POST /account/edit-password`  <br>2. Body: `{ "username":"alice","currentPassword":"old","newPassword":"123" }`       | 400 Bad Request; JSON `error: "Password must be at least 8 chars"` |
| EP-04 | **Negative**: missing fields                                 | –                                       | 1. `POST /account/edit-password`  <br>2. Body: `{ "username":"alice","currentPassword":"old" }`                           | 400 Bad Request; JSON `error: "newPassword required"`              |


5. POST /account/signup

| ID    | Description                          | Preconditions           | Steps                                                                                      | Expected Result                                                               |
| ----- | ------------------------------------ | ----------------------- | ------------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------- |
| SU-01 | **Happy**: register new user         | No user `frank` exists  | 1. `POST /account/signup`  <br>2. Body: `{ "username":"frank","password":"GoodPass123!" }` | 201 Created; JSON `{ "username":"frank" }`; check DB hash                     |
| SU-02 | **Negative**: username already taken | User `alice` exists     | 1. `POST /account/signup`  <br>2. Body: `{ "username":"alice","password":"Whatever" }`     | 409 Conflict; JSON `error: "Username already taken"`                          |
| SU-03 | **Negative**: password too weak      | No user `george` exists | 1. `POST /account/signup`  <br>2. Body: `{ "username":"george","password":"123" }`         | 400 Bad Request; JSON `error: "Password does not meet strength requirements"` |
| SU-04 | **Negative**: missing fields         | –                       | 1. `POST /account/signup`  <br>2. Body: `{ "username":"harry" }`                           | 400 Bad Request; JSON `error: "password required"`                            |


























