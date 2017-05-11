package com.nokia.model;
/**
 * Created by alexandru_bobernac on 5/11/17.
 */
import javax.persistence.*;
@SuppressWarnings({"JpaDataSourceORMInspection", "unused"})
@Entity
@Table(name = "Logs")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String databaseUser;

    @Column(name = "databaseName")
    private String database;

    @Column(columnDefinition = "TEXT")
    private String statement;

    @Column(columnDefinition = "DATETIME")
    private String createDate;

    public Log() {}

    public Log(String username, String databaseUser, String database, String statement, String createDate) {
        this.username = username;
        this.databaseUser = databaseUser;
        this.database = database;
        this.statement = statement;
        this.createDate = createDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public void setDatabaseUser(String databaseUser) {
        this.databaseUser = databaseUser;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", databaseUser='" + databaseUser + '\'' +
                ", database='" + database + '\'' +
                ", statement='" + statement + '\'' +
                ", createDate='" + createDate + '\'' +
                '}';
    }
}

