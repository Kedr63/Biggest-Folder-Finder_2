import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ForkJoinPool;

public class Main {
    public static final char[] sizeUnitOfMeasurements = {'B', 'K', 'M', 'G', 'T'};

    public static void main(String[] args) {
        // System.out.println(getSizeFromHumanReadable("149Gb"));
        // System.exit(0); // так можно завершить программу

        String folderPath = "/Users/aleksandrshabalin/Desktop/подкасты в дорогу дек14";
        File file = new File(folderPath);

        long start = System.currentTimeMillis();

        // С помощью многопоточности сделаем чтоб быстрее (через ForkJoinPool правильнее)
        FolderSizeCalculator calculator = new FolderSizeCalculator(file);
        ForkJoinPool pool = new ForkJoinPool(); // управляет количеством потоков которые одновременно работают
        long size = pool.invoke(calculator);
        String stringSize = getHumanReadableSize(size);
        System.out.println("Размер для user: " + stringSize);
        System.out.println("byte = " + size);
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
        for (int i = 0; i < sizeUnitOfMeasurements.length; i++) {
            double value = size / Math.pow(1024, i);
            if (value < 1024) {
                return Math.round(value) + "" + sizeUnitOfMeasurements[i] + (i > 0 ? "b" : "");
                // ⬆ Складывая объекты класса String с объектами других классов, мы приводим последние
                // к строковому виду. Преобразование объектов других классов к строковому представлению
                // выполняется через неявный вызов метода toString у объекта
                // (https://javarush.ru/groups/posts/2347-klass-string-v-java)
                // Поэтому здесь применили /""/, и их можно было поставить и вначале, главное применить
                // строковый символ (здесь пустой) чтоб привести объекты других классов к строковому виду
                // это такой хитрый способ
            }
        }
        return "Very big";
    }

    public static long getSizeFromHumanReadable(String size) {
        HashMap<Character, Integer> char2size = getSizeUnitOfMeasurements();
        char sizeFactor = size.replaceAll("[0-9\\s+]+", "")
                .charAt(0);
        int multiplier = char2size.get(sizeFactor);
        return multiplier * Long.parseLong(size.replaceAll("[^0-9]", ""));
    }

    private static HashMap<Character, Integer> getSizeUnitOfMeasurements() {
        HashMap<Character, Integer> char2size = new HashMap<>();
        for (int i = 0; i < sizeUnitOfMeasurements.length; i++) {
            char2size.put(sizeUnitOfMeasurements[i], (int) Math.pow(1024, i));
        }
        return char2size;
    }
}
