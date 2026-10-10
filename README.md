# Study Planner (Java Mini-Project)

A desktop study-planning application built with **Java Swing**. It starts empty so users enter their own information.

## Features
- Add study tasks and optional subjects
- Mark tasks as completed using the checkbox
- Remove a selected task
- Automatically update total, remaining, completed, and percentage progress
- Display progress grouped by subject
- Add study sessions using 24-hour time (`HH:mm`)
- Automatically sort sessions by time and remove selected sessions
- Desktop UI built with Java Swing; no external libraries required

## Requirements
- JDK 8 or later (JDK 17 or 21 recommended)
- Windows, macOS, or Linux with a graphical desktop

## Run on Windows
1. Install a JDK if Java is not already installed.
2. Extract the ZIP file.
3. Open the extracted `study_planner_java` folder.
4. Click the folder address bar in File Explorer, type `cmd`, and press Enter.
5. In the terminal, run:

   ```bat
   javac StudyPlanner.java
   java StudyPlanner
   ```

The Study Planner window should open. If `javac` is not recognized, install a JDK and ensure its `bin` folder is on your PATH, then reopen the terminal.

You can also double-click `run.bat` after installing the JDK.

## Run on macOS/Linux
Open Terminal in this folder and run:

```sh
javac StudyPlanner.java
java StudyPlanner
```

## Notes
- Tasks and sessions are stored in memory and are cleared when the application closes. This version does not use a database or permanent file storage.
