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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;
import java.util.*;
import java.net.*;
import java.nio.file.Path;


public class Download extends Observable implements Runnable {
    
    private static final int MAX_BUFFER_SIZE=1024;
    public static final String STATUSES[]={"Downloading","Paused","Complete","Cancelled","Error"};
    
    public static final int DOWNLOADING=0;
    public static final int PAUSED=1;
    public static final int COMPLETE=2;
    public static final int CANCELLED=3;
    public static final int ERROR=4;
    
    private URL url;
    private int size;
    private int downloaded;
    private int status;
    
    public String fileNameNew;
    
    public Download(URL url)
    {
        this.url=url;
        size=-1;
        downloaded=0;
        status=DOWNLOADING;
        
        download();
    }
     public String getUrl()
     {
         return url.toString();
     }
     
     public int getSize()
     {
         return size;
     }
     
     public float getProgress()
     {
         return ((float)downloaded/size)*100;
     }
     
     public int getStatus(){
         return status;
     }
     
     public void pause()
     {
         status=PAUSED;
         stateChanged();
     }
     
     public void cancel()
     {
         status=CANCELLED;
         stateChanged();
     }
     
     public void resume()
     {
         status=DOWNLOADING;
         stateChanged();
         download();
     }
     
     private void error()
     {
         status=ERROR;
         stateChanged();
     }

    

    private void download() {
        Thread thread=new Thread(this);
        thread.start();
    }
    
    public String getFileName(URL url)
    {
        String fileName=url.getFile();
        return fileName.substring(fileName.lastIndexOf('/')+1);
        
        
    }
    
     public void run ()
     {
         RandomAccessFile file=null;
         InputStream stream=null;
         
         try{
             HttpURLConnection connection=(HttpURLConnection)url.openConnection();
             connection.setRequestProperty("Range", "bytes="+downloaded+"-");
             connection.connect();
             
             if(connection.getResponseCode()/100!=2)
             {
                 error();
             }
             
             int contentLength=connection.getContentLength();
             if(contentLength<1)
                 error();
             
             if(size==-1)
             {
                 size=contentLength;
                 stateChanged();
             }
             
             fileNameNew=getFileName(url);
             file=new RandomAccessFile(getFileName(url),"rw");
             file.seek(downloaded);
             //System.out.println(fileNameNew);
             
             
             stream=connection.getInputStream();
             while(status==DOWNLOADING)
             {
                 byte buffer[];
                 if(size-downloaded>MAX_BUFFER_SIZE)
                     buffer=new byte[MAX_BUFFER_SIZE];
                 else
                     buffer=new byte[size-downloaded];
                 
                 int read=stream.read(buffer);
             if(read==-1)
                 break;
             
             file.write( buffer,0,read);
             downloaded+=read;
             stateChanged();
             }
             
             if(status==DOWNLOADING)
             {
                 status=COMPLETE;
                 stateChanged();
             
             }
             
             
             String movingDirectory=DownloadManager.targetDirectory;
             System.out.println(movingDirectory);
            /* String currentDirectory=System.getProperty("user.dir");
                    
             
             
             
            // System.out.println(currentDirectory+" "+movingDirectory);
             
             try{

    	   File afile =new File(currentDirectory+"\\"+getFileName(url));

    	   if(afile.renameTo(new File(movingDirectory+"\\"+getFileName(url)+afile.getName()))){
    		System.out.println("File is moved successful!");
    	   }else{
    		System.out.println("File is failed to move!");
    	   }

    	}catch(Exception e){
    		e.printStackTrace();
    	}
             */
             
         }catch(Exception e)
         {
             error();
         }
         finally
         {
             if(file!=null)
             {
                 
                 
                 String movingDirectory=DownloadManager.targetDirectory;
             String currentDirectory=System.getProperty("user.dir");
                 
                 try
                 {
                     file.close();
                     
                     try{

    	   File afile =new File(currentDirectory+"\\"+getFileName(url));

    	   if(afile.renameTo(new File(movingDirectory+"\\"+getFileName(url)))){
    		System.out.println("File is moved successful!");
    	   }else{
    		System.out.println("File is failed to move!");
    	   }

    	}catch(Exception e){
    		e.printStackTrace();
    	}
                     
                     
                     
                     
                 }catch(Exception e)
                 {
                     e.printStackTrace();
                 }
             }
             
             if(stream!=null)
             {
                 try{
                     stream.close();
                 }catch(Exception e)
                 { e.printStackTrace();}
             }
         }
     }
     
     private void stateChanged() {
        setChanged();
        notifyObservers();
    }
    
}
