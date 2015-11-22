package edu.oakland.sharedspace;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The Event object represents an single event.
 *
 * This class can be used to quickly and easily retrieve and add
 * events inside the database.
 *
 * @author      Joseph Herkness
 * @version     1.0 October 4, 2015
 */
public class Event {
    private static Event event;

    private final String DATE_STRING_FORMAT = "EEEE, MMM dd, yyyy HH:mm:ss";

    /**
     * Owner/Maker of the event
     */
    private String owner;

    /**
     * Title of the event.
     */
    private String title;

    /**
     * Description of the event.
     */
    private String description;

    /**
     * Date of the event.
     */
    private String date;

    /**
     * Counter for number of people interested
     */
    private int interested = 0;

    /**
     * Class constructor.
     */
    public Event(){

    }

    /**
     * Class constructor that defines the title, description, and date of the event.
     *
     * @param title  the title of the event
     * @param description  the description of the event
     * @param date  the date of the event
     * */
    public Event(String owner, String title, String description, Date date){
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_STRING_FORMAT);

        this.owner = owner;
        this.title = title;
        this.description = description;
        if(date != null) {
            this.date = formatter.format(date);
        }
    }

    /**
     * Gets the owner of the event
     *
     * @return the owner of the event
     */
    public String getOwner(){
        return owner;
    }


    /**
     * Gets the title of the event
     *
     * @return the title of the event
     */
    public String getTitle(){
        return title;
    }

    /**
     * Gets the description of the event
     *
     * @return the description of the event
     */
    public String getDescription(){
        return description;
    }

    /**
     * Gets the date of the event
     *
     * @return the date of the event
     */
    public String getDate(){
        return date;
    }

    public void interestCounter(boolean button) {
        if(button)
            interested++;
        else
            interested--;
    }
}
