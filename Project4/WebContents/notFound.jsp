<%@ page import="edu.ucla.cs.cs144.*" %>
<!DOCTYPE html>
<html>
	<head>
		<title>eBay Search</title>
		<style type="text/css">
			body{
				margin: 0 10px 0 10px;
			}
			.container{ display:inline-block; }
		</style>
		<link rel="stylesheet" href="suggest.css">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css">
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="emptyCheck.js"></script>
		<script type="text/javascript" src="AutoSuggest.js"></script>
		<script type="text/javascript" src="SuggestionProvider.js"></script>
		<script type="text/javascript">
			window.onload = function () {
				var oTextbox = new AutoSuggestControl(document.getElementById("query"), new SuggestionProvider()); 
			}
		</script>
	</head>
	<body>
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
		<h4 style="margin-left:10px;">Item Not Found! Please search again.</h4>
	</body>
</html>
      
