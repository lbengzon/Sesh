var pos;
var global_lat;
var global_lon;


$(document).ready(() => {
	navigator.geolocation.getCurrentPosition(c);
	console.log("document ready");
});

// function geolocationfunction() {
// 	var sesh_name = $("#sesh_name").val();
// 	var host_name =$("#host_name").val();
// 	var privacy_setting = $('input[name="privacy_setting"]:checked').val();

// 	console.log("sesh_name: " + sesh_name);
// 	console.log("host_name: " + host_name);
// 	console.log("privacy_setting: " + privacy_setting);
// 	console.log("lat: " + global_lat);
// 	console.log("lon: " + global_lon);
// 	const postParameters = {sesh_name: sesh_name, host_name: host_name, privacy_setting: privacy_setting, lat: global_lat, lon: global_lon};
// 		$.post("/create/party", postParameters, responseJSON => {
// 			console.log("im here");
			
// 		});
// 		document.location.href = "localhost:4567/create/party";
// }

var c = function(pos) {
		global_lat = pos.coords.latitude, 
		global_lon = pos.coords.longitude;
		$("#lat").val(global_lat);
		$("#lon").val(global_lon);

		// const postParameters = {lat: lat, lon: lon};
		// $.post("/create/party", postParameters, responseJSON => {
		// 	console.log("lat: " + lat);
		// 	console.log("lon: " + lon);

		// 	//do something
		// })
	}
