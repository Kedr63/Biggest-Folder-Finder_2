import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class FolderSizeCalculator extends RecursiveTask<Long> { //позволяет создавать разветляющие потоки,
    // которые потом можно собирать в едино
    private File folder;

    public FolderSizeCalculator(File folder) {
        this.folder = folder;
    }

    @Override
    protected Long compute() {
        if (folder.isFile()) {
            return folder.length();
        }

        long sum = 0;
        List<FolderSizeCalculator> subTasks = new LinkedList<>();
        File[] files = folder.listFiles();
        assert files != null;
        for (File file : files) {
            FolderSizeCalculator task = new FolderSizeCalculator(file);
            task.fork(); // запустим асинхронно
            subTasks.add(task);
        }

        for (FolderSizeCalculator task : subTasks) {
            sum += task.join(); // дождёмся выполнения задачи и прибавим результат
        }
        return sum;
    }
    public static String getHumanReadableSize(long size) {
        String humanReadableSize = "";
        long kiloByte = 1024;
        long megaByte = kiloByte * 1024;
        long gigoByte = megaByte * 1024;
        long teraByte = gigoByte * 1024;

        if (size < kiloByte) {
            humanReadableSize = size + "B";
        }
        if (size >= kiloByte && size < megaByte) {
            humanReadableSize = size / kiloByte + "Kb";
        }
        if (size >= megaByte && size < gigoByte){
            humanReadableSize = size / megaByte + "Mb";
        }
        if (size >= gigoByte && size < teraByte){
            humanReadableSize = size / gigoByte + "Gb";
        }
        if (size >= teraByte){
            humanReadableSize = size / teraByte + "Tb";
        }
        return humanReadableSize;
    }

}
