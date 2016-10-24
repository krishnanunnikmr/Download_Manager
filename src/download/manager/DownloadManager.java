/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package download.manager;

/**
 *
 * @author NowshadApu
 */

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.*;
import java.util.*;
import javafx.stage.DirectoryChooser;
import javax.swing.*;
import javax.swing.*;
import javax.swing.event.*;



    
    
    public class DownloadManager extends JFrame implements Observer
{
	private  JTextField addTextField;
	
	private DownloadsTableModel tableModel;
	
	private JTable table;
	
	private JButton pauseButton,resumeButton;
	
	private JButton cancelButton, clearButton;
        private JButton selectFolder,showDownloads;
	
	private static Download selectedDownload;

	private boolean clearing;
	
        static public String emni,targetDirectory;
        
        public static boolean selectionFlag=false;
	
	public DownloadManager()
	{
            
		setTitle("Download Manager");
		
		setSize(640, 480);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				actionExit();
			}
		});
		
                JMenuBar menuBar=new JMenuBar();
                JMenu fileMenu=new JMenu("File");
                fileMenu.setMnemonic(KeyEvent.VK_F);
                JMenuItem fileExitMenuItem=new JMenuItem("Exit",KeyEvent.VK_X);
                fileExitMenuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        actionExit();
                    }
                });
                
                
		fileMenu.add(fileExitMenuItem);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
                
                
		
		JPanel addPanel= new JPanel();
                
                selectFolder=new JButton("Select Folder");
                selectFolder.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        actionSelectFolder();
                    }
                });
                addPanel.add(selectFolder);
                selectFolder.setEnabled(true);
                
                
		addTextField= new JTextField(30);
		addPanel.add(addTextField);
                
                
                
                
                
		JButton addButton = new JButton("Add Download");
		addButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				actionAdd();
			}
		});
                
		addPanel.add(addButton);
		
                
		tableModel= new DownloadsTableModel();
		table= new JTable(tableModel);
		table.getSelectionModel().addListSelectionListener(new
		ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				tableSelectionChanged();
			}
		});
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ProgressRenderer renderer= new ProgressRenderer(0, 100);
		renderer.setStringPainted(true);
		table.setDefaultRenderer(JProgressBar.class, renderer);
		
		table.setRowHeight((int)renderer.getPreferredSize().getHeight());
		
		JPanel downloadsPanel= new JPanel();
		downloadsPanel.setBorder(
		BorderFactory.createTitledBorder("downloads"));
		downloadsPanel.setLayout(new BorderLayout());
		downloadsPanel.add(new JScrollPane(table),
		BorderLayout.CENTER);
		
		JPanel buttonsPanel= new JPanel();
                
                
                
                
                
                
		pauseButton= new JButton("pause");
		pauseButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        actionPause();
                    }
                });
		pauseButton.setEnabled(false);
		buttonsPanel.add(pauseButton);
                
                
		resumeButton= new JButton("Resume");
		resumeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				actionResume();
			}
		});	
		resumeButton.setEnabled(false);
		buttonsPanel.add(resumeButton);
                
                
		cancelButton= new JButton("cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				actionCancel();
			}
		});
		cancelButton.setEnabled(false);
		buttonsPanel.add(cancelButton);
                
                
		clearButton= new JButton("clear");
		clearButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				actionClear();
			}
		});
		clearButton.setEnabled(false);
		buttonsPanel.add(clearButton);
                
                
                
                showDownloads=new JButton("Show Downloads");
                showDownloads.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                            actionShowDownloads();
                    }
                });
                buttonsPanel.add(showDownloads);
                showDownloads.setEnabled(true);
                
                
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(addPanel, BorderLayout.NORTH);
		getContentPane().add(downloadsPanel, BorderLayout.CENTER);
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
                
                //String dfgh=selectedDownload.fileNameNew;
                //System.out.println(dfgh);
}

		private void actionExit(){
			System.exit(0);
		}
		
		private void actionAdd(){
			URL verifiedUrl= verifyUrl(addTextField.getText());
			if(verifiedUrl!= null){
				tableModel.addDownload(new Download(verifiedUrl));
				addTextField.setText("");
			}else{
				JOptionPane.showMessageDialog(this,
				"Invalid Download URL", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		private URL verifyUrl(String url)
                {
                    if(!url.toLowerCase().startsWith("http://"))
                        return null;
                    
                    URL verifiedUrl =null;
                    try
                    {
                        verifiedUrl=new URL(url);
                    }catch(Exception e)
                    {
                        return null;
                        
                    }
                    
                    if(verifiedUrl.getFile().length()<2)
                        return null;
                    
                    return verifiedUrl;
                }
                
                private void tableSelectionChanged()
                {
                    if(selectedDownload!=null)
                        selectedDownload.deleteObserver(DownloadManager.this);
                    
                    if(!clearing && table.getSelectedRow()>-1)
                    {
                        selectedDownload=tableModel.getDownload(table.getSelectedRow());
                        selectedDownload.addObserver(DownloadManager.this);
                        updateButtons();
                    }
                }
                
                
                
                public  void actionSelectFolder()
                {
                JFileChooser  fc=new JFileChooser();
                 fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                 if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
                 {
                     targetDirectory=String.valueOf(fc.getSelectedFile());
                     //System.out.println(targetDirectory);
                 }
                 //selectionFlag=true;
                }
                
               public static String getTargetDirectory()
               {
                   return targetDirectory;
               }
                
                private void actionPause()
                {
                    selectedDownload.pause();
                    updateButtons();
                
                }
                
                private void actionResume()
                {
                    selectedDownload.resume();
                    updateButtons();
                }
                
                private void actionCancel()
                {
                    selectedDownload.cancel();
                    updateButtons();
                }
                
                private void actionClear()
                {
                    clearing=true;
                    tableModel.clearDownload(table.getSelectedRow());
                    clearing=false;
                    selectedDownload=null;
                    updateButtons();
                }
                
               private void actionShowDownloads()
               {
                   try{
                   if(targetDirectory==null)
                   {
                       //String currentDirectory=System.getProperty("user.dir");
                       Desktop.getDesktop().open(new File(System.getProperty("user.dir")));
                   }
                   else
                   {
                       Desktop.getDesktop().open(new File(targetDirectory));
                      // System.out.println(targetDirectory);
                   }
                       }
                   catch(Exception ex)
                   {ex.printStackTrace();}
               }
                
                private void updateButtons()
                {
                    if(selectedDownload!=null)
                    {
                        int status=selectedDownload.getStatus();
                        
                        switch(status)
                        {
                            case Download.DOWNLOADING:
                                pauseButton.setEnabled(true);
                                resumeButton.setEnabled(false);
                                cancelButton.setEnabled(true);
                                clearButton.setEnabled(false);
                                break;
                                
                            case Download.PAUSED:
                                pauseButton.setEnabled(false);
                                resumeButton.setEnabled(true);
                                cancelButton.setEnabled(true);
                                clearButton.setEnabled(false);
                                break;
                                
                            case Download.ERROR:
                                pauseButton.setEnabled(false);
                                resumeButton.setEnabled(true);
                                cancelButton.setEnabled(false);
                                clearButton.setEnabled(true);
                                break;
                               
                            default:
                                pauseButton.setEnabled(false);
                                resumeButton.setEnabled(false);
                                cancelButton.setEnabled(false);
                                clearButton.setEnabled(true);
                                
                        }
                    }
                    else
                    {
                        pauseButton.setEnabled(false);
                                resumeButton.setEnabled(false);
                                cancelButton.setEnabled(false);
                                clearButton.setEnabled(false);
                    }
                }
                
                public void update(Observable o,Object arg)
                {
                    if(selectedDownload!=null && selectedDownload.equals(o))
                        
                        SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            updateButtons();
                        }
                    });
                }
                
               /* public static String returnString()
                {
                    return selectedDownload.fileNameNew;
                }
                */
                
                
                public static void main(String[] args)
                {
                    
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            DownloadManager manager=new DownloadManager();
                            manager.setVisible(true);
                            
                            
                                   
                        }
                    });
                    
                    
                     
                  
                   /*String movingDirectory=DownloadManager.getTargetDirectory();
                   System.out.println(currentDirectory);
                    System.out.println(movingDirectory); 
                    */
                
                
                
                
                
                }
                
                
                        
		
	}

    
    
    

