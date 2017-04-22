$(document).ready(() => {

	var c = function(pos) {
		var lat = pos.coords.latitude, 
		lon = pos.coords.longitude;
		console.log("lat: " + lat);
		console.log("lon: " + lon);

		const postParameters = {lat: lat, lon: lon};
		$.post("/join", postParameters, responseJSON => {
			//display list of parties in range here
		})
	}

	navigator.geolocation.getCurrentPosition(c);

});