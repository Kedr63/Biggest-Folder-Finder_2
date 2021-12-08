import java.io.File;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class Main {
    static long sizeLimit = 50 * 1024 * 1024; // размер лимита в 50 МБ

    public static void main(String[] args) {
        // System.out.println(getSizeFromHumanReadable("149Gb"));
        // System.exit(0); // так можно завершить программу

        String folderPath = "/Users/aleksandrshabalin/Desktop/Брин ФМ4М 2021/";
        File file = new File(folderPath);


        // Создадим дерево (это будет корневая нода) и добавим туда нашу папку)
        Node root = new Node(file);

        long start = System.currentTimeMillis();

        // С помощью многопоточности сделаем чтоб быстрее (через ForkJoinPool правильнее)
        FolderSizeCalculator calculator = new FolderSizeCalculator(root);
        ForkJoinPool pool = new ForkJoinPool(); // управляет количеством потоков которые одновременно работают
        pool.invoke(calculator);
        String stringSize = SizeCalculator.getHumanReadableSize(root.getSize());
        System.out.println("Размер для user: " + stringSize);
        System.out.println("byte = " + root.getSize());
        System.out.println("после обратного перевода из пользовательского удобно читаемого = "
                + SizeCalculator.getSizeFromHumanReadable(stringSize));

        /*System.out.println(file.length()); // (238 byte) т/к это директория, то размер будет не настоящий*/

        // System.out.println(getFolderSize(file));
        long duration = System.currentTimeMillis() - start;
        System.out.println(duration + "ms");

        File listChild = root.getFolder();
        System.out.println(listChild);

        List<Node> nodeList = root.getChildren();
        printChildrenOfSize(nodeList, sizeLimit); // используем мой метод для фильтрации папок по размеру
    }

    /* // создадим рекурсивный метод (это первый простой способ без многопоточности)
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
        }*/

    // создадим рекурсивный метод который пройдется по всем /нодам/ и отфильтрует папки по размеру (мой способ)
    public static void printChildrenOfSize(List<Node> nodeList, long sizeLimit) {
        for (Node node : nodeList) {
            FolderSizeCalculator calculator = new FolderSizeCalculator(node);
            ForkJoinPool pool = new ForkJoinPool();
            pool.invoke(calculator);
            if (node.getFolder()
                    .isDirectory() && node.getSize() > sizeLimit) {
                System.out.println(node.getFolder() + " - " + SizeCalculator.getHumanReadableSize(node.getSize()));
                if (node.getChildren() != null){
                    printChildrenOfSize(node.getChildren(), sizeLimit);
                }
            }
        }
    }

}
