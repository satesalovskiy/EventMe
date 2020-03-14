package com.tsa.EventMe;

import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

public class Person {

    private String name;
    private String email;
    private List<Person> friends;
    private List<Event> iGoEvents;

    Person(String email) {
        this.name = "Name";
        this.email = email;
        this.friends = new LinkedList<>();
        this.iGoEvents = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Person> getFriends() {
        return friends;
    }

    public void setFriends(List<Person> friends) {
        this.friends = friends;
    }

    public List<Event> getiGoEvents() {
        return iGoEvents;
    }

    public void setiGoEvents(List<Event> iGoEvents) {
        this.iGoEvents = iGoEvents;
    }

}
