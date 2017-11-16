-- ==== Project 2: PART E ==== --

-- query 1: Find the number of users in the database --
select count(*) from User;

-- query 2: Find the number of items in "New York", (i.e., items whose location is exactly the string "New York"). Pay special attention to case sensitivity. You should match the items in "New York" but not in "new york". --
select count(*) from Item where binary Location="New York";

-- query 3: Find the number of auctions belonging to exactly four categories. --
select count(Tmp.ItemID) 
	from (select ItemID, count(distinct CateName) as CateCnt from Category group by ItemID) as Tmp
	where Tmp.CateCnt=4;

-- query 4: Find the ID(s) of current (unsold) auction(s) with the highest bid. Remember that the data was captured  at the point in time December 20th, 2001, one second after midnight, so you can use this time point to decide which auction(s) are current. Pay special attention to the current auctions without any bid. --
select ItemID from Bidder,
	(select max(B3.Amount) as MaxCurr from Item as I3, Bidder as B3 where STR_TO_DATE(B3.Bid_Time,'%b-%d-%Y %H:%i:%s')<=TIMESTAMP("2001-12-20 00:00:01") and STR_TO_DATE(I3.Ends,'%b-%d-%Y %H:%i:%s')>TIMESTAMP("2001-12-20 00:00:01") and I3.ItemID=B3.ItemID) as MaxBids
	where Amount=MaxBids.MaxCurr;

-- query 5: Find the number of sellers whose rating is higher than 1000. --
select count(User.UserID) from User where User.Sell_Rating>1000;

-- query 6: Find the number of users who are both sellers and bidders. --
select count(UserID) from User where Bid_Rating<>"" and Sell_Rating<>"";

-- query 7: Find the number of categories that include at least one item with a bid of more than $100. --
select count(distinct Category.CateName) from Item, Category where Item.Number_of_Bids<>0 and Item.Currently>100 and Item.ItemID=Category.ItemID;

# ans: 13422, 103, 8365, 1046740686, 3130, 6717, 150


