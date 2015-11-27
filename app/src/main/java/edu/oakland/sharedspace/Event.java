package edu.oakland.sharedspace;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * The Event object represents an single event.
 *
 * This class can be used to quickly and easily retrieve and add
 * events inside the database.
 *
 * @author      Joseph Herkness
 * @version     1.0 October 4, 2015
 */
public class Event implements Serializable {
    private final String DATE_STRING_FORMAT = "EEEE, MMM dd, yyyy HH:mm";

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
     * List of tags for the event.
     */
    private List<String> tags;

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
    public Event(String owner, String title, String description, Date date, List<String> tags){
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_STRING_FORMAT);

        this.owner = owner;
        this.title = title;
        this.description = description;
        if(date != null) {
            this.date = formatter.format(date);
        }
        this.tags = tags;
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

    /**
     * Gets a list of tags for the event
     *
     * @return a list of tags
     */
    public List<String> getTags(){
        return tags;
    }
}
