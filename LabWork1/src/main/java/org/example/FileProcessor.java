package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FileProcessor implements Runnable{
    private static final Semaphore sem = new Semaphore(1);
    private static Integer threadNum;
    private final String instanceThreadNum;
    private final String filename;
    private final int min;
    private final int max;

    public FileProcessor(String filename, int min, int max){
        threadNum = (threadNum==null)?0:threadNum+1;
        instanceThreadNum = "FileProcessor Thread #"+threadNum;

        this.filename = filename;
        this.min = min;
        this.max = max;
    }

    @Override
    public void run() {
        System.out.println(instanceThreadNum + " started.");
        try {
            List<String> allLines = Files.readAllLines(Paths.get(filename));
            List<String> result = new ArrayList<>();

            for (String line : allLines) {
                Pattern pattern = Pattern.compile("[0-9]+");
                Matcher matcher = pattern.matcher(line);
                List<Integer> intList = new ArrayList<>();
                while (matcher.find()) {
                    Integer match = Integer.parseInt(matcher.group());
                    intList.add(match);
                }
                if (intList.size() > 0) {
                    for (Integer elem : intList) {
                        if (elem >= min && elem <= max) {
                            result.add(line);
                            break;
                        }
                    }
                }
            }
            if(result.size() > 0) {
                File f = new File(filename);
                result.add(0, ">>>>>>>> " + f.getName() + " <<<<<<<<");
                result.add("");
                result.add(0, "");
                try{
                    sem.acquire();
                    System.out.println("Data output. Other threads are paused");
                    for(String elem : result) System.out.println(elem);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    System.out.println("Output finished. Threads released");
                    sem.release();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(instanceThreadNum + " finished.");
    }
}
