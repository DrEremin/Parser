import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import lombok.Data;

/**
 * Класс VacancyToFile описывает объект, который парсит страницу каждой вакансии
 * из списка объектов класса PageElement, создает html-файл  и записывает в него
 * искомые элементы.
 */

@Data
public class VacancyToFile {

    public static final int TIMEOUT = 60000;   // время ожидания соединения с сайтом (в миллисекундах)
    private Element vacancy;                   // поле для хранения очередного искомого элемента
    private URL linkOnPage;                    // объект класса URL для связи со страницей вакансии
    PrintWriter htmlPrint;                     // поле потока куда идет запись результатов

    /*
     * конструктор класса без параметров
     */

    public VacancyToFile() {
        fileCreate();
        this.vacancy = null;
        this.linkOnPage = null;
    }

    /*
     * Метод загрузки содержимого html-документа страницы вакансии,
     * заключенного внутри тега <body></body>
     */

    public Element bodyOfPage() {
        try {
            return Jsoup.parse(linkOnPage, TIMEOUT).body();
        } catch (IOException e) {
            //логирование
            return null;
        }
    }

    /*
     * Метод создания файла для записи результатов
     */

    public void fileCreate() {
        File htmlFile = new File("/home/ivan", "1.html");
        try {
            htmlFile.createNewFile();
            htmlPrint = new PrintWriter (new FileWriter(htmlFile), true);
        } catch (IOException e) {
            System.out.println("no file");
            //logg
            return;
        }
    }

    /*
     * Метод вывода результатов в файл
     */

    public void output () {
        htmlPrint.println("<!DOCTYPE html>\n<html>\n\t<head>\n\t\t<meta charset = \"UTF-8\">" +
                "\n\t\t<title>Подборка вакансий</title>\n\t</head>\n\t<body>");
        for (PageElement pageElement : Page.getListOfElements()) {
            try {
                linkOnPage = new URL(pageElement.getLinkOnVacancy());
                vacancy = bodyOfPage().selectFirst("table.msgTable").select("td.msgBody").last();
                htmlPrint.println("\t\t<p>" + pageElement + "</p>");
                htmlPrint.println("\t\t" + vacancy + "<hr>");
            } catch (MalformedURLException e) {
                this.linkOnPage = null;
                //logg
            }
        }
        htmlPrint.println("\t</body>\n<html>");
        htmlPrint.close();
    }
}