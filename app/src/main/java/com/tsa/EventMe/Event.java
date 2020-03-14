package com.tsa.EventMe;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class Event {
    private String topic;
    private String description;
    private String image;
    private long creationTime;
    private int year;
    private int month;
    private int day;
    private String location;
    private String userEmail;



    //private LinkedList<String> subscribers;

    Event() {

    }

    public Event(String ui, String email, String topic, String description, String image, String location, Calendar calendar) {
        this.userEmail = email;
        this.topic = topic;
        this.description = description;
        this.image = image;
        this.location = location;
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.creationTime = new Date().getTime();
//        this.subscribers = new LinkedList<>();
//        this.subscribers.add(ui);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

//    public LinkedList<String> getSubscribers() {
//        return subscribers;
//    }
//
//    public void setSubscribers(LinkedList<String> subscribers) {
//        this.subscribers = subscribers;
//    }
}
