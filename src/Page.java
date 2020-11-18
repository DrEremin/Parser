import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Класс Page является шаблоном для создания объекта
 * конкретной страницы сайта. Объект данного класса позволяет
 * проводить синтаксический анализ DOM каждой страницы и загружать
 * следующую для дальнейшего парсинга. Результатом синтаксического
 * анализа страницы является список объектов класса PageElement.
 */

@Data
public class Page {
    public static final int TIMEOUT = 60000;         // время ожидания соединения с сайтом (в миллисекундах)
    private static List<PageElement> listOfElements; // список всех объектов PageElement на сайте
    private String[] keyWords;                       // массив ключевых слов
    private boolean isProceed;                       // признак продолжения синтаксического анализа страниц сайта
    private Elements allTopics;                      // все топики вакансий на текущей странице
    private Element nextPage;                        // элемент текущей страницы для поиска ссылки на следующую
    private URL linkOnPage;                          // объект класса URL для связи со страницей сайта
                                                     // в заданном диапазоне дат
    /*
     * Конструктор с тремя параметрами
     */

    public Page(String linkOnPage, String[] keyWords, int ageOfVacancy) {
        if (linkOnPage == null || keyWords == null) {
            //logg
            throw new NullPointerException();
        }
        try {
            this.linkOnPage = new URL(linkOnPage);
            this.isProceed = true;
            this.keyWords = keyWords;
            this.allTopics = null;
            /*this.keyTopics = new Elements();
            this.dates = new Elements();*/
            listOfElements = new ArrayList<>();
            PageElement.ageOfVacancy = ageOfVacancy;
        } catch (MalformedURLException e) {
            this.linkOnPage = null;
            //логирование пока не сделал
        }
    }

    /*
     * Метод загрузки содержимого html-документа страницы,
     * заключенного внутри тега <body></body>
     */

    public Element bodyOfPage() {
        try {
            return Jsoup.parse(linkOnPage, TIMEOUT).body();
        } catch (IOException e) {
            //логирование пока не сделал
            return null;
        }
    }

    /*
     * Метод загрузки списка элементов, которые содержат топики вакансий
     */

    public void setAllTopics() {
        allTopics = bodyOfPage().select("td.postslisttopic");
    }

    /*
     * Метод содания добавления в список, объектов, содержащих ссылки, заголовки
     * и даты публикации ключевых вакансий на текущей странице
     */

    public void addElementsToList() {
        boolean flag;
        Element element;
        Iterator<Element> iterator = allTopics.iterator();
        while (iterator.hasNext()) {
            element = iterator.next().child(0);
            flag = false;

            /*
             * анализ текущего топика по ключевым словам, создание объекта PageElement
             * в случае соответствия хотя бы одному из них, добавление PageElement в общий список
             */

            for (int i = 0; i < keyWords.length; i++) {
                if (element.ownText().matches(".*[" + keyWords[i].toLowerCase() +
                        keyWords[i].toUpperCase() + "]{" + keyWords[i].length() + "}[\\s\\W]+.*")) {
                    flag = true;
                }
            }
            if (flag) {
                PageElement pageElement = new PageElement(element, element.parent().lastElementSibling());
                if (pageElement.isPermittedDate()) {     // если дата объекта укладывается в диапазон
                    listOfElements.add(pageElement);
                } else {
                    if(!pageElement.getInvalidDate()) {  // если у объекта корректная дата
                        isProceed = false;               // и она не укладывается в диапазон
                    }
                }
            }
        }
    }

    /*
     * Метод извлечения ссылки на следующую страницу сайта
     * для дальнейшего синтаксического анализа
     */

    public void flipPage() {
        Elements elements = bodyOfPage().getElementsByClass("sort_options");
        nextPage = elements.last().selectFirst("b").nextElementSibling();
        try {
            linkOnPage = new URL(nextPage.attr("href"));
        } catch (MalformedURLException e) {
            this.linkOnPage = null;
            //логирование пока не сделал
        }
    }

    /*
     * Метод прохода по всем страницам, пока даты вакансий подходящие
     */

    public void parsePages () {
        while (isProceed) {
            setAllTopics();
            addElementsToList();
            flipPage();
        }
        //System.out.println(listOfElements);
    }

    /*
     * Геттер списка объектов PageElement
     */

    public static Iterable<? extends PageElement> getListOfElements() {
        return listOfElements;
    }
}