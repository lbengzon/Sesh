var pos;
var global_lat = null;
var global_lon = null;

$(document).ready(() => {
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(getLoc, errorCallBack);
	}
	wait();
	$("#userId").val(userId);
});

function getLoc(position) {
	console.log("here");
	global_lat = position.coords.latitude, 
	global_lon = position.coords.longitude;
	$("#lat").val(global_lat);
	$("#lon").val(global_lon);
	}

function errorCallBack(error) {
	console.log(error);
	if (error.code == error.PERMISSION_DENIED) {
		window.location.replace("/error");
	}
}

function wait() {
	if (global_lat != null || global_lon != null) {
		console.log("lat: " + global_lat + " lon: " + global_lon);
		console.log("values set");
		document.getElementById("formSubmit").disabled = false;
		document.getElementById("formSubmit").value = "Submit";

	} else {
		console.log("lat: " + global_lat + " lon: " + global_lon);
		setTimeout(wait, 300);
		console.log("waiting...");
		document.getElementById("formSubmit").disabled = true;
		document.getElementById("formSubmit").value = "Loading...";

	}
}
