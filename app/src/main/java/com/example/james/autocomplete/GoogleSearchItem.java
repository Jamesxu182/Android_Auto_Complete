package com.example.james.autocomplete;

/**
 * Created by James on 15/11/24.
 */
public class GoogleSearchItem extends Item {
    public GoogleSearchItem(String content, int source) {
        super(content, source);
    }

    public GoogleSearchItem(String content, int source, boolean star) {
        super(content, source, star);
    }
}
