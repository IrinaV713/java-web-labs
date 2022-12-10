package org.example;

import java.io.File;
import java.util.concurrent.ExecutorService;

class FolderProcessor implements Runnable {
    private static Integer threadNum;
    private final String instanceThreadNum;
    private final String directory;
    private final int min;
    private final int max;
    private final ExecutorService executorService;

    public FolderProcessor(ExecutorService executorService, String directory, int min, int max) {
        threadNum = (threadNum==null)?0:threadNum+1;
        instanceThreadNum = "FolderProcessor Thread #"+threadNum;

        this.directory = directory;
        this.min = min;
        this.max = max;

        this.executorService = executorService;
    }

    @Override
    public void run() {
        System.out.println(instanceThreadNum + " started.");

        File rootDir = new File(directory);
        File[] listOfFiles = rootDir.listFiles(f -> !f.isHidden());
        if(listOfFiles == null) {
            return;
        }
        for (File file : listOfFiles) {
            if (file.isDirectory()){
                FolderProcessor folderProcessor = new FolderProcessor(executorService, file.toString(), min, max);
                executorService.execute(folderProcessor);
            } else if (file.isFile() && file.toString().endsWith(".cs")) {
                FileProcessor fileProcessor = new FileProcessor(file.toString(), min, max);
                executorService.execute(fileProcessor);
            }
        }
        System.out.println(instanceThreadNum + " finished.");
    }
}
