package com.java24hours;

// Fig. 17.20: FileDemonstration.java
// Demonstrating JFileChooser.
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class FileDemonstration extends JFrame
{
    private JTextArea outputArea; // used for output
    private JScrollPane scrollPane; // used to provide scrolling to output

    // set up GUI
    public FileDemonstration()
    {
        super( "Testing class File" );

        outputArea = new JTextArea();

        // add outputArea to scrollPane
        scrollPane = new JScrollPane( outputArea );

        add( scrollPane, BorderLayout.CENTER ); // add scrollPane to GUI

        setSize( 400, 400 ); // set GUI size
        setVisible( true ); // display GUI

        analyzePath(); // create and analyze File object
    } // end FileDemonstration constructor

    // allow user to specify file or directory name
    public File getFileOrDirectory()
    {
        // display file dialog, so user can choose file or directory to open
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(
                JFileChooser.FILES_AND_DIRECTORIES );

        int result = fileChooser.showOpenDialog( this );

        // if user clicked Cancel button on dialog, return
        if ( result == JFileChooser.CANCEL_OPTION )
            System.exit( 1 );

        File fileName = fileChooser.getSelectedFile(); // get File

        // display error if invalid
        if ( ( fileName == null ) || ( fileName.getName().equals( "" )))
        {
            JOptionPane.showMessageDialog( this, "Invalid Name",
                    "Invalid Name", JOptionPane.ERROR_MESSAGE );
            System.exit( 1 );
        } // end if

        return fileName;
    } // end method getFile

    // display information about file or directory user specifies
    public void analyzePath()
    {
        // create File object based on user input
        File name = getFileOrDirectory();

        if ( name.exists() ) // if name exists, output information about it
        {
            // display file (or directory) information
            outputArea.setText( String.format(
                    "%s%s\n%s\n%s\n%s\n%s%s\n%s%s\n%s%s\n%s%s\n%s%s",
                    name.getName(), " exists",
                    ( name.isFile() ? "is a file" : "is not a file" ),
                    ( name.isDirectory() ? "is a directory" :
                            "is not a directory" ),
                    ( name.isAbsolute() ? "is absolute path" :
                            "is not absolute path" ), "Last modified: ",
                    name.lastModified(), "Length: ", name.length(),
                    "Path: ", name.getPath(), "Absolute path: ",
                    name.getAbsolutePath(), "Parent: ", name.getParent() ) );

            if ( name.isDirectory() ) // output directory listing
            {
                String[] directory = name.list();
                outputArea.append( "\n\nDirectory contents:\n" );

                for ( String directoryName : directory )
                    outputArea.append( directoryName + "\n" );
            } // end else
        } // end outer if
        else // not file or directory, output error message
        {
            JOptionPane.showMessageDialog( this, name +
                    " does not exist.", "ERROR", JOptionPane.ERROR_MESSAGE );
        } // end else
    } // end method analyzePath
} // end class FileDemonstration
