/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package download.manager;

import static download.manager.Download.DOWNLOADING;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NowshadApu
 * 
 * This class is the actual 'function1' which runs in the different thread.And parallel thread is 'Main thread '. The 'Download' class is referenced 
 * in this class.So, when I change the different values in this class original values in the 'Download' class are also changed.
 */

/*
This class extends thread. that means it runs thread by calling run() method. Here, all value come from Download class .So, I had to be careful if those are 
changed accidently in this class.
*/
public class abc extends Thread {

    Download d;
    int result;

    public abc(Download dd) {
        
        //Here dd is the reference of original Download object. 
        d = dd;

    }
    
    //Now thread starts with the function calling run() .

    public void run() {
        
        /*
        In this function all values are changed in d.(something). That means all  changes are kept in Download object. 
        */
        
        int flag1 = 0;

        HttpURLConnection connection;
        try {

            connection = (HttpURLConnection) d.url.openConnection();
            connection.setRequestProperty("Range", "bytes=" + d.downloaded + "-");
            connection.connect();
            d.stream = connection.getInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        while (d.status == DOWNLOADING) {

            try {

                d.read1[d.counter1] = d.stream.read(d.buffer1[d.counter1]);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            System.out.println("Downloaded from Part 1 " + d.downloaded);

            System.out.println("Read-- Psrt1 " + d.read1[d.counter1] + "\n");
            System.out.println("size-downloaded Part1 " + (d.size - d.downloaded));

            if (flag1 == 1) {
                d.downloaded = d.size;
            }

            if (d.read1[d.counter1] == -1 || (d.size - d.downloaded) == 0) {
                System.out.println("It is broken! Part1\n");
                break;

            }

            if ((d.size - d.downloaded) <= d.MAX_BUFFER_SIZE) {
                flag1 = 1;
            }
            System.out.println("Bytes from Buffer1 ar (Part 1) ");
            for (int i = 0; i < d.buffer1[d.counter1].length; ++i) {
                System.out.print(" " + d.buffer1[d.counter1][i] + " ");
            }
            System.out.print("\n");

            d.downloaded += d.read1[d.counter1];

            ++d.counter1;

            System.out.println("Hiji Biji- Part1 " + d.counter1);
            d.stateChanged();
        }

    }

}
