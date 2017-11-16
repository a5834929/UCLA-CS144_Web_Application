package edu.ucla.cs.cs144;

import java.util.List;

public class Item {
	private int itemID;
	private String name;
	private String currently;
	private String buyPrice;
	private String firstBid;
	private int numberBid;
	private String started;
	private String ends;
	private String description;
	private String location;
	private String country;
	private String latitude;
	private String longitude;
	private List<String> category;
	private User seller;
	private List<Bid> bids;
	
	public int getItemID(){
		return itemID;
	}
	
	public void setItemID(int id){
		itemID = id;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String n){
		name = n;
	}
	
    public String getCurrently() {
        return currently;
    }

    public void setCurrently(String c) {
        currently = c;
    }

    public String getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(String bp) {
        buyPrice = bp;
    }

    public String getFirstBid() {
        return firstBid;
    }

    public void setFirstBid(String fb) {
        firstBid = fb;
    }

    public int getNumberOfBids() {
        return numberBid;
    }

    public void setNumberOfBids(int nb) {
    	numberBid = nb;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String s) {
    	started = s;
    }

    public String getEnds() {
        return ends;
    }

    public void setEnds(String e) {
        ends = e;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        description = desc;
    }

    public String getLocation() {
    	return location;
    }

    public void setLocation(String loc) {
    	location = loc;
    }

    public String getCountry() {
    	return country;
    }

    public void setCountry(String cnty) {
    	country = cnty;
    }

    public String getLatitude() {
    	return latitude;
    }

    public void setLatitude(String lat) {
    	latitude = lat;
    }

    public String getLongitude() {
    	return longitude;
    }

    public void setLongitude(String lon) {
    	longitude = lon;
    }

    public List<String> getCategoryList() {
        return category;
    }

    public void setCategory(List<String> categoryList) {
        category = categoryList;
    }

	public User getSeller() {
        return seller;
    }

    public void setSeller(User s) {
        seller = s;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bidList) {
        bids = bidList;
    }
	
}
