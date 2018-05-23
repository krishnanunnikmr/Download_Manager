# Download_Manager
This project is made for our academic projct of **Networking lab**. It is the project which is described in _Java- The Complete Reference_. But some extra features are added.

## Getting Started
**JAVA- The Complete Reference** is one of the nicest books I've ever read for JAVA language by **_Herbert Schildt_**. There is a project in the last chapter named **Download Manager**. It is a nice project but has some limitations. For individual download, it creates a separate thread. But for a particular file download, it uses only one thread. If we can download a file using multiple threads, wouldn't be it nice? That's the project I did in my _Networking Lab_.  

![alt text](https://github.com/nowshad-hasan/Download_Manager/blob/master/Screenshots/screenshot_1.png)

We all know that file is downloaded _byte by byte_. So, I save those bytes from the server in different ArrayList from different threads. After downloading the last byte, I merged those downloaded bytes from ArrayList sequentially. Finally, the **_File to be Downloaded_** is made. Here is the screenshot of those bytes of the downloading file.

![alt text](https://github.com/nowshad-hasan/Download_Manager/blob/master/Screenshots/screenshot_3.JPG)

After finishing the download, the file is saved on the destination folder. 

![alt text](https://github.com/nowshad-hasan/Download_Manager/blob/master/Screenshots/screenshot_2.JPG)

### Prerequisites 
You need to install _JAVA_ for running this project. Please check by typing `java -version` if _JAVA_ is installed or not. 

### Installation
 Go to **_Download_Manager/dist_** folder or click the link https://github.com/nowshad-hasan/Download_Manager/tree/master/dist.
 Download the **Download_Manager.jar** file </br> and run `java -jar Download_Manager.jar` in _terminal or cmd_.
