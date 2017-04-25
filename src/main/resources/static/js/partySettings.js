var pos;
var global_lat = null;
var global_lon = null;

$(document).ready(() => {
	navigator.geolocation.getCurrentPosition(c, errorCallBack);
	wait();
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
		window.location.replace("/error");
	}
}

function wait() {
	if (global_lat != null || global_lon != null) {
		console.log("lat: " + global_lat + " lon: " + global_lon);
		document.getElementById("formSubmit").disabled = false;
		document.getElementById("formSubmit").value = "Submit";

	} else {
		console.log("lat: " + global_lat + " lon: " + global_lon);
		setTimeout(wait, 300);
		document.getElementById("formSubmit").disabled = true;
		document.getElementById("formSubmit").value = "Loading...";

	}
}
