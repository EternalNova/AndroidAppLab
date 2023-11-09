package com.example.lab1;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;

public class ParsingHtmlService {
    private static final String URL = "http://mirkosmosa.ru/holiday/2021";
    public static String getHoliday(String date) {
        Document document = null;
        try {
            document = Jsoup.connect(URL).get();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        Element body = document.body();
        Elements month_rows = body.getElementsByClass("month_row");
        Element right_row = null;
        for (Element month_row: month_rows
             ) {
            Element month_cell_date = month_row.getElementsByClass("month_cel_date").first();
            String holiday_date = month_cell_date.getElementsByTag("span").first().text();
            if (Objects.equals(holiday_date, date)){
                right_row = month_row;
                break;
            }
        }
        Element month_cell = right_row.getElementsByClass("month_cel").first();
        Elements list = month_cell.getElementsByClass("holiday_month_day_holiday").first().getElementsByTag("li");
        String out = "";
        for (Element l: list
             ) {
            out += l.getElementsByTag("a").first().text() + " ";
        }
        if (out.length() == 0)
            return "Нет праздников";
        else
            return out;
    }
}
