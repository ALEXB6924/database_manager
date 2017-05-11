package com.nokia.model;

import javax.persistence.*;

/**
 * Created by alexandru_bobernac on 5/11/17.
 */
@SuppressWarnings("ALL")
@Entity
@Table(name = "DatabaseURLs")
public class DatabaseURL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @Column
    private String hostname;

    public DatabaseURL(){
    }

    public DatabaseURL(String name, String hostname) {
        this.name = name;
        this.hostname = hostname;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String toString() {
        return "DatabaseConnections{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + hostname + '\'' +
                '}';
    }
}
