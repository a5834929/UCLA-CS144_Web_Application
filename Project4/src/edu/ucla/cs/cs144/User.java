package edu.ucla.cs.cs144;

public class User {
	private String userID;
	private String location;
	private String country;
	private int rating;

	public User(){
		userID = "";
		rating = 0;
		location = "";
		country = "";
	}

	public String getUserID(){
		return userID;
	}
	public void setUserID(String id){
		userID = id;
	}

	public int getRating(){
		return rating;
	}
	public void setRating(int r){
		rating = r;
	}

	public String getLocation(){
		return location;
	}
	public void setLocation(String loc){
		location = loc;
	}

	public String getCountry(){
		return country;
	}
	public void setCountry(String cnty){
		country = cnty;
	}
}
