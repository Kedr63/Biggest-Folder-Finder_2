import java.io.File;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static void main(String[] args) {
        String folderPath = "/Users/aleksandrshabalin/Desktop/подкасты в дорогу дек14";
        File file = new File(folderPath);

        long start = System.currentTimeMillis();
        // С помощью многопоточности сделаем чтоб быстрее (через ForkJoinPool правильнее)
        FolderSizeCalculator calculator = new FolderSizeCalculator(file);
        ForkJoinPool pool = new ForkJoinPool(); // управляет количеством потоков которые одновременно работают
        long size = pool.invoke(calculator);
        System.out.println(size);
        /*System.out.println(file.length()); // (238 byte) т/к это директория, то размер будет не настоящий*/
        // System.out.println(getFolderSize(file));
        long duration = System.currentTimeMillis() - start;
        System.out.println(duration + "ms");
    }

    // создадим рекурсивный метод (это первый простой способ без многопоточности)
    public static long getFolderSize(File folder) {
        if (folder.isFile()) {  // если это файл
            return folder.length(); // то вернет размер файла
        }
        // далее получим список файлов в этой папке
        long sum = 0;
        File[] files = folder.listFiles();
        for (File file : files) {
            sum += getFolderSize(file);
        }
        return sum;
    }
}
