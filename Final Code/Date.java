//Represents Date objects manipulated by other classes

import java.time.MonthDay;
import java.io.Serializable;

public class Date implements Serializable {
    private String name; // name of the day
    private MonthDay day; // stores the day and month
    private String type; // type of the date
    private boolean stickied; // indicates if the date is stickied on top

    // Constructor
    public Date(String name, int month, int day, String type, boolean stickied) {
        this.name = name;
        this.day = MonthDay.of(month, day);
        this.type = type;
        this.stickied = stickied;
    }

    // Getters and setters for date attributes
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MonthDay getDay() {
        return day;
    }

    public void setDay(int month, int day) {
        this.day = MonthDay.of(month, day);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isStickied() {
        return stickied;
    }

    public void setStickied(boolean stickied) {
        this.stickied = stickied;
    }
}
