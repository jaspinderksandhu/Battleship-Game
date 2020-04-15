package cu.su.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * This abstract class represents a Client, or Player, that can connect
 * to a netgame Server.  The client is used for sending messages to the
 * Server (and through the Server possibly to other clients).  It also
 * receives messages from the Server.  An application must define a
 * subclass of this abstract class in order to create players.
 * Each player in the game will have an associated Client object,
 * which will handle details of network communication with the Server.
 * <p>At a minimum, the abstract method messageReceived(Object) must
 * be defined to say how the Client should respond when a message is
 * received.  Subclasses might also define the closedByError(),
 * serverShutdown(), playerConnected(), and playerDisconnected() methods.
 * <p>A client provides methods send(Object) and disconnect() for
 * sending a message to the Server and for closing down the connection.
 * Any non-null object that implements the Serializable interface can be sent
 * as a message.  Note that an ObjectOutputStream is used for sending
 * messages.  If the same object is to be sent more than once, with
 * changes between transmissions, then the resetOutput() method should be
 * called between transmissions (or the autoreset property should be
 * set to true).
 * <p>A client has a unique ID number that is assigned to the client
 * when it connects to the Server.  The ID can retrieved by calling
 * the getID() method.  The protected variable connectedPlayerIDs
 * contains the ID numbers of all clients currently connected to the
 * Server, including this one.
 */
abstract public class Client {

    private final ConnectionToServer connection;  // Represents the network connection to the Server.
    /**
     * A list of the ID numbers of all clients who are currently connected
     * to the Server.  This list is set each time this client is notified that
     * a client has connected to or disconnected from the Server.
     */
    protected int[] connectedPlayerIDs = new int[0];
    /**
     * If the autoreset property is set to true, then the ObjectOutputStream
     * that is used for transmitting messages is reset before each object is
     * sent.
     */
    private volatile boolean autoreset;

    // ---------------- Methods that subclasses can override --------------------------

    /**
     * Constructor opens a connection to a Server.  This constructor will
     * block while waiting for the connection to be established.
     *
     * @param ServerHostName The host name (or IP address) of the computer where the Server is running.
     * @param ServerPort     The port number on which the Server is listening for connection requests.
     * @throws IOException if any I/O exception occurs while trying to connect.
     */
    public Client(String ServerHostName, int ServerPort) throws IOException {
        connection = new ConnectionToServer(ServerHostName, ServerPort);
    }

    /**
     * This method is called when a message is received from the Server.
     * Concrete subclasses of this class must override this method to
     * say how to respond to messages.  Messages can be arbitrary
     * Serializable objects.
     */
    abstract protected void messageReceived(Object message);

    /**
     * This method is called whenever this client is notified that
     * a client has connected to the Server.  (Note that it is called
     * when this client connects, so this method will be called just
     * after the connection has been established.)   The list of all
     * connected players, including the new one, is in the protected
     * variable connectedPlayerIDs.  The method in this class does nothing.
     *
     * @param newPlayerID the ID number of the player who has connected.
     */
    protected void playerConnected(int newPlayerID) {
    }

    /**
     * This method is called when this client is notified that a client
     * has disconnected from the Server.  (Note that it IS NOT called
     * when this client disconnects.)  The list of all connected
     * players is in the protected variable connectedPlayerIDs.
     * The method in this class does nothing.
     *
     * @param departingPlayerID the ID number of the player who has
     *                          just disconnected.
     */
    protected void playerDisconnected(int departingPlayerID) {
    }

    /**
     * This method is called when the connection to the Server is closed down
     * because of some error.  The method in this class does nothing.  Subclasses
     * can override this method to take some action when the error occurs.
     */
    protected void connectionClosedByError(String message) {
    }

    /**
     * This method is called when the connection to the Server is closed down
     * because the server is shutting down normally.  The method in this class does
     * nothing.  Subclasses can override this method to take some action when shutdown
     * occurs.  The message will be "*shutdown*" if the message was in fact
     * sent by a Server that is shutting down in the normal way.
     */
    protected void serverShutdown(String message) {
    }

    // ----------------------- Methods meant to be called by users of this class -----------

    /**
     * This method is called after a connection to the server has been opened
     * and after the client has been assigned an ID number.  Its purpose is to
     * do extra checking or set up before the connection is fully established.
     * If this method throws an IOException, then the connection is closed
     * and the player is never added to the list of players.  The method in
     * this class does nothing.  The client and the Server must both be programmed
     * with the same handshake protocol.  At the time this method is called,
     * the client's ID number has already been set and can be retrieved by
     * calling the getID() method, but the client has not yet been added to
     * the list of connected players.
     *
     * @param in  a stream from which messages from the Server can be read.
     * @param out a stream to which messages to the Server can be written.  After writing
     *            a message to this stream, it is important to call out.flush() to make sure
     *            that the message is actually transmitted.
     * @throws IOException should be thrown if some error occurs that would
     *                     prevent the connection from being fully established.
     */
    protected void extraHandshake(ObjectInputStream in, ObjectOutputStream out)
            throws IOException {
    }

    /**
     * This method can be called to disconnect cleanly from the server.
     * If the connection is already closed, this method has no effect.
     */
    public void disconnect() {
        if (!connection.closed)
            connection.send(new DisconnectMessage("Goodbye Server"));
    }

    /**
     * This method is called to send a message to the Server.  This method simply
     * drops the message into a queue of outgoing messages, and it
     * never blocks.  This method throws an IllegalStateException if the
     * connection to the Server has already been closed.
     *
     * @param message A non-null object representing the message.  This object
     *                must implement the Serializable interface.
     * @throws IllegalArgumentException if message is null or is not Serializable.
     * @throws IllegalStateException    if the connection has already been closed,
     *                                  either by the disconnect() method, because the Server has shut down, or
     *                                  because of a network error.
     */
    public void send(Object message) {
        if (message == null)
            throw new IllegalArgumentException("Null cannot be sent as a message.");
        if (!(message instanceof Serializable))
            throw new IllegalArgumentException("Messages must implement the Serializable interface.");
        if (connection.closed)
            throw new IllegalStateException("Message cannot be sent because the connection is closed.");
        connection.send(message);
    }

    /**
     * Returns the ID number of this client, which is assigned by the Server when
     * the connection to the Server is created.  The id uniquely identifies this
     * client among all clients which have connected to the Server.  ID numbers
     * are always assigned in the order 1, 2, 3, 4...  There can be gaps in the
     * sequence if some client disconnects or because some client does not
     * completely connect because of an exception.  (This can include an
     * exception in the "extra handshake" part, if there is one, of the
     * connection setup.)
     * @return connection.id_number correct ID for connecting the computers.
     */
    public int getID() {
        return connection.id_number;
    }

    /**
     * Resets the output stream, after any messages currently in the output queue
     * have been sent.  The stream only needs to be reset in one case:  If the same
     * object is transmitted more than once, and changes have been made to it
     * between transmissions.  The reason for this is that ObjectOutputStreams are
     * optimized for sending objects that don't change -- if the same object is sent
     * twice it will not actually be transmitted the second time, unless the stream
     * has been reset in the meantime.
     */
    public void resetOutput() {
        connection.send(new ResetSignal()); // A ResetSignal in the output stream is seen as a signal to reset.
    }

    /**
     * Returns the value of the autoreset property.
     * @return autoreset reset option of game.
     */
    public boolean getAutoreset() {
        return autoreset;
    }


    //------------- Private implementation part of the class -----------------------------

    /**
     * If the autoreset property is set to true, then the output stream will be reset
     * before every object transmission.  Use this if the same object is going to be
     * continually changed and retransmitted.  See the resetOutput() method for more
     * information on resetting the output stream.
     * @param auto saves the reset done.
     */
    public void setAutoreset(boolean auto) {
        autoreset = auto;
    }

    /**
     * This private class handles the actual communication with the server.
     */
    private class ConnectionToServer {

        private final int id_number;               // The ID of this client, assigned by the Server.
        private final Socket socket;               // The socket that is connected to the Server.
        private final ObjectInputStream in;        // A stream for sending messages to the Server.
        private final ObjectOutputStream out;      // A stream for receiving messages from the Server.
        private final SendThread sendThread;       // The thread that sends messages to the Server.
        private final ReceiveThread receiveThread; // The thread that receives messages from the Server.

        private final LinkedBlockingQueue<Object> outgoingMessages;  // Queue of messages waiting to be transmitted.

        private volatile boolean closed;     // This is set to true when the connection is closing.
        // For one thing, this will prevent errors from being
        // reported when exceptions are generated because the
        // connection is being closed in the normal way.

        /**
         * Constructor opens the connection and sends the string "Hello Server"
         * to the Server.  The Server responds with an object of type Integer representing
         * the ID number of the client.  The extraHandshake() method is then called
         * to do any other required startup communication.  Finally, threads
         * are created to handle sending and receiving messages.
         */
        ConnectionToServer(String host, int port) throws IOException {
            outgoingMessages = new LinkedBlockingQueue<Object>();
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject("Hello Server");
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            try {
                Object response = in.readObject();
                id_number = ((Integer) response).intValue();
            } catch (Exception e) {
                throw new IOException("Illegal response from server.");
            }
            extraHandshake(in, out);  // Will throw an IOException if handshake doesn't succeed.
            sendThread = new SendThread();
            receiveThread = new ReceiveThread();
            sendThread.start();
            receiveThread.start();
        }

        /**
         * This method is called to close the connection.  It can be called from outside
         * this class, and it is also used internally for closing the connection.
         */
        void close() {
            closed = true;
            sendThread.interrupt();
            receiveThread.interrupt();
            try {
                socket.close();
            } catch (IOException e) {
            }
        }

        /**
         * This method is called to transmit a message to the Server.
         *
         * @param message the message, which must be a Serializable object.
         */
        void send(Object message) {
            outgoingMessages.add(message);
        }

        /**
         * This method is called by the threads that do input and output
         * on the connection when an IOException occurs.
         */
        synchronized void closedByError(String message) {
            if (!closed) {
                connectionClosedByError(message);
                close();
            }
        }

        /**
         * This class defines a thread that sends messages to the Server.
         */
        private class SendThread extends Thread {
            public void run() {
                System.out.println("Client send thread started.");
                try {
                    while (!closed) {
                        Object message = outgoingMessages.take();
                        if (message instanceof ResetSignal) {
                            out.reset();
                        } else {
                            if (autoreset)
                                out.reset();
                            out.writeObject(message);
                            out.flush();
                            if (message instanceof DisconnectMessage) {
                                close();
                            }
                        }
                    }
                } catch (IOException e) {
                    if (!closed) {
                        closedByError("IO error occurred while trying to send message.");
                        System.out.println("Client send thread terminated by IOException: " + e);
                    }
                } catch (Exception e) {
                    if (!closed) {
                        closedByError("Unexpected internal error in send thread: " + e);
                        System.out.println("\nUnexpected error shuts down client send thread:");
                        e.printStackTrace();
                    }
                } finally {
                    System.out.println("Client send thread terminated.");
                }
            }
        }

        /**
         * This class defines a thread that reads messages from the Server.
         */
        private class ReceiveThread extends Thread {
            public void run() {
                try {
                    while (!closed) {
                        Object obj = in.readObject();
                        if (obj instanceof DisconnectMessage) {
                            close();
                            serverShutdown(((DisconnectMessage) obj).message);
                        } else if (obj instanceof StatusMessage) {
                            StatusMessage msg = (StatusMessage) obj;
                            connectedPlayerIDs = msg.players;
                            if (msg.connecting)
                                playerConnected(msg.playerID);
                            else
                                playerDisconnected(msg.playerID);
                        } else
                            messageReceived(obj);
                    }
                } catch (IOException e) {
                    if (!closed) {
                        closedByError("IO error occurred while waiting to receive  message.");
                        System.out.println("Client receive thread terminated by IOException: " + e);
                    }
                } catch (Exception e) {
                    if (!closed) {
                        closedByError("Unexpected internal error in receive thread: " + e);
                        System.out.println("\nUnexpected error shuts down client receive thread:");
                        e.printStackTrace();
                    }
                } finally {
                    System.out.println("Client receive thread terminated.");
                }
            }
        }

    } // end nested class ConnectionToServer

}