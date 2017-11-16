================
	[PART B]
================

Relation: (keys are specified with *)

	Item(ItemID*, Name, Currently, Buy_Price, First_Bid, Number_of_Bids, Location, Latitude, Longitude, Country, Started, Ends, SellerID, Description)

	Catgeory(ItemID, CateName)

	Bidder(ItemID, BidderID, Bid_Time, Amount)

	User(UserID*, Location, Country, Bid_Rating, Sell_Rating)

----------------

Completely nontrivial functional dependency:

	The Item and User relations are completely nontrivial functional dependencies.
	Within the Item table, there are some other dependencies such as 

		(Longitude, Latitude) -> Location
		(Longitude, Latitude) -> Country
		Location -> Country

----------------

BCNF: 
	Yes.

----------------

Fourth Normal Form: 
	The Item table is not in 4NF because splitting all attriutes into different tables would be redundant.

----------------


