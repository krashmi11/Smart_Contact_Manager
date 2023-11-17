package com.smartcontact.smartcontact.Helper;

public class helper {
    private String Content;
    private String type;

    public helper(String content, String type) {
        super();
        Content = content;
        this.type = type;
    }

    public String getContent() {
        return Content;
    }

    public String getType() {
        return type;
    }

    public void setContent(String content) {
        Content = content;
    }

    public void setType(String type) {
        this.type = type;
    }

}
