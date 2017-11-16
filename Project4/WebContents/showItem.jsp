<%@ page import="edu.ucla.cs.cs144.*" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
	<head>
		<title>eBay Search</title>
		<style>
			html { height: 100% } 
			body{ height: 80%; }
			#map_canvas {
				display:inline-block;
				float:right;
				margin: 0px 5px 5px 0px;
				width:48%; 
				height:80%;
			}
			#table_div { 
				display:inline-block;
				margin-left:10px;
				width:48%;
			}
			.container{ display:inline-block; }
		</style>
		<link rel="stylesheet" href="suggest.css">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
		<script type="text/javascript" src="showItemOnload.js"></script>
		<script type="text/javascript" src="emptyCheck.js"></script>
		<script type="text/javascript" src="AutoSuggest.js"></script>
		<script type="text/javascript" src="SuggestionProvider.js"></script>

	</head>

	<% 
		Item item = (Item) request.getAttribute("item");
		List<String> categoryList = item.getCategoryList();
		List<Bid> bidList = item.getBids();
	%>

	<!-- BODY -->
 	<% 	if(!item.getLongitude().equals("")){ 								%>
			<body onload="initialize()">
	<%	} else{																%>
			<body onload="suggest()">	
	<%	}																	%>
 
		<div class="container" style="width:40%;height:20%;">
			<h3>Search Item by Keyword</h3>
			<form action="/eBay/search" method="GET" class="form-inline">
			<div class="form-group col-xs-2">
				<input type="text" id="query" name="query" autocomplete="off" placeholder="keyword" class="form-control">
				<input type="hidden" value="0" name="numToSkip" placeholder="number of items to skip">
				<input type="hidden" value="20" name="numToReturn" placeholder="number of items to return">
				<input type="submit" value="Search" class="btn btn-default" onclick="return formSubmitQuery()">
			</div>
			</form>
		</div>
			
		<div class="container" style="width:40%;height:20%;">
			<h3>Search Item by ID</h3>
			<form action="/eBay/item" method="GET" class="form-inline">
				<div class="form-group col-xs-2">
					<input type="text" id="itemID" name="itemID" autocomplete="off" placeholder="item ID" class="form-control col-md-2">
					<input type="submit" value="Search" class="btn btn-default" onclick="return formSubmitItem()">
				</div>
			</form>
		</div>

		<br><br><br>
			<div id="table_div">
				<table class="table table-hover">
					<tr>
						<th>ItemID</th>
						<td><%= item.getItemID() %></td>
					</tr>
					<tr>
						<th>Name</th>
						<td><%= item.getName() %></td>
					</tr>
					<tr>
						<th rowspan="<%= categoryList.size() %>">Category</th>
						<td><%= categoryList.get(0) %></td>
					</tr>
					<% for(int i=1;i<categoryList.size();i++){ %>
					<tr>
						<td><%= categoryList.get(i) %></td>
					</tr>
					<% } %>
					<tr>
						<th>Description</th>
						<td><%= item.getDescription() %></td>
					</tr>
					<tr>
						<th>Country</th>
						<td><%= item.getCountry() %></td>
					</tr>
					<tr>
						<th>Location<br>(Latitude, Longitude)</th>
						<td><%= item.getLocation() %>
						<% if(!item.getLongitude().equals("")){ %>
						 (<%= item.getLatitude() %>, <%= item.getLongitude() %>)
						 <% } %>
						</td>
					</tr>
					<tr>
						<th>Seller (Rating)</th>
						<td><%= item.getSeller().getUserID() %> (<%= item.getSeller().getRating() %>)</td>
					</tr>
					<tr>
						<th>Current Price</th>
						<td><%= item.getCurrently() %></td>
					</tr>
					<tr>
						<th>Buy Price</th>
						<td><%= item.getBuyPrice() %></td>
					</tr>
					<tr>
						<th>First Bid</th>
						<td><%= item.getFirstBid() %></td>
					</tr>
					<tr>
						<th>Number of Bids</th>
						<td><%= item.getNumberOfBids() %></td>
					</tr>
					<tr>
						<th>Start Time</th>
						<td><%= item.getStarted() %></td>
					</tr>
					<tr>
						<th>End Time</th>
						<td><%= item.getEnds() %></td>
					</tr>
					<% if(item.getNumberOfBids()!=0){ %>
					<tr>
						<th rowspan="<%= bidList.size() %>">Bids</th>
						<td>Bidder: <%= bidList.get(0).getBidder().getUserID() %><br>
						Time: <%= bidList.get(0).getTime() %><br>
						Amount: <%= bidList.get(0).getAmount() %><br>
						</td>
					</tr>
					<% for(int i=1;i<bidList.size();i++){ %>
					<tr>
						<td>Bidder: <%= bidList.get(i).getBidder().getUserID() %><br>
						Time: <%= bidList.get(i).getTime() %><br>
						Amount: <%= bidList.get(i).getAmount() %><br>
						</td>
					</tr>
					<% }} %>
				</table>
			</div>

		<% 	if(!item.getLongitude().equals("")){ 									%>
				<p id="longitude" style="display:none;"> <%= item.getLongitude() 	%> </p>
				<p id="latitude" style="display:none;"> <%= item.getLatitude() 		%> </p>
				<div id="map_canvas"></div>
		<% } 																		%>
		
	</body>
</html>