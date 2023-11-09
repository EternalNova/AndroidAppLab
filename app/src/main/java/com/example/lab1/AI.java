package com.example.lab1;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.telecom.Call;
import android.util.Log;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AI {
    /**
     *dictionary - Словарь с вопросами/ответами
     */
    private Map<String, String> dictionary = Stream.of(new String[][]
            {
                    {"привет", "Привет"},
                    {"приветик", "Ку"},
                    {"как дела?", "Не плохо"},
                    {"чем занимаешься?", "Отвечаю на вопросы"},
                    {"а чем занимаешься?", "Отвечаю на вопросы"}
            }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    /**
     * months - Словарь с номерами и названиями месяцов
     */
    private Map<String, String> months = Stream.of(new String[][]
            {
                    {"01", "января"},
                    {"02", "февраля"},
                    {"03", "марта"},
                    {"04", "апреля"},
                    {"05", "мая"},
                    {"06", "июня"},
                    {"07", "июля"},
                    {"08", "августа"},
                    {"09", "сентября"},
                    {"10", "октября"},
                    {"11", "ноября"},
                    {"12", "декабря"}
            }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    /**
     * opMap - Словарь который хранит вопросы : методы
     * Например: который час -> метод, возвращающий текущий час
     */
    private Map<String, Callable<String>> opMap;
    /**
     * parMap - Словарь который хранит вопросы : функции с 1 параметром
     * Например: сколько дней до dd.mm.yyyy -> функция, возвращающая кол-во дней до заданной даты
     */
    private Map<String, Function<String, String>>parMap;
    public void getAnswer(String question, final Consumer<String> callback) {
        question = question.toLowerCase();
        //смотрим ответы из dictionary
        final String[] answer = {dictionary.get(question)};
        //смотрим ответы из opMap
        Callable<String> op = opMap.get(question);
        if (op != null) {
            try {
                answer[0] = op.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            Pattern pattern = Pattern.compile("(до)");
            Matcher matcher = pattern.matcher(question);
            matcher.find();
            //смотрим ответы из parMap, если нашли ключевое слово "до"
            Function<String, String> par = parMap.get(question.substring(0, matcher.end())); //
            if (par != null) {
                try {
                    answer[0] = par.apply(question.substring(matcher.start()+3));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Pattern cityPattern = Pattern.compile("погода в городе (\\p{L}+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = cityPattern.matcher(question);
        //смотрим погоду
        if (matcher.find()) {
            String cityName = matcher.group(1);
            ForecastToString.getForecast(cityName, new Consumer<String>() {
                @Override
                public void accept(String s) {
                    answer[0] = correctEnding(s);
                    getWord(answer[0], new Consumer<String>() {
                        @Override
                        public void accept(String z) {
                            answer[0] = z;
                            callback.accept(answer[0]);
                        }
                    });

                }
            });
        }
        else {
            //смотрим праздники
            Log.w("holiday", "Подходит ли строчка");
            Pattern holidayPattern = Pattern.compile("какой праздник (\\d{2}\\.\\d{2}\\.\\d{4})", Pattern.CASE_INSENSITIVE);
            matcher = holidayPattern.matcher(question);
            if (matcher.find()){
                Log.w("holiday", "Какой праздник спросили");
                String date2find = getDate(matcher.group(1));
 /*               new AsyncTask<String, Integer, Void>(){
                    @Override
                    protected Void doInBackground(String... strings) {
                        String holiday = ParsingHtmlService.getHoliday(strings[0]);
                        answer[0] = holiday;
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        callback.accept(answer[0]);
                    }
                }.execute(date2find);*/
                Observable.fromCallable(() -> {
                    String holiday = ParsingHtmlService.getHoliday(date2find);
                    answer[0] = holiday;
                    return answer;
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                callback.accept(result[0]);
                });
            }
            else {
                //если ничего не нашли то значит не поняли вопроса
                if (answer[0] == null)
                    answer[0] = "Я вас не понял";
                callback.accept(answer[0]);
            }
        }
    }
    public AI(){
        opMap = new HashMap<>();
        parMap = new HashMap<>();
        opMap.put("который час", () -> {return Integer.toString(LocalDateTime.now().getHour())+":"+Integer.toString(LocalDateTime.now().getMinute());});
        opMap.put("какой сегодня день", () -> {return Integer.toString(LocalDateTime.now().getDayOfMonth()) + ' ' +
                LocalDateTime.now().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());});
        opMap.put("какой день недели", () -> { return LocalDateTime.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());});
        parMap.put("сколько дней до", (String day) -> {return Long.toString(getDaysBetween(day));});

    }
    private long getDaysBetween(String date){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date1 = LocalDate.parse(date, dtf);

        return Math.abs(Duration.between(date1.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays());
    }
    private String correctEnding(String str){
        Pattern ptrn = Pattern.compile("([+-]?\\d+) (градуса)", Pattern.CASE_INSENSITIVE);
        Matcher matcher  = ptrn.matcher(str);
        if (matcher.find()){
            String number = matcher.group(1);
            String deg = matcher.group(2);
            int i = Integer.parseInt(number);
            int last_digit = Integer.parseInt(Character.toString(number.charAt(number.length()-1)));
            if (Math.abs(i) == 1)
                deg = "градус";
            else if (last_digit > 4)
                deg = "градусов";
            String out = matcher.replaceAll(number + ' ' + deg);
            return out;
        }
        return str;
    }
    private String getDate(String raw_string){
        Pattern p = Pattern.compile("(\\d{2})\\.(\\d{2})\\.(\\d{4})");
        Matcher m = p.matcher(raw_string);
        if (m.find()) {
            String day = m.group(1);
            if (day.charAt(0) == '0')
                day = Character.toString(day.charAt(1));
            Log.w("holiday", "dd MMMM yyyy");
            return day + " " + months.get(m.group(2)) + " " + m.group(3);
        }
        else
            return "";
    }
    //Лимит запросов
    private void getWord(String str, final Consumer<String> callback){
        Pattern ptrn = Pattern.compile("([+-]?\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher  = ptrn.matcher(str);
        Pattern pattern = Pattern.compile("(рублей 00 копеек)|(рубль 00 копеек)|(рубля 00 копеек)", Pattern.CASE_INSENSITIVE);
        if (matcher.find()) {
            Log.w("n2w", "I found msg");
            String num = matcher.group(1);
            int i = Integer.parseInt(num);
            i = Math.abs(i);
            final String[] out = {null};
            int finalI = i;
            n2wToString.getWord(Integer.toString(i), new Consumer<String>() {
                @Override
                public void accept(String s) {
                    if (s != null) {
                        out[0] = s;
                        Matcher m = pattern.matcher(out[0]);
                        out[0] = m.replaceAll("");
                        //Log.w("n2w", "I did return word");
                        if (finalI < 0) {
                            //Log.w("n2w", "i called back");
                            callback.accept(matcher.replaceAll("минус" + ' ' + out[0]));
                        }
                        else {
                            //Log.w("n2w", "i called back");
                            callback.accept(matcher.replaceAll(out[0]));
                        }
                        return;
                    }
                }
            });

        }
        else
            callback.accept(str);
    }
}
