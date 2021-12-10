package com.example.civiladvocacy;

import java.io.Serializable;

public class Official implements Serializable {

    private final String city;
    private final String state;
    private final String zip;

    private final String office;
    private final String name;
    private final String party;
    private final String phone;
    private final String url;
    private final String email;
    private final String photo;
    private final String facebook;
    private final String twitter;
    private final String youtube;
    private final String address;


    public Official(String city, String state, String zip, String office, String name, String party, String address, String phone, String url, String email, String photo, String facebookUrl, String twitterUrl, String youtubeUrl) {
        this.city = city;
        this.state = state;
        this.zip = zip;

        this.office = office;
        this.name = name;
        this.party = party;
        this.address = address;

        this.phone = phone;
        this.url = url;
        this.email = email;
        this.photo = photo;

        this.facebook = facebookUrl;
        this.twitter = twitterUrl;
        this.youtube = youtubeUrl;
    }

    String getCity() { return city; }
    String getState() { return state; }
    String getZip() { return zip; }

    String getOfficeName() { return office; }
    String getOfficialName() { return name; }
    String getPartyName() { return party; }
    String getPhone() { return phone; }
    String getUrl() { return url; }
    String getEmail() { return email; }
    String getPhoto() { return photo; }
    String getFacebook() { return facebook; }
    String getTwitter() { return twitter; }
    String getYoutube() { return youtube; }
    String getAddress() { return address; }
}
