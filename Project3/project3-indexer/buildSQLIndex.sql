# ================================================
# 		B.2: Build Spatial Index in MySQL
# ================================================

# create table
CREATE TABLE ItemSpatial (ItemID INTEGER, Location POINT NOT NULL) ENGINE=MyISAM;


# populate the table with itemId, latitude, and longitude information
INSERT INTO ItemSpatial(ItemID, Location)
	SELECT ItemID, POINT(Latitude, Longitude) FROM Item;


# create a spatial index on latitude and longitude
CREATE SPATIAL INDEX Sp_index ON ItemSpatial (Location);
