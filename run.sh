#!/bin/bash

# Paths relative to the script's location (inside LanChatApp)
SRC_DIR="./src"
BIN_DIR="./bin"

# Ensure bin directory exists
if [ ! -d "$BIN_DIR" ]; then
    mkdir -p "$BIN_DIR"
fi

# Function to compile the source files
compile() {
    echo "Compiling source files..."
    javac -d "$BIN_DIR" "$SRC_DIR/Server.java" "$SRC_DIR/ClientHandler.java" "$SRC_DIR/Client.java"
    if [ $? -eq 0 ]; then
        echo "Compilation successful"
    else
        echo "Compilation failed"
        exit 1
    fi
}

# Check if at least one argument is provided
if [ $# -lt 1 ]; then
    echo "Usage: $0 {server | client <server_ip> | compile}"
    echo "Examples:"
    echo "  $0 compile             # Compile source files into bin/"
    echo "  $0 server              # Run the server"
    echo "  $0 client 192.168.1.100 # Run a client connecting to 192.168.1.100"
    exit 1
fi

# Command to run
MODE=$1

case $MODE in
    "compile")
        compile
        ;;
    "server")
        # Compile if bin is empty or outdated
        if [ ! "$(ls -A $BIN_DIR)" ]; then
            compile
        fi
        echo "Starting server..."
        java -cp "$BIN_DIR" Server
        ;;
    "client")
        if [ $# -lt 2 ]; then
            echo "Error: Server IP address required for client mode"
            echo "Usage: $0 client <server_ip>"
            exit 1
        fi
        # Compile if bin is empty or outdated
        if [ ! "$(ls -A $BIN_DIR)" ]; then
            compile
        fi
        SERVER_IP=$2
        echo "Starting client, connecting to $SERVER_IP..."
        java -cp "$BIN_DIR" Client "$SERVER_IP"
        ;;
    *)
        echo "Error: Invalid mode '$MODE'"
        echo "Usage: $0 {server | client <server_ip> | compile}"
        exit 1
        ;;
esac