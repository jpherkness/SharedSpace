package edu.oakland.sharedspace;

import com.firebase.client.Firebase;

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

    private final String DATE_STRING_FORMAT = "EEEE, MMM dd, yyyy HH:mm:ss";

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
    public Event(String title, String description, Date date){
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_STRING_FORMAT);

        this.title = title;
        this.description = description;
        this.date = formatter.format(date);
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
     * Probably a temporary method that provides the ability to easily add events.
     */
    public static void addEvent(String title, String description, Date date){

        // Create a reference to the firebase application
        Firebase ref = new Firebase("https://shared-space.firebaseio.com");

        // Create a event object
        Event event = new Event(title, description, date);

        // Store the event in the database under a unique identifier
        ref.child("events").push().setValue(event);
    }
}