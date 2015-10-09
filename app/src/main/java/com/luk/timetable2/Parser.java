package com.luk.timetable2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luk on 5/16/15.
 */
public class Parser {
    private String url;
    private ArrayList<String> hours = new ArrayList<>();
    private HashMap<Integer, ArrayList<HashMap<String, String>>> lessons = new HashMap<>();
    private Pattern regex_group = Pattern.compile("-[a-zA-z0-9/]+$");

    private String QUERY_CLASSES_SELECT = "select[name=oddzialy]";
    private String QUERY_CLASSES_A = "a[target=plan]";

    private String QUERY_TABLE = "table[class=tabela]";
    private String QUERY_LESSON = "td[class=l]";
    private String QUERY_LESSON_MULTIPLE = "span[style=font-size:85%]";
    private String QUERY_SUBJECT = "span[class=p]";
    private String QUERY_HOUR = "td[class=g]";
    private String QUERY_ROOM = ".s";

    /**
     * @param url Vulcan API link
     */
    public Parser(String url) {
        this.url = url;
    }

    /**
     * @return list of classes
     * @throws IOException
     */
    public HashMap<Integer, String> parseClasses() throws IOException {
        HashMap<Integer, String> class_list = new HashMap<>();

        Document data = Jsoup.connect(url).header("Accept", "*/*").get();

        Elements classes_select = data.select(QUERY_CLASSES_SELECT);
        Elements classes_a = data.select(QUERY_CLASSES_A);

        if (classes_select.size() > 0) {
            for (Element c : classes_select.select("option")) {
                if (c.hasAttr("value")) {
                    class_list.put(Integer.parseInt(c.attr("value")), c.html());
                }
            }
        } else {
            for (Element c : classes_a) {
                if (c.attr("href").startsWith("plany/o")) {
                    class_list.put(Integer.parseInt(c.attr("href").substring(7, c.attr("href").length() - 5)), c.html());
                }
            }
        }

        return class_list;
    }

    /**
     * Should return list of lessons for specific class in this scheme:
     * day: {
     *     lesson: [hour, lesson_name, group, room]
     * }
     *
     * @return list of lessons
     * @throws IOException
     */
    public HashMap<Integer, ArrayList<HashMap<String, String>>> parseLessons() throws IOException {
        Document data = Jsoup.connect(url).header("Accept", "*/*").get();
        Elements table = data.select(QUERY_TABLE);
        Elements tr = table.select("tr");

        for (Element hour : table.select(QUERY_HOUR)) {
            String h = hour.html();
            h = h.replace("- ", "-");

            hours.add(h);
        }

        for (int i = 1; i < tr.size(); i++) {
            int day = 1;
            String hour = tr.get(i).select(QUERY_HOUR).html();
            hour = hour.replace("- ", "-");
            hours.add(hour);

            Elements lessons = tr.get(i).select(QUERY_LESSON);

            for (Element lesson : lessons) {
                if (lesson.select(QUERY_LESSON_MULTIPLE).size() > 0 && lesson.select(QUERY_SUBJECT).size() < lesson.select(QUERY_LESSON_MULTIPLE).size()) {
                    for (int l = 0; l < lesson.select(QUERY_LESSON_MULTIPLE).size(); l++) {
                        HashMap _lesson = parseLesson(lesson, hours.get(i - 1), l);
                        ArrayList<HashMap<String, String>> array = this.lessons.get(day) == null ? new ArrayList<>() : (ArrayList) this.lessons.get(day);
                        array.add(_lesson);

                        this.lessons.put(day, array);
                    }
                } else {
                    try {
                        if (lesson.select(QUERY_SUBJECT).size() > 1 && lesson.select(QUERY_ROOM).size() > 0) {
                            String[] _groups = lesson.html().split("<br>");

                            for (String group : _groups) {
                                Element elem = Jsoup.parse(group);
                                HashMap _lesson = parseLesson(elem, hours.get(i - 1), 0);
                                ArrayList<HashMap<String, String>> array = this.lessons.get(day) == null ? new ArrayList<>() : (ArrayList) this.lessons.get(day);
                                array.add(_lesson);

                                this.lessons.put(day, array);
                            }
                        } else {
                            HashMap _lesson = parseLesson(lesson, hours.get(i - 1), 0);
                            ArrayList<HashMap<String, String>> array = this.lessons.get(day) == null ? new ArrayList<>() : (ArrayList) this.lessons.get(day);
                            array.add(_lesson);

                            this.lessons.put(day, array);
                        }
                    } catch (Exception ex) {
                        // Do nothing, no lesson that time
                    }
                }
                day++;
            }
        }

        return lessons;
    }

    /**
     * Should return lesson in this scheme:
     * lesson: [hour, lesson_name, group, room]
     * Used internally.
     *
     * @return list of lessons
     */
    private HashMap<String, String> parseLesson(Element lesson, String hour, Integer num) {
        HashMap<String, String> map = new HashMap<>();

        String _lesson = lesson.select(QUERY_SUBJECT).get(num).html();
        String _room = "";
        String _group = "";

        // make _lesson uppercase
        _lesson = _lesson.substring(0, 1).toUpperCase() + _lesson.substring(1);

        // get room
        try {
            _room = lesson.select(QUERY_ROOM).get(num).html();
        } catch (Exception ex) {
            // do nothing, no room
        }

        // get group
        Matcher match = regex_group.matcher(_lesson);

        if (match.find()) {
            _lesson = _lesson.replace(match.group(0), ""); // remove group from lesson
            _group = match.group(0).substring(1);
        } else {
            for (String line : lesson.html().split("\n")) {
                match = regex_group.matcher(line.trim());

                if (match.find()) {
                    _group = match.group(num).substring(1);
                    break;
                }
            }
        }

        map.put("hour", hour);
        map.put("lesson", _lesson);
        map.put("room", _room);
        map.put("group", _group);

        return map;
    }

}
