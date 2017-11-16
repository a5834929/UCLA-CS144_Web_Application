package edu.ucla.cs.cs144;

public class Bid {
	private User bidder;
	private String time;
	private String amount;

	public User getBidder(){
		return bidder;
	}
	
	public void setBidder(User b){
		bidder = b;
	}

	public String getTime(){
		return time;
	}
	public void setTime(String t){
		time = t;
	}

	public String getAmount(){
		return amount;
	}
	public void setAmount(String a){
		amount = a;
	}
}
