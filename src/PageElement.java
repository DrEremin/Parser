import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

import lombok.Data;
import org.jsoup.nodes.Element;

/**
 * Класс PageElement является шаблоном для создания объектов,
 * содержащих ссылку на каждую вакансию, ее заголовок и
 * дату ее публикации на странице сайта
 */

@Data
public class PageElement implements Comparable<Calendar> {

    public static final String[] MONTHS = new String[]{"янв", "фев", "мар",
            "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек"};
    final static int[] FIELDS = new int[]{5, 2, 1, 11, 12}; // нужные поля класса Calendar

    public static int ageOfVacancy;        // допустиммая давность вакансии (в месяцах)
    private final String linkOnVacancy;    // ссылка на вакансию
    private final String titleOfVacancy;   // заголовок вакансии
    private boolean invalidDate;           // признак неверного формата даты (такие элементы не добавляются)
    private Calendar date;                 // дата вакансии

    /*
     * Конструктор с параметрами
     */

    public PageElement(Element topic, Element stringDate) {
        this.linkOnVacancy = topic.attr("href");
        this.titleOfVacancy = topic.ownText();
        this.invalidDate = false;
        stringToDate(stringDate.ownText());
    }

    /*
     * Метод определения соответствия строки-даты и шаблона
     */

    public boolean isDate(String stringDate) {
        boolean flag1 = stringDate.matches(
                "[0-9]{1,2}\\s[авгдеийклмнопрстфюя]{3}\\s[0-9]{2},\\s[0-9]{2}:[0-9]{2}");
        boolean flag2 = stringDate.matches("вчера,\\s[0-9]{2}:[0-9]{2}");
        boolean flag3 = stringDate.matches("сегодня,\\s[0-9]{2}:[0-9]{2}");
        if (flag1 || flag2 || flag3) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * Метод определения: находится ли срок давности вакансии,
     * в допустимом диапазоне
     */

    public boolean isPermittedDate() {
        Calendar border = new GregorianCalendar();
        border.add(Calendar.MONTH, -ageOfVacancy);
        return date.after(border);
    }

    /*
     * Метод конвертации даты из строки в класс Calendar
     */

    public void stringToDate(String stringDate) {
        if (!isDate(stringDate)) {
            date = new GregorianCalendar(1900, Calendar.JANUARY, 1, 0, 0);
            invalidDate = true;
            return;
        }
        Integer value;
        stringDate = stringDate.trim();
        stringDate = stringDate.replace(",", "");
        stringDate = stringDate.replace(":", " ");
        String[] valuesOfFields = stringDate.split("[\\s]");
        date = new GregorianCalendar();
        if (valuesOfFields.length == 5) {
            for (int i = 0; i < valuesOfFields.length; i++) {
                if (i == 1) {
                    value = month(valuesOfFields[i]);
                    if (value < 0) {
                        date = new GregorianCalendar(1900, Calendar.JANUARY, 1, 0, 0);
                        invalidDate = true;
                        //logg
                        return;
                    }
                } else if (i == 2) {
                    value = Integer.parseInt("20" + valuesOfFields[2]);
                } else {
                    value = Integer.parseInt(valuesOfFields[i]);
                }
                date.set(FIELDS[i], value);
            }
        } else {
            if ("вчера".equals(valuesOfFields[0])) {
                date.roll(5, false);
            }
            for (int i = 1; i < valuesOfFields.length; i++) {
                value = Integer.parseInt(valuesOfFields[i]);
                date.set(FIELDS[i + 2], value);
            }
        }
    }

    /*
     * Метод конвертации названия месяца в целочисленное значение.
     * Если название месяца в строке с датой несоответствует
     * ни одному элементу константного массива с названиями месяцев,
     * то возвращается -1
     */

    public int month(String str) {
        for (int i = 0; i < MONTHS.length; i++) {
            if (MONTHS[i].equals(str)) {
                return i;
            }
        }
        return -1;
    }

    /*
     * Переопределение методов equals(), hashCode(), toString(), compareTo()
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageElement that = (PageElement) o;
        return Objects.equals(linkOnVacancy, that.linkOnVacancy) &&
                Objects.equals(titleOfVacancy, that.titleOfVacancy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkOnVacancy, titleOfVacancy);
    }

    @Override
    public String toString() {
        SimpleDateFormat formatDate = new SimpleDateFormat("dd MMM yyyy, HH:mm");
        return "<b>Вакансия: </b>" + titleOfVacancy + "<br><b>Дата публикации: </b>" +
                formatDate.format(date.getTime()) + " <br><b>Сcылка: </b><a href = " + linkOnVacancy + ">" +
                linkOnVacancy + "</a>";
    }

    @Override
    public int compareTo(Calendar other) {
        return this.date.compareTo(other);
    }

    /*
     * Геттер поля invalidDate
     */

    public boolean getInvalidDate() {
        return invalidDate;
    }
}
