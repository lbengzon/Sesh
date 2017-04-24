$(document).ready(() => {

	$("#userId").val(userId);
	
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			const lat = position.coords.latitude;
			const lon = position.coords.longitude
			console.log(lat);
			console.log(lon);
			const postParameters = {latitude: lat, longitude: lon};
			$.post("/join", postParameters, responseJSON => {
				console.log("parties should be received here");
			});
		});
	}
});