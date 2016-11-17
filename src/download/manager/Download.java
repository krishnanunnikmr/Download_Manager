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

    public static final int MAX_BUFFER_SIZE = 1024;
    public static final String STATUSES[] = {"Downloading", "Paused", "Complete", "Cancelled", "Error"};

    public static final int DOWNLOADING = 0;
    public static final int PAUSED = 1;
    public static final int COMPLETE = 2;
    public static final int CANCELLED = 3;
    public static final int ERROR = 4;

    public URL url;
    public int size;
    public int downloaded;
    public int status;

    public String fileNameNew;
    public int counter1 = 0;
    
    /*The reason for taking two dimensional byte array is to store the byte arrarys in another array. I don't want to read and write sequentially.
        My plan is to read all the byte arrays sequentially or separately and then to write sequentially. 
*/
    byte buffer1[][] = new byte[100000][MAX_BUFFER_SIZE];
    byte buffer2[][] = new byte[100000][MAX_BUFFER_SIZE];
    
    //These two read array are also for storing 'read' information from reading bytes.

    int[] read1 = new int[100000];
    int[] read2 = new int[100000];

    public Download(URL url) {
        this.url = url;
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;

        download();
    }

    public String getUrl() {
        return url.toString();
    }

    public int getSize() {
        return size;
    }

    public float getProgress() {
        return ((float) downloaded / size) * 100;
    }

    public int getStatus() {
        return status;
    }

    public void pause() {
        status = PAUSED;
        stateChanged();
    }

    public void cancel() {
        status = CANCELLED;
        stateChanged();
    }

    public void resume() {
        status = DOWNLOADING;
        stateChanged();
        download();
    }

    public void error() {
        status = ERROR;
        stateChanged();
    }

    public void download() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public String getFileName(URL url) {

        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf('/') + 1);

    }
    RandomAccessFile file = null;
    RandomAccessFile file2 = null;
    InputStream stream = null;
    InputStream stream2 = null;
    int a, b;
    abc ABC;

    public void run() {

        try {

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
            connection.connect();

           
            if (connection.getResponseCode() / 100 != 2) {
                error();
            }

            int contentLength = connection.getContentLength();

            if (contentLength < 1) {
                error();
            }
            System.out.println("Full Content is " + contentLength);

            if (size == -1) {
                size = 10000;           
/*Some of the place in the code I use 10000.It means that I want to download 10000 bytes at the first time.
                Then remaining bytes will be downloaded.
                */

                stateChanged();
            }

            fileNameNew = getFileName(url);
            file = new RandomAccessFile(getFileName(url), "rw");

            file.seek(downloaded);

            stream = connection.getInputStream();

            size = 10000;
            downloaded = 0;
            
            /*
            Here I make a function named 'function1' to download the first 10000 bytes.I use 'size' and 'downloaded' as parameter.  
            */
            
            function1(size, downloaded);

            System.out.println("Starting of FUNCTION2");
            
            /*
            'function2' downloads  from 100001(In original,I think 1000001+1215) to remainging bytes.
            */

            int sizeX = contentLength;
            int downloadedX = 10000 + 1215;
            int counter2 = finction2(sizeX, downloadedX);

            /*
            From this thread(Main thread) I run another thread from 'abc' class from 'function1'. So, I wait here untill that thread finishes its work. After 
            finishing I write the random access file with buffer1(from function1) and buffer2 (from function2).
            */
            
            ABC.join();
            
            /*
            From these part I write the file 
            equentially . That means, bytes downloaded from function1 and function2 are sequentially merged in the file . 
            */

            for (int i = 0; i <= (counter1); ++i) {
                file.write(buffer1[i], 0, read1[i]);
            }

            for (int i = 0; i < (counter2); ++i) {
                file.write(buffer2[i], 0, read2[i]);
            }

            if (status == DOWNLOADING) {
                System.out.println("Joss!!!");
                status = COMPLETE;
                stateChanged();

            }
            
            /*
            movingDirectory is the current directory and targerDirectory is what I choose from 'Select Folder' button.If I select nothing then the
            file will'be downloaded in the Project folder(By default).
            */

            String movingDirectory = DownloadManager.targetDirectory;
            System.out.println(movingDirectory);

        } catch (Exception e) {
            error();
        } finally {
            if (file != null) {
                
                

                String movingDirectory = DownloadManager.targetDirectory;
                String currentDirectory = System.getProperty("user.dir");

                try {
                    file.close();

                    try {
                        
                        //if there is a file created then finally that file will be moved to target directory

                        File afile = new File(currentDirectory + "\\" + getFileName(url));

                        if (afile.renameTo(new File(movingDirectory + "\\" + getFileName(url)))) {
                            System.out.println("File is moved successful!");
                        } else {
                            System.out.println("File is failed to move!");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stateChanged() {
        setChanged();
        notifyObservers();
    }

    public void function1(int size, int downloaded) throws IOException {
        
        /*
        Here, new Thread in abc class is started. But value of this 'Download' class is passed to 'abc' class. This is called Pass By Reference.
        So if there is a change in abc class then, the value of this class is changed . 
        */

        ABC = new abc(this);
        ABC.start();

    }

    public int finction2(int size, int downloaded) throws IOException, InterruptedException {
        
        /*
        Here, new Input Stream, HTTPURL connection, setRequestProperty all are set for this function.
        */
        

        InputStream streamX = null;
        HttpURLConnection connectionX = (HttpURLConnection) url.openConnection();
        connectionX.setRequestProperty("Range", "bytes=" + 0 + "-");
        connectionX.connect();
        streamX = connectionX.getInputStream();

        int counter2 = 0;
        int flag2 = 0;
        
        //As I want to download 10,001-remaining bytes for this URL , so I use 'skip' function for skipping the bytes. 

        System.out.println("Skipped " + (byte) streamX.skip(10000 + 1215));

        while (status == DOWNLOADING) {

            if (size - downloaded > MAX_BUFFER_SIZE) {

                System.out.println("First Size-downloaded Part2 " + (size - downloaded));
            } else {

                System.out.println("second Size-downloaded Part 2 " + (size - downloaded));
            }

            read2[counter2] = streamX.read(buffer2[counter2]);

            System.out.println("Read-- Part2 " + read2[counter2] + "\n");

            if (read2[counter2] == -1 || (size - downloaded) == 0) {
                System.out.println("It is broken! part2 \n");
                break;
            }
            
            /*
            This part is to actually seeing the original bytes 
            */

            System.out.println("Bytes from Buffer1 ar (Part 2) ");
            for (int i = 0; i < buffer2[counter2].length; ++i) {
                System.out.print(" " + buffer2[counter2][i] + " ");
            }
            System.out.print("\n");

            downloaded += read2[counter2];
            ++counter2;
            System.out.println("Hiji Biji- Part 2 " + counter2);
            stateChanged();

        }

        return counter2;

    }

}
