package quanye.org.chatapp.domain;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 4455065920612636652L;

    private String userName;
    private String content;

    public Message() {
    }

    public Message(String userName, String content) {
        this.userName = userName;
        this.content = content;
    }

    public void setUesrName(String userName) {
        this.userName = userName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getContent() {
        return this.content;
    }
}