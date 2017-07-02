package CopyFilesFromUSB;

import org.apache.commons.io.FileUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Created by Aingaran on 02-07-2017.
 * This is a class to copy files from removable drive to a local location.
 */
public class CopyFilesFromUSB {

    private final static String DRIVE_TYPE_REMOVABLE = "Removable Disk";
    private final static String LOCAL_FILE_PATH = "C:\\Copied from USB\\";
    private int noOfDrivesAvailable;

    private void setNoOfDrivesAvailable(int noOfDrivesAvailable)    {this.noOfDrivesAvailable = noOfDrivesAvailable;}
    private int getNoOfDrivesAvailable()    {return this.noOfDrivesAvailable;}

    public static void main(String args[])  {
        CopyFilesFromUSB copyFilesFromUSB = new CopyFilesFromUSB();
        copyFilesFromUSB.setNoOfDrivesAvailable(File.listRoots().length);
        while(true) {
            if(copyFilesFromUSB.driveListChange(copyFilesFromUSB.getNoOfDrivesAvailable())) {
                File[] paths;
                FileSystemView fsv = FileSystemView.getFileSystemView();

                // returns pathNames for files and directory
                paths = File.listRoots();

                // for each pathname in pathNames array
                for(File path:paths)
                {
                    if(fsv.getSystemTypeDescription(path).equalsIgnoreCase(DRIVE_TYPE_REMOVABLE))   {
                        System.out.println("Drive Name: "+path);
                        copyFilesFromUSB.copyFiles(path);
                    }
                }
            }
            try {
                Thread.sleep(100);
            }   catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean driveListChange(int noOfDrivesAvailable)   {
        if(File.listRoots().length != noOfDrivesAvailable)  {
            if(File.listRoots().length > noOfDrivesAvailable)   {
                System.out.println("New Removable Drive(s) added");
                this.noOfDrivesAvailable = File.listRoots().length;
                return true;
            }   else    {
                System.out.println("Existing Removable Drive(s) removed");
                this.noOfDrivesAvailable = File.listRoots().length;
                return false;
            }
        }
        return false;
    }

    private void copyFiles(File path) {
        try {
            ArrayList<String> files = new ArrayList<>();
            listFilesAndFilesSubDirectories(path.toString(),files);
            String destinationFolder = LOCAL_FILE_PATH + LocalDate.now() + "_" + LocalTime.now().getHour() + "." + LocalTime.now().getMinute() + "." + LocalTime.now().getSecond();
            if(new File(destinationFolder).exists())    {
                FileUtils.forceDelete(new File(destinationFolder));
            }
            for(String file : files)    {
                if(!file.contains("System Volume Information")) {
                    System.out.println("Copying file from " + file + " to " + destinationFolder + "\\" + file.substring(3));
                    FileUtils.copyFile(new File(file),new File(destinationFolder + "\\" + file.substring(3)));
                    System.out.println("File Copied");
                }
            }
            System.out.println("All file(s) Copied");
        }   catch(IOException e)    {
            e.printStackTrace();
        }
    }

    private void listFilesAndFilesSubDirectories(String directoryName , ArrayList<String> files){
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fileList = directory.listFiles();
        for (File file : fileList){
            if (file.isFile()){
                files.add(file.getAbsolutePath());
            } else if (file.isDirectory()){
                listFilesAndFilesSubDirectories(file.getAbsolutePath(),files);
            }
        }
    }
}
