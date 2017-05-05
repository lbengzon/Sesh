var pos;
var global_lat = null;
var global_lon = null;

$(document).ready(() => {
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(getLoc, errorCallBack);
	}
	wait();
	$("#userId").val(userId);

	const postParams = {userId, userId};
	$.post("/devices", postParams, responseJSON => {
		const responseObject = JSON.parse(responseJSON);
		const devices = responseObject.devices;
		for (let device in devices) {
			console.log(device);
		}
	});
	
});

function post(path, params, method) {
	method = method || "post"; // Set method to post by default if not specified.

    // The rest of this code assumes you are not using a library.
    // It can be made less wordy if you use one.
    let form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", path);

    for(var key in params) {
        if(params.hasOwnProperty(key)) {
            let hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", key);
            hiddenField.setAttribute("value", params[key]);

            form.appendChild(hiddenField);
         }
    }

    document.body.appendChild(form);
    form.submit();
}

function getLoc(position) {
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
		console.log("values set "+ " lat: " + global_lat + " lon: " + global_lon);
		document.getElementById("formSubmit").disabled = false;
		document.getElementById("formSubmit").value = "Submit";
		$("#formSubmit").click(function() {
		console.log("USER ID: " + userId);
		console.log("SESH NAME: " + $("#sesh_name").val());
		console.log("HOST NAME: " + $("#host_name").val());
		console.log("PRIVACY SETTING: " + $("input[name=privacy_setting]:checked").val());
		console.log("LAT: " + $("#lat").val());
		console.log("LON: " + $("#lon").val());

		const postParameters = {userId: userId, sesh_name: $("#sesh_name").val(), host_name: $("#host_name").val(), privacy_setting: $("input[name=privacy_settings]:checked").val(), lat: $("#lat").val(), lon: $("#lon").val()};
		$.post("/getParty", postParameters, responseJSON => {
			const responseObject = JSON.parse(responseJSON);
			const partyId = responseObject.partyId;
			const partyName = responseObject.partyName;
			const uId = responseObject.userId;
			console.log("party id: " + partyId);
			console.log("party name: " + partyName);
			console.log("uId: " + uId);
			const postParams = {userId: uId, partyId: partyId, partyName: partyName};
			post("/create/party", postParams);

		});
	});

	} else {
		setTimeout(wait, 300);
		console.log("waiting...");
		document.getElementById("formSubmit").disabled = true;
		document.getElementById("formSubmit").value = "Loading...";

	}
}
