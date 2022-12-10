package org.example;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.util.*;
import java.util.concurrent.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String dir;
        int min, max;

        while(true) {
            System.out.println("Enter complete path to directory, min and max values separated by space (ex. '/home 0 10' ): ");
            String input = scanner.nextLine();
            String[] values = input.split(" ");
            if(checkInput(values)){
                dir = values[0];
                min = min(Integer.parseInt(values[1]), Integer.parseInt(values[2]));
                max = max(Integer.parseInt(values[1]), Integer.parseInt(values[2]));
                break;
            }
        }

        ExecutorService executorService= Executors.newCachedThreadPool();
        FolderProcessor folderProcessor = new FolderProcessor(executorService, dir, min, max);
        executorService.execute(folderProcessor);
    }

    public static boolean checkInput(String[] values){
        if(values == null)
            return false;
        if(values.length != 3)
            return false;
        try {
            File file = new File(values[0]);
            if(!file.isDirectory()){
                return false;
            }
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        try {
            Integer.parseInt(values[1]);
            Integer.parseInt(values[2]);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}

