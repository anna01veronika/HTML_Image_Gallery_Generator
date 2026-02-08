# first_java_project

This is a project written in Java that automatically crawls a folder and generates a navigable HTML gallery from the images (.jpg, .jpeg, .png) in it.

Features
Recursive Traversal: The program maps the specified root folder and all its subdirectories.

Automatic Indexing: Generates an index.html for every folder, listing subdirectories and images.

Image Viewer Pages: Each image gets its own dedicated HTML page with "Back", "Next", and "Up" (^^) navigation controls.

Cleaning Mode: A dedicated option to remove all generated HTML files from the directory tree in one go.

Interactive Images: Clicking on an image automatically takes you to the next one in the gallery.

Usage
To run the program, compile the Java files and use the following commands:

### 1. Generate Gallery
```bash
java Main <folder_path>

### 2. Clean Gallery (Delete HTML files)
```bash
java Main <folder_path> -c

### 3. Help
```bash
java Main -h
# or
java Main -help

### I uploaded a zip file that contains a folder that almost covers all the possible outcomes for the program, so you can test it out yourself!
