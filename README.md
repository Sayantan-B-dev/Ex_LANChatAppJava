# LanChatApp

LanChatApp is a simple LAN-based chat application built in Java. It allows multiple clients to connect to a server over a local network and exchange messages in real-time. The server runs on one computer, while clients with a graphical user interface (GUI) run on other computers, all connected via the same LAN (e.g., through a switch). Clients can choose their own usernames upon startup.

## Features
- **Server**: Runs on a single machine, accepts client connections, and broadcasts messages.
- **Clients**: Feature a Swing-based GUI with a chat display area, message input field, and send button.
- **Custom Usernames**: Each client can specify their own username when starting the application.
- **LAN Communication**: Works over a local network using TCP sockets (port 5000).
- **Simple Setup**: Easy to compile and run with a provided Bash script (`run.sh`).

## Folder Structure
```
LanChatApp/
├── images/
│   └── diagram.png
├── src/
│   ├── Server.java       # Server logic
│   ├── ClientHandler.java # Handles connections
│   └── Client.java       # Client GUI
├── bin/
└── run.sh
```

## Prerequisites
- **Java Development Kit (JDK)**: Version 8 or higher installed on all machines.
    - Check with: `java -version` and `javac -version`
- **Local Network**: All computers must be on the same LAN (e.g., connected via a switch or router).
- **Operating System**: Linux, macOS, or Windows (with Bash-like environment, e.g., Git Bash, for `run.sh`).

## Setup Instructions

1. **Clone or Download**:
    - Copy the `LanChatApp` folder to each computer that will run the server or a client.

2. **Prepare the Directory**:
    - Ensure the `src/` folder contains `Server.java`, `ClientHandler.java`, and `Client.java`.
    - Create the `bin/` folder if it doesn’t exist:
    ```
    mkdir -p LanChatApp/bin
    ```
    - Place `run.sh` in the `LanChatApp/` directory.

3. **Make the Script Executable** (Linux/macOS):
- Navigate to the folder:   
```
cd LanChatApp
```
- Run:
```
chmod +x run.sh
```

## How to Run

### Step 1: Compile the Application
Compile the source files into the `bin/` folder:
```
cd LanChatApp
./run.sh compile
```
- Output: `Compiling source files...` followed by `Compilation successful`
- This creates `.class` files in `LanChatApp/bin/`.

**Note**: Re-run this command if you modify the source files.

### Step 2: Run the Server
Start the server on one computer (e.g., with IP `192.168.1.100`):
```
cd LanChatApp
./run.sh server
```
- Output: `Starting server...` followed by `Server started on port 5000`
- The server will listen for client connections on port 5000.

**Find Your Server IP**:
- Linux/macOS: `ifconfig` or `ip addr`
- Windows: `ipconfig` in Command Prompt

### Step 3: Run the Clients
Start a client on each additional computer, connecting to the server’s IP:
```
cd LanChatApp
./run.sh client <server_ip>
```
- Example: `./run.sh client 192.168.1.100`
- A dialog will prompt for a username (e.g., "Alice"). Enter a name and click OK.
- Output: A GUI window opens with "LAN Chat Client - <username>".

**Client Usage**:
- Type messages in the text field and click "Send" or press Enter.
- Messages appear in the chat area (e.g., `Alice: Hi Bob`).
- Type `/quit` and send to exit the client.

### Example Workflow
- **Computer 1 (Server, IP 192.168.1.100)**:
```
cd LanChatApp
./run.sh compile
./run.sh server
```
- **Computer 2 (Client)**:
```
cd LanChatApp
./run.sh client 192.168.1.100
```
- Enter username: "Alice"
- **Computer 3 (Client)**:
```
cd LanChatApp
./run.sh client 192.168.1.100
```


## Architecture
Below is a diagram showing how LanChatApp is deployed across three computers on a LAN:
![LanChatApp Deployment Diagram](images/diagram.png)