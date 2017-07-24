package com.devcookie.estimote.gameapp;


public class FoundBeacon {

    int id;
    String region;
    String message;
    String clue;
    String timestamp;

    public FoundBeacon() {

    }

    public FoundBeacon(int id, String region, String message, String clue, String timestamp) {
        this.id = id;
        this.region = region;
        this.message = message;
        this.clue = clue;
        this.timestamp = timestamp;
    }

}
