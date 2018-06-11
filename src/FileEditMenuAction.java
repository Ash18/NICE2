
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.io.*;

import javax.swing.event.*;

import java.util.*;

import javax.swing.undo.*;
import javax.swing.filechooser.FileFilter;

/**
* Handles the menu actions of Run menu in the application
*/

public class FileEditMenuAction extends AbstractAction implements UndoableEditListener {
    protected LC frame;
    private UndoManager undomanager;

    public FileEditMenuAction(String name, Icon icon, String descrip,Integer mnemonic, KeyStroke accel, LC gui) {
        super(name, icon);
        putValue(SHORT_DESCRIPTION, descrip);
        putValue(MNEMONIC_KEY, mnemonic);
        putValue(ACCELERATOR_KEY, accel);
        frame = gui;
    }

    /**
    * implements the actionPerformed method of AbstractAction
    * triggered when an event is generated by any component
    *
    * @param e component triggering this call
    */
    public void actionPerformed(ActionEvent e) {
        frame.codeEditor.flag=false;
        if(e.getActionCommand().equals("New"))
        fileNewAction(e);
        if(e.getActionCommand().equals("Import"))
        fileIncludeAction(e);
        if(e.getActionCommand().equals("Open ..."))
        fileOpenAction(e);
        if(e.getActionCommand().equals("Close"))
        fileCloseAction(e);
        if(e.getActionCommand().equals("Save"))
        fileSaveAction(e);
        if(e.getActionCommand().equals("Save as ..."))
        fileSaveAsAction(e);
        if(e.getActionCommand().equals("Exit"))
        fileExitAction(e);
        if(e.getActionCommand().equals("Undo"))
        editUndoAction(e);
        if(e.getActionCommand().equals("Redo"))
        editRedoAction(e);
        if(e.getActionCommand().equals("Cut"))
        editCutAction(e);
        if(e.getActionCommand().equals("Copy"))
        editCopyAction(e);
        if(e.getActionCommand().equals("Paste"))
        editPasteAction(e);
        if(e.getActionCommand().startsWith("check")) 
        	checked(e);
    }

    
    public void  checked(ActionEvent e){
    	StringBuilder b = new StringBuilder();
    	String[] commend = e.getActionCommand().split(" ", 2);
    	int port = Integer.parseInt(commend[1]);
    	for(int j=0;j<8;j++){
    			if(LC.cb[port][j].isSelected()) b.append("1");
    			else b.append("0");
    	}
    	int col = 0;
    	if(port%2==0)
    		col=1;
    	else
    		col=3;
    	LC.IOdevicePanel.table.setValueAt(b.toString(), port/2,col );
    		
    }
    /**
    * Code to create a new document.  It clears the source code window.
    *
    * @param e component triggering this call
    */
    public void fileNewAction(ActionEvent e) {
        if (frame.codeEditor.hasChanged() && !askSaveChanges(true)) return;
        frame.codeEditor.newFile();
        refreshMenuItems(false);
        String filename = frame.codeEditor.getFilename();
        frame.setTitle(frame.title + (filename == null ? " - Untitled*" : " - " + filename));
    }


    /**
    * Launch a file chooser for name of file to open
    *
    * @param e component triggering this call
    */
    public void fileOpenAction(ActionEvent e) {
        if (frame.codeEditor.hasChanged() && !askSaveChanges(true)) return;
        frame.codeEditor.openDialog();
        refreshMenuItems(false);
        refreshTitleBar();
    }

    /**
    * Code to insert a file in some file.
    *
    * @param e component triggering this call
    */
    public void fileIncludeAction(ActionEvent e) {
        JFileChooser chooser;
        int status;
        frame.codeEditor.codeArea.setEditable(true);
        frame.codeEditor.codeArea.setBackground(Color.white);

        undomanager = new UndoManager();
        undomanager.setLimit(1000);
        frame.codeEditor.codeArea.requestFocus();

        chooser = new JFileChooser();
        chooser.setFileFilter(new SourceFileFilter());
        status=chooser.showDialog(null,"Import");
        if(status==JFileChooser.APPROVE_OPTION)
        readSource(chooser.getSelectedFile(),false);
    }

    /** reads a file and sets in the code area
    * 'true' mode for 'open' and 'false' for 'import/import'
    */
    public void readSource(File file,boolean mode) {
        try {
            FileManager fm = new FileManager();
            if(mode) {
                frame.codeEditor.codeArea.setText(fm.openFile(file.getAbsolutePath()));
                frame.setTitle("2L Proc-Sim -"+file.getAbsolutePath());
            }
            else {
                //frame.codeEditor.codeArea.append(fm.openFile(file.getAbsolutePath()));
                frame.codeEditor.codeArea.
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,"Error in saving file \n :"+ex.getMessage());
        }
    }


    /**
    * saves the file, if not alredy saved it will do a saveAs
    */
    public boolean fileSaveAction(ActionEvent e) {
        boolean success = frame.codeEditor.saveFile();
        if (success) {
            refreshSaveMenuItem(false);
            refreshTitleBar();
        }
        return success;
    }


    /* saveAs option...saves the file with differnt name */
    public boolean fileSaveAsAction(ActionEvent e) {
        boolean success = frame.codeEditor.saveDialog();
        if (success) {
            refreshSaveMenuItem(false);
            refreshTitleBar();
        }
        return success;
    }


    /* prompts user to save the document before it is discarded */
    protected boolean askSaveChanges(boolean allowCancel) {
        int ans = JOptionPane.showConfirmDialog(frame,
        "Save changes to the file?", getTitle(), allowCancel ?
        JOptionPane.YES_NO_CANCEL_OPTION : JOptionPane.YES_NO_OPTION);
        if (ans == JOptionPane.YES_OPTION) frame.codeEditor.saveFile();
        return (ans != JOptionPane.CANCEL_OPTION);
    }

    /**
    * closes the file currently open.
    *
    * @param e component triggering this call
    */
    public void fileCloseAction(ActionEvent e) {
        if (frame.codeEditor.hasChanged()&& !askSaveChanges(true)) return;
        frame.codeEditor.closeFile();
        refreshMenuItems(false);
        frame.codeEditor.currentFile=null;
        frame.codeEditor.codeArea.setText("");
        frame.assembleCodeArea.setText("");
        frame.codeEditor.codeArea.setEditable(false);
        frame.codeEditor.codeArea.setBackground(Color.lightGray);
        refreshTitleBar();
    }


    /**
    * Exits the application.
    * NEEDS TO CHECK ASK TO SAVE CHANGES IF CHANGES HAVE OCCURED IN CURRENT SOURCE CODE.
    *
    * @param e component triggering this call
    */
    public void fileExitAction(ActionEvent e) {
        if (frame.codeEditor.hasChanged()) askSaveChanges(false);
        System.exit(0);
    }

    /**
    * implements the function of Undo action for Edit menu
    *
    * @param e component triggering this call
    */
    public void editUndoAction(ActionEvent e) {
        frame.codeEditor.undo();
        refreshMenuItems(true);
    }

    /**
    * implements the function of Redo action for Edit menu
    *
    * @param e component triggering this call
    */
    public void editRedoAction(ActionEvent e) {
        frame.codeEditor.redo();
        refreshMenuItems(true);
    }

    /**
    * implements the function of cut action for Edit menu
    *
    * @param e component triggering this call
    */
    public void editCutAction(ActionEvent e) {
        frame.codeEditor.cut();
        refreshMenuItems(true);
    }

    /**
    * implements the function of copy action for Edit menu
    *
    * @param e component triggering this call
    */
    public void editCopyAction(ActionEvent e) {
        frame.codeEditor.copy();
        refreshUndoMenuItems();
    }

    /**
    * implements the function of paste action for Edit menu
    *
    * @param e component triggering this call
    */
    public void editPasteAction(ActionEvent e) {
        frame.codeEditor.paste();
        refreshMenuItems(true);
    }

    /** refreshes Edit Undo, Edit Redo */
    private void refreshMenuItems(boolean dirty) {
        refreshUndoMenuItems();
        refreshSaveMenuItem(dirty);
    }


    /** refreshes the Edit Undo and Edit Redo menu items */
    private void refreshUndoMenuItems() {
        frame.editUndo.setEnabled(frame.codeEditor.canUndo());
        frame.editUndo.setText(frame.codeEditor.getUndoName());
        frame.Undo.setEnabled(frame.codeEditor.canUndo());
        frame.editRedo.setEnabled(frame.codeEditor.canRedo());
        frame.Redo.setEnabled(frame.codeEditor.canRedo());
        frame.editRedo.setText(frame.codeEditor.getRedoName());
    }

    /** refreshes the File Save menu item */
    private void refreshSaveMenuItem(boolean dirty) {
        frame.fileSave.setEnabled(dirty);
        frame.Save.setEnabled(dirty);
    }

    /** refreshes the frame's title bar */
    private void refreshTitleBar() {
        String filename = frame.codeEditor.getFilename();
        frame.setTitle(frame.title + (filename == null ? "" : " - " + filename));
    }

    /** updates menu items when undoable action occurs */
    public void undoableEditHappened(UndoableEditEvent e) {
        if (!e.getEdit().isSignificant())	return;
        // refresh menu items when an undoable event occurs
        refreshMenuItems(true);
    }

    /** sets the text editor's title bar text */
    public void setTitle(String frameTitle) {
        frame.title = frameTitle;
        refreshTitleBar();
    }

    /** gets the text editor's title bar text */
    public String getTitle() {
        return frame.title;
    }
}