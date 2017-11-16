function initialize() {

	suggest();

	var latitude = document.getElementById("latitude").innerHTML;
	var longitude = document.getElementById("longitude").innerHTML;
	var addr = latitude+","+longitude;
	var latlng = new google.maps.LatLng(latitude, longitude);
	var myOptions = {
		zoom: 14, // default is 8
		center: latlng,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

	var marker = new google.maps.Marker({
	    position: latlng,
	    map: map,
	    title: 'marker'
	});
}
function suggest() {
	var oTextbox = new AutoSuggestControl(document.getElementById("query"), new SuggestionProvider());
}