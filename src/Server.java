/*
    Eurolfan, Jan Ellis D.
    CMSC 137 CD-2L
    Project 1 - Emulating TCP using UDP
    Server.java
    Acts as the server of the transactions
*/

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.IOException;

// list of possible server states
enum serverState
{
    idle, // idle server state
    prepareToReceive, // prepare for client input
    sendGoodbye // send disconnection
}

public class Server implements Runnable
{
    // socket for server's use
    protected static DatagramSocket serverSocket;
    // tracks the state of the server
    protected static serverState currentState;
    // address to be used
    protected static InetAddress inetAddress;
    // generic packet variable
    protected static DatagramPacket packet;

    public static void main (String[] args)
    {
        try
        {
            // create server instance. use localhost IP
            new Server (InetAddress.getLocalHost ());
        }
        catch (UnknownHostException uhe)
        {
            uhe.printStackTrace ();
        }
    }

    // constructor
    public Server (InetAddress inetAddress)
    {
        try
        {
            // localhost ip
            this.inetAddress = inetAddress;
            // socket for server's use
            this.serverSocket = new DatagramSocket (5590);
            // set timeout to 10 secs
            this.serverSocket.setSoTimeout (10000);
            // set server state
            this.currentState = serverState.idle;
        }
        catch (SocketException se)
        {
            se.printStackTrace ();
        }
        finally
        {
            this.run ();
        }
    }

    @Override
    public void run ()
    {
        System.out.println ("Running Server");

        while (true)
        {
            switch (this.currentState)
            {
                // when server is idle
                case idle:
                    try
                    {
                        // sleep for a while
                        Thread.sleep (1000);
                    }
                    catch (InterruptedException ie)
                    {
                        ie.printStackTrace ();
                    }
                    finally
                    {
                        try
                        {
                            // create a small buffer for potential messages
                            byte[] buffer = new byte [256];
                            // create holder for potential packet to be received
                            packet = new DatagramPacket (buffer, buffer.length);
                            // receive packet
                            serverSocket.receive (packet);
                        }
                        // also catches timeout
                        catch (IOException ioe)
                        {
                            System.out.println ("Server Status: Idle");
                        }
                        finally
                        {
                            String message = new String (packet.getData ());
                            if (message.length () != 0)
                            {
                                // print to terminal
                                printOutput (message);
                                // set server state to prepareToReceive
                                this.currentState = serverState.prepareToReceive;
                            }
                            else
                            {
                                System.out.println ("Server Status: Idle");
                                continue;
                            }
                        }
                    }
                    break;
                // server prepares to receive input
                case prepareToReceive:
                    try
                    {
                        // current state
                        System.out.println ("Server status: Ready to Receive");
                        // sleep for a while
                        Thread.sleep (1000);
                        // send acknowledgement for request
                        // message string
                        String temp = "Ready to Receive";
                        // create buffer
                        byte[] buffer = temp.getBytes ();
                        // create packet to be sent. last argument is hardcoded client socket
                        packet = new DatagramPacket (buffer, buffer.length, this.inetAddress, 5591);
                        // send packet
                        serverSocket.send (packet);
                    }
                    catch (InterruptedException ie)
                    {
                        ie.printStackTrace ();
                    }
                    catch (IOException ioe)
                    {
                        System.out.println ("Server status: Ready to Receive");
                    }
                    finally
                    {
                        try
                        {
                            // create a small buffer for potential messages
                            byte[] buffer = new byte [256];
                            // create holder for potential packet to be received
                            packet = new DatagramPacket (buffer, buffer.length);
                            // receive packet
                            serverSocket.receive (packet);
                        }
                        catch (IOException ioe)
                        {
                            System.out.println ("Server status: Ready to Receive");
                        }
                        finally
                        {
                            String message = new String (packet.getData ());
                            if (message.length () != 0)
                            {
                                // print output
                                printOutput (new String (packet.getData ()));
                                // set server state to cut connection with client
                                this.currentState = serverState.sendGoodbye;
                            }
                            else
                            {
                                System.out.println ("Server Status: Ready to Receive");
                                continue;
                            }
                        }
                    }
                break;
                case sendGoodbye:
                    try
                    {
                        // server status
                        System.out.println ("Server status: Sending Goodbye");
                        // sleep for a while
                        Thread.sleep (1000);
                    }
                    catch (InterruptedException ie)
                    {
                        ie.printStackTrace ();
                    }
                    finally
                    {
                        // send disconnection
                        try
                        {
                            // message string
                            String temp = "Goodbye";
                            // create buffer
                            byte[] buffer = temp.getBytes ();
                            // create packet to be sent. last argument is hardcoded client socket
                            packet = new DatagramPacket (buffer, buffer.length, this.inetAddress, 5591);
                            // send packet
                            serverSocket.send (packet);
                        }
                        catch (IOException ioe)
                        {
                            ioe.printStackTrace ();
                        }
                        finally
                        {
                            // return to idle state
                            this.currentState = serverState.idle;
                        }
                    }
                break;
            }
        }
    }

    // convenience method for printing
    private void printOutput (String output)
    {
        System.out.println ("Client sent -> " + output);
    }
}
