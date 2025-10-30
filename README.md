<p align="center">
  <img src="https://raw.githubusercontent.com/ckroeger/simple-activiti/main/.github/logo.png" alt="Simple Activiti Logo" width="180"/>
</p>

# Simple Activiti

Simple Activiti is a lightweight, open-source Java application that simulates mouse activity to prevent your computer 
from going idle or triggering screen savers and auto-locks. It offers two operation modes: a simple periodic mouse 
movement and a more natural, human-like random movement within a configurable area.

## Features

- **Simple Mode (1):** Moves the mouse by a small amount at regular intervals.
- **Natural Mode (2):** Moves the mouse in a smooth, random, and human-like pattern within a defined area.
- **User Activity Detection:** Pauses automatic movement if the user moves the mouse, and resumes after a period of inactivity.
- **Configurable:** Easily adjust movement intervals, ranges, and delays via code.
- **Cross-platform:** Runs anywhere Java is supported (Windows, macOS, Linux).

## Usage

### Requirements
- Java 8 or higher

### Build

```sh
mvn package
```

### Run

```sh
java -jar target/simple-activiti-*.jar [mode]
```

- `mode` (optional):
  - `1` - Simple mode (default: moves mouse every second)
  - `2` - Natural mode (moves mouse in a random area)

Example:

```sh
java -jar target/simple-activiti-1.0-SNAPSHOT.jar 2
```

## Why?

Simple Activiti is useful for:
- Preventing screen savers or auto-locks during long-running tasks
- Keeping remote desktop sessions alive
- Simulating presence for testing or demonstration purposes

## Logo

The logo represents a mouse pointer in motion, symbolizing the application's core functionality: keeping your system active by simulating mouse movement.

<p align="center">
  <img src="https://raw.githubusercontent.com/ckroeger/simple-activiti/main/.github/logo.png" alt="Simple Activiti Logo" width="120"/>
</p>

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

Made with ❤️ by [ckroeger](https://github.com/ckroeger)

