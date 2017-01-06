/*
    Eurolfan, Jan Ellis D.
    CMSC 137 CD-2L
    Project 1 - Emulating TCP using UDP
    Client.java
    Acts as the client of the transactions
*/

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.IOException;

// list of possible client states
enum clientState
{
    sendRequest, // send request to server
    acceptAcknowledgement, // acceptAcknowledgement and send input
    acceptGoodbye // disconnec from server
}

public class Client implements Runnable
{
    // client's socket
    protected static DatagramSocket clientSocket;
    // client state
    protected static clientState currentState = clientState.sendRequest;
    // address to be used
    protected static InetAddress inetAddress;
    // generic packet variable
    protected static DatagramPacket packet;

    public static void main (String[] args)
    {
        try
        {
            // use localhost ip
            new Client (InetAddress.getLocalHost ());
        }
        catch (UnknownHostException uhe)
        {
            uhe.printStackTrace ();
        }
    }

    public Client (InetAddress inetAddress)
    {
        try
        {
            // contains local ip
            this.inetAddress = inetAddress;
            // hardcoded socket for client's use
            this.clientSocket = new DatagramSocket (5591);
            // set timeout to 10 secs
            this.clientSocket.setSoTimeout (10000);
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
        System.out.println ("Running Client");

        while (true)
        {
            switch (this.currentState)
            {
                case sendRequest:
                    try
                    {
                        // sleep for a while
                        Thread.sleep (1000);
                        System.out.println ("Client status: Request Sent");
                    }
                    catch (InterruptedException ie)
                    {
                        ie.printStackTrace ();
                    }
                    finally
                    {
                        try
                        {
                            // request message
                            String message = "Request to Send";
                            // create buffer for message
                            byte[] buffer = message.getBytes ();
                            // create packet to be sent
                            packet = new DatagramPacket (buffer, buffer.length, this.inetAddress, 5590);
                            // send packet
                            clientSocket.send (packet);
                        }
                        catch (IOException ioe)
                        {
                            ioe.printStackTrace ();
                        }
                        finally
                        {
                            this.currentState = clientState.acceptAcknowledgement;
                        }
                    }
                break;
                case acceptAcknowledgement:
                    try
                    {
                        // sleep for a while
                        Thread.sleep (1000);
                        // wait for acknowledgement
                        byte[] buffer = new byte [256];
                        // create packet holder
                        packet = new DatagramPacket (buffer, buffer.length);
                        // receive packet
                        clientSocket.receive (packet);
                    }
                    catch (InterruptedException ie)
                    {
                        ie.printStackTrace ();
                    }
                    catch (IOException ioe)
                    {
                        System.out.println ("Client status: Awaiting Acknowledgement");
                    }
                    finally
                    {
                        try
                        {
                            // print acknowledgement
                            printOutput (new String (packet.getData()));
                            System.out.println ("Client status: Sending Input");
                            // send input to server
                            String message = "Hi Server!";
                            byte[] buffer = message.getBytes ();
                            // send to server
                            packet = new DatagramPacket (buffer, buffer.length, this.inetAddress, 5590);
                            clientSocket.send (packet);
                        }
                        catch (IOException ioe)
                        {
                            System.out.println ("Client status: Sending Input");
                        }
                        finally
                        {
                            this.currentState = clientState.acceptGoodbye;
                        }
                    }
                break;
                case acceptGoodbye:
                    try
                    {
                        // sleep for a while
                        Thread.sleep (1000);
                        // create buffer for message
                        byte[] buffer = new byte [256];
                        // create holder packet
                        packet = new DatagramPacket (buffer, buffer.length);
                        // receive packet
                        clientSocket.receive (packet);
                    }
                    catch (IOException ioe)
                    {
                        System.out.println ("");
                    }
                    catch (InterruptedException ie)
                    {
                        ie.printStackTrace ();
                    }
                    finally
                    {
                        // print output
                        printOutput (new String (packet.getData ()));
                        // set client state to init state
                        this.currentState = clientState.sendRequest;
                    }
                break;
            }
        }
    }

    // convenience method for printing
    private void printOutput (String output)
    {
        System.out.println ("Server sent -> " + output);
    }
}
