package com.expenx.expenx.core;

/**
 * Created by samintha on 5/8/2017.
 */

public class NoteDataModel {

    private String title;
    private String description;
    private String imageUrl;
    private String amount;

    public NoteDataModel(String title, String description, String imageUrl, String amount) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAmount() {
        return amount;
    }
}
