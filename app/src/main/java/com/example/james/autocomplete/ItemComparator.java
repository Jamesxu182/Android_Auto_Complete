package com.example.james.autocomplete;

import java.util.Comparator;

/**
 * Created by James on 15/11/26.
 */

//To Sort the Array of Item according the key_word. It will give the result the user really want at the front of list view.
//Collects.sort() needs this class as parameter.
public class ItemComparator implements Comparator<Item> {
    private final String key_word;

    public ItemComparator(String search) {
        this.key_word = search.toLowerCase();
    }

    @Override
    public int compare(Item item1, Item item2) {
        if (item1.getContent().toLowerCase().startsWith(this.key_word) && !item2.getContent().toLowerCase().startsWith(this.key_word)) {
            return 1;
        } else if (!item1.getContent().toLowerCase().startsWith(key_word) && item2.getContent().toLowerCase().startsWith(this.key_word)) {
            return -1;
        } else if (item1.getContent().toLowerCase().startsWith(key_word) && item2.getContent().toLowerCase().startsWith(this.key_word)) {
            if(item1.getSource() == 1 && item2.getSource() != 1) {
                return -1;
            } else if(item1.getSource() != 1 && item2.getSource() == 1) {
                return 1;
            } else {
                if (item1.getContent().toLowerCase().length() < item2.getContent().toLowerCase().length()) {
                    return -1;
                } else if (item1.getContent().toLowerCase().length() > item2.getContent().toLowerCase().length()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        } else {
            return 0;
        }
    }
}
