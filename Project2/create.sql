-- ==== Project 2: PART D ==== --

-- create table --
create table Item (ItemID integer primary key, 
					Name varchar(100),
					Currently decimal(12,2),
					Buy_Price decimal(12,2), 
					First_Bid decimal(12,2), 
					Number_of_Bids integer, 
					Location varchar(50), 
					Country varchar(50), 
					Latitude decimal(9,6), 
					Longitude decimal(9,6), 
					Started varchar(30), 
					Ends varchar(30), 
					SellerID integer, 
					Description varchar(4001));

create table Bidder (ItemID integer, BidderID varchar(100), Bid_Time varchar(30), Amount decimal(12,2));
create table User (UserID varchar(100) primary key, Location varchar(50), Country varchar(50), Bid_Rating varchar(20) default NULL, Sell_Rating varchar(20) default NULL);
create table Category (ItemID integer, CateName varchar(100));