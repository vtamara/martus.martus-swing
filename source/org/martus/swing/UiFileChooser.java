/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2004, Beneficent
Technology, Inc. (Benetech).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/

package org.martus.swing;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

//This implementation is needed because of a Java bug 
//which by clicking on a directory changes the file name 
//to be saved to that of the directory name.

public class UiFileChooser extends JFileChooser
{
	private UiFileChooser()
	{
	}

	private UiFileChooser(String title, File currentlySelectedFile, File currentDirectory, String buttonLabel, FileFilter filterToUse)
	{
		super();
		setComponentOrientation(UiLanguageDirection.getComponentOrientation());
		addPropertyChangeListener(JFileChooser.DIRECTORY_CHANGED_PROPERTY,new DirectoryChangeListener());
		addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, new FileSelectedChangeListener());
		setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if(title != null)
			setDialogTitle(title);

		if(currentlySelectedFile != null)
			setSelectedFile(currentlySelectedFile);
		if(currentDirectory != null)
			setCurrentDirectory(currentDirectory);
		if(currentlySelectedFile == null && currentDirectory == null)
			setCurrentDirectory(getHomeDirectoryFile());
		
		if(buttonLabel != null)
			setApproveButtonText(buttonLabel);
		if(filterToUse != null)
			setFileFilter(filterToUse);
	}
	
	static public FileDialogResults displayFileSaveDialog(Component owner, String title, String newFileName)
	{
		return displayFileSaveDialog(owner, title, getHomeDirectoryFile(newFileName));
	}
	
	static public FileDialogResults displayFileSaveDialog(Component owner, String title, File currentDirectory, FileFilter filterToUse)
	{
		UiFileChooser chooser = new UiFileChooser(title, null, currentDirectory, null, filterToUse);
		return getFileResults(chooser.showSaveDialog(owner), chooser);
	}

	static public FileDialogResults displayFileSaveDialog(Component owner, String title, File currentlySelectedFile)
	{
		UiFileChooser chooser = new UiFileChooser(title, currentlySelectedFile, null, null, null);
		return getFileResults(chooser.showSaveDialog(owner), chooser);
	}
	
	static public FileDialogResults displayFileOpenDialog(Component owner, String title, String currentlySelectedFileName)
	{
		return displayFileOpenDialog(owner, title, getHomeDirectoryFile(currentlySelectedFileName), null, null, null);
	}
	
	static public FileDialogResults displayFileOpenDialog(Component owner, String title, File currentDirectory, String buttonLabel, FileFilter filterToUse)
	{
		return displayFileOpenDialog(owner, title, null, currentDirectory, buttonLabel, filterToUse);
	}
	
	static public FileDialogResults displayFileOpenDialog(Component owner, String title, File currentlySelectedFile)
	{
		return displayFileOpenDialog(owner, title, currentlySelectedFile, null, null, null);
	}

	static public FileDialogResults displayFileOpenDialog(Component owner, String title, File currentlySelectedFile, File currentDirectory)
	{
		return displayFileOpenDialog(owner, title, currentlySelectedFile, currentDirectory, null, null);
	}
	
	static public FileDialogResults displayFileOpenDialog(Component owner, String title, File currentlySelectedFile, File currentDirectory, String buttonLabel, FileFilter filterToUse)
	{
		UiFileChooser chooser = new UiFileChooser(title, currentlySelectedFile, currentDirectory, buttonLabel, filterToUse);
		return getFileResults(chooser.showOpenDialog(owner), chooser);
	}
	
	private static FileDialogResults getFileResults(int result, UiFileChooser chooser)
	{
		if(result != JFileChooser.APPROVE_OPTION)
			return new FileDialogResults();
		return new FileDialogResults(chooser.getSelectedFile(), false);
	}

	static public class FileDialogResults
	{
		FileDialogResults()
		{
			this(null, true);
		}
		
		FileDialogResults(File choosenFileToUse, boolean wasCancelChoosen)
		{
			cancelChoosen = wasCancelChoosen;
			choosenFile = choosenFileToUse;
		}
		
		public boolean wasCancelChoosen()
		{
			return cancelChoosen;
		}

		public File getChosenFile()
		{
			return choosenFile;
		}
		
		public File getCurrentDirectory()
		{
			if(choosenFile == null)
				return null;
			return choosenFile.getParentFile();
		}

		private File choosenFile;
		private boolean cancelChoosen;
	}
	
	static public File getHomeDirectoryFile()
	{
		return new File(System.getProperty("user.home"));
	}
	
	static public File getHomeDirectoryFile(String fileName)
	{
		File homeDir = getHomeDirectoryFile();
		if(fileName != null && fileName.length()>0)
			return new File(homeDir, fileName);
		return homeDir;
	}

	class DirectoryChangeListener implements PropertyChangeListener 
	{
		public void propertyChange(PropertyChangeEvent e) 
		{
			processDirChanged(e);
		}
	}

	void processDirChanged(PropertyChangeEvent e) 
	{
		if ( previouslySelectedFile == null )
			return; 

		File newSelectedFile = new File(getCurrentDirectory().getPath() + File.separator + previouslySelectedFile.getName());
		previouslySelectedFile = newSelectedFile;
		setSelectedFile(previouslySelectedFile);
	}

	class FileSelectedChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e) 
		{
			processFileSelected(e);
		}
	}
	
	void processFileSelected(PropertyChangeEvent e) 
	{
		File selectedFile = getSelectedFile();
		if ( previouslySelectedFile != null && ( selectedFile == null || selectedFile.isDirectory()) )
			setSelectedFile(previouslySelectedFile);
		else 
			previouslySelectedFile = getSelectedFile();
	} 

	static public final File NO_FILE_SELECTED = null;
	private File previouslySelectedFile = null;
}
