package com.netsec;
// Fig. 27.6: ServerTest.java
// Test the Server application.
import javax.swing.JFrame;

public class ServerTest_net
{
    public static void main( String[] args )
    {
        Server_net application = new Server_net(); // create server
        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        application.runServer(); // run server application
    } // end main
} // end class ServerTest

