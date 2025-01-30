# LockItOut - Credential Management Tool

LockItOut is a command-line tool for securely managing credentials in a MySQL database. It allows users to add, delete, modify, and retrieve stored credentials, making it easy to manage login information across multiple platforms.

## Features

- **Add new credentials** (`-a` / `--add`)

- **Delete credentials** (`-d` / `--delete`)

- **Show stored credentials** (`-sc` / `--show-credentials`)

- **List all platforms** (`-spf` / `--show-platforms`)

- **Modify stored credentials** (`-mu`, `-me`, `-mp`)

- **Truncate database** (`-t` / `--empty`)

## Installation

Ensure you have:

- **Java 17+**

- **MySQL Database**

- **Apache Commons CLI** and **SLF4J** dependencies

To build the project:

## Usage

### **Authentication Parameters (Required)**

| Option                      | Description               |
| --------------------------- | ------------------------- |
| `-db, --database <name>`    | Specify the database name |
| `-ul, --user <db_user>`     | MySQL database username   |
| `-pl, --pass <db_password>` | MySQL database password   |

### **Operations**

| Option                               | Description                                                             |
| ------------------------------------ | ----------------------------------------------------------------------- |
| `-a, --add`                          | Add new credentials (requires `-pf`, `-e`, `-u`, `-p`)                  |
| `-d, --delete`                       | Delete credentials (requires `-pf` and either `-e` or `-u`)             |
| `-sa, --show-all`                    | Show all credentials stored in the database                             |
| `-spf, --show-platforms`             | List all platforms in the database                                      |
| `-sc, --show-credentials`            | Show credentials for a specific platform (requires `-pf` and `-e`/`-u`) |
| `-mu, --modify-username <old> <new>` | Modify a username                                                       |
| `-me, --modify-email <old> <new>`    | Modify an email                                                         |
| `-mp, --modify-password <old> <new>` | Modify a password                                                       |
| `-t, --empty`                        | Delete all stored credentials                                           |
| `-h, --help`                         | Show the help menu                                                      |

## Contributing

Feel free to open issues and submit pull requests to improve **LockItOut**.

## License

This project is licensed under the MIT License.
