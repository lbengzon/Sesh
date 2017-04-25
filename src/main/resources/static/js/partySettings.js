var pos;
var global_lat;
var global_lon;

$(document).ready(() => {
	navigator.geolocation.getCurrentPosition(c, errorCallBack);
	$("#userId").val(userId);




});

var c = function(pos) {
		global_lat = pos.coords.latitude, 
		global_lon = pos.coords.longitude;
		$("#lat").val(global_lat);
		$("#lon").val(global_lon);

	}

function errorCallBack(error) {
	if (error.code == error.PERMISSION_DENIED) {
		console.log("FAILURE");
		window.location.replace("/error");
	}
}
