# Airline Management System

A Java desktop project for airline booking and operations, backed by SQLite.

## Features

- View available flights
- Book tickets
- Cancel existing bookings
- Check route details for each flight
- Login authentication with staff/admin demo users
- Modern Swing-based interface with seeded sample data

## Project Structure

- `src/airline/MainApp.java`: application entry point
- `src/airline/db/DatabaseManager.java`: SQLite setup and sample data
- `src/airline/service/AuthService.java`: login verification
- `src/airline/service/AirlineService.java`: booking and flight operations
- `src/airline/ui/LoginFrame.java`: authentication screen
- `src/airline/ui/AirlineManagementFrame.java`: desktop UI

## Demo Login

- Username: `admin`
- Password: `admin123`

Alternative staff account:

- Username: `staff`
- Password: `staff123`

## SQLite Driver

This project needs the SQLite JDBC driver in the `lib/` folder.

Expected file name example:

- `lib/sqlite-jdbc-3.49.1.0.jar`

## Compile

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

## Run

```powershell
java --enable-native-access=ALL-UNNAMED -cp "out;lib/*" airline.MainApp
```

## Notes

- The app creates `airline.db` in the project folder on first run.
- Flights and routes are seeded automatically if the database is empty.
