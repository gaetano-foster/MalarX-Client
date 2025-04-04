# MalarX-Client

This project represents the **client-side application for MalarX**, a desktop-based GUI tool built in Java using native libraries. The application allows the user to select a local `.png` image of a blood smear, send it to a socket-based deep learning server, and receive a classification result indicating whether the cell is uninfected or infected with either *Plasmodium Falciparum* or *Plasmodium Vivax*.

The backend is assumed to be running as a Python socket server that processes incoming image data with a trained TensorFlow model.

---

## Functionality

The client performs the following tasks:

1. Displays a fixed-size window emulating mobile screen dimensions (`375x667`) using Java Swing.
2. Allows the user to pick a `.png` file from the local file system.
3. Scales and displays the selected image in the UI.
4. Connects to the server via a `Socket` at `0.0.0.0:12345` and sends the image as raw bytes.
5. Waits for a response from the server and shows the result in a modal dialog.

---

## File Structure


---

## Usage

This project is designed to be run as a prebuilt binary. Open the application and click "Pick" to select a `.png` image from disk. The image is displayed within the UI and can be sent to the server by clicking "Process File". The server must be running and listening on port `12345`.

Only `.png` files are supported for input. If another format is selected, the application will prompt the user to choose a valid image.

---

## Networking

The client uses a basic TCP socket connection:

- Host: `0.0.0.0`
- Port: `12345`
- Protocol: raw byte stream over TCP

The client sends the file length as an integer followed by the raw image bytes. It expects the server to respond with a single-line string indicating the classification result.

Example responses:


---

## GUI Details

- Resolution: `375x667`
- File input: `JFileChooser`
- Image display: `JLabel` with scaled `ImageIcon`
- Result: `JOptionPane` dialog
- Dependencies: None (standard Java SE libraries only)

---

## Server Integration

The backend should:

- Accept a socket connection.
- Receive an integer indicating the byte length, followed by the image data.
- Process the image using a trained TensorFlow model.
- Return a classification string as the response.

This client is built to work seamlessly with the MalarX-ServerSide Python backend.

---

## Notes

- All image processing and model inference occur server-side.
- Communication is over raw TCP, not HTTP or WebSocket.
- Only single-image prediction is supported.
- GUI is lightweight and emulates a mobile experience in a desktop window.

---

## License

This project is released under the MIT License.

