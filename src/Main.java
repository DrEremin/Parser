public class Main {
    public static void main(String[] args)  {

        final String SITE = "https://www.sql.ru/forum/job-offers";
        String[] str = new String[]{"разработчик"};
        Page page = new Page(SITE, str, 3);

        page.parsePages();
        VacancyToFile out = new VacancyToFile();
        out.output();
    }
}
    /*final String SITE = "https://www.sql.ru/forum/job-offers"; separatorChar*/