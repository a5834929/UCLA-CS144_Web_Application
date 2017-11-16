# ================================================
# 		B.2: Build/Drop Spatial Index in MySQL
# ================================================

# Drop index and table
DROP INDEX Sp_index ON ItemSpatial;
DROP TABLE IF EXISTS ItemSpatial;