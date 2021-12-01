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
        String stringSize = getHumanReadableSize(size);
        System.out.println(stringSize);
        System.out.println("byte = "+ size);
        System.out.println("после обратного перевода из пользовательского удобно читаемого = "
                + getSizeFromHumanReadable(stringSize));

        /*System.out.println(file.length()); // (238 byte) т/к это директория, то размер будет не настоящий*/

        // System.out.println(getFolderSize(file));
        long duration = System.currentTimeMillis() - start;
        System.out.println(duration + "ms");

        System.out.println(getSizeFromHumanReadable("235Kb")); // для проверки
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
    // Задание из вебинара: создать два метода
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

    public static long getSizeFromHumanReadable(String size){
        long kiloByte = 1024;
        long megaByte = kiloByte * 1024;
        long gigoByte = megaByte * 1024;
        long teraByte = gigoByte * 1024;
        long result = 0;
        if (size.contains("B")){
            result = Integer.parseInt(size.replaceAll("[^0-9]", ""));
        }
        if (size.contains("Kb")){
            result = Integer.parseInt(size.replaceAll("[^0-9]", "")) * kiloByte;
        }
        if (size.contains("Mb")){
            result = Integer.parseInt(size.replaceAll("[^0-9]", "")) * megaByte;
        }
        if (size.contains("Gb")){
            result = Integer.parseInt(size.replaceAll("[^0-9]", "")) * gigoByte;
        }
        if (size.contains("Tb")){
            result = Integer.parseInt(size.replaceAll("[^0-9]", "")) * teraByte;
        }
        return (long) result;
    }
}
