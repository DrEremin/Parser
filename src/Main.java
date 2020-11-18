//import java.util.logging.Logger;

public class Main {

    public static final String SITE = "https://www.sql.ru/forum/job-offers";
    //private static final Logger myLogger = Logger.getLogger(Main.class.getName());
    //логирование пока не сделал

    public static void main(String[] args) {

        String[] keyWords = new String[args.length - 2];
        for (int i = 0; i < keyWords.length; i++) {       //заполнение массива строк ключевыми словами
            keyWords[i] = args[i + 2];
        }
        if (argCheck(args[0], args[1])) {
            Page page = new Page(SITE, keyWords, Integer.parseInt(args[1]));
            page.parsePages();
            VacancyToFile out = new VacancyToFile();
            out.output();
        } else {
            System.out.println("Некорректный ввод аргумента\nПрограмма завершает работу");
            //логирование пока не сделал
        }
    }

    /*
     * Метод проверки корректности ввода аргументов коммандной строки
     */

    public static boolean argCheck(String link, String arg1) {

        int months;
        try {
            months = Integer.parseInt(arg1);
        } catch (NumberFormatException e) {
            months = -1;
        }
        boolean matchArg1 = link.matches("(https://www.sql.ru)|(sql.ru)|(" + SITE + ")");
        boolean matchArg2 = months <= 12 && months > 0;
        return matchArg1 && matchArg2;
    }
}
