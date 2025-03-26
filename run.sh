
#!/bin/bash

# Define source and binary directories
SRC_DIR="src"
BIN_DIR="bin"

# Check if the binary directory exists, if not, create it
if [ ! -d "$BIN_DIR" ]; then
  mkdir "$BIN_DIR"
fi

# Compile Java files
if [ "$1" == "compile" ]; then
  javac -d "$BIN_DIR" "$SRC_DIR/chat_server.java" "$SRC_DIR/chat_client.java"

  # Check if compilation was successful
  if [ $? -ne 0 ]; then
    echo "Compilation failed. Check your Java code."
    exit 1
  fi

  echo "Compilation successful."
elif [ "$1" == "server" ]; then
  java -cp "$BIN_DIR" chat_server
elif [ "$1" == "client" ]; then
  java -cp "$BIN_DIR" chat_client
else
  echo "Usage: ./run.sh compile | server | client"
  exit 1
fi