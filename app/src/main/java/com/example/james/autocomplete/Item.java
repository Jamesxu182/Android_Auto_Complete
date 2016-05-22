package com.example.james.autocomplete;

/**
 * Created by James on 15/11/21.
 */
public class Item {
    public String content = null;
    public int id = 0;
    public boolean star = false;

    //Source attribute means where the item comes from
    //1 from database
    //2 from google
    public int source = 0;

    public Item() {;};

    public Item(String content, int source) {
        this.content = content;
        this.source = source;
    }

    public Item(String content, int source, boolean star) {
        this.content = content;
        this.source = source;
        this.star = star;
    }

    public void setId(int id) {
        this.id =id;
    }

    public void setStar(boolean star) {
        this.star = star;
    }

    public String getContent() {
        return this.content;
    }

    public int getNum() {
        return this.id;
    }

    public int getSource() {
        return this.source;
    }

    public boolean getStar() { return this.star; }
}

