$(document).ready(() => {

	var c = function(pos) {
		var lat = pos.coords.latitude, 
		lon = pos.coords.longitude;
		console.log("lat: " + lat);
		console.log("lon: " + lon);

		const postParameters = {lat: lat, lon: lon};
		$.post("/create", postParameters, responseJSON => {
			console.log("lat: " + lat);
			console.log("lon: " + lon);

			//do something
		})
	}

	navigator.geolocation.getCurrentPosition(c);

})