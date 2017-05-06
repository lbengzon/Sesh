let pos;
let global_lat = 41.8240;
let global_lon = 71.4128;
let global_device_id = null;

$(document).ready(() => {
	const $deviceList = $("#deviceList");
	// if (navigator.geolocation) {
	// 	navigator.geolocation.getCurrentPosition(getLoc, errorCallBack);
	// }

	wait();

	$("#userId").val(userId);

	const postParams = {userId, userId};
	$.post("/devices", postParams, responseJSON => {
		const responseObject = JSON.parse(responseJSON);
		const devices = responseObject.devices;
		for (let key in devices) {
			const deviceId = devices[key].id;
			const deviceType = devices[key].type;
			console.log(devices[key].name);
			$("#loadingDevices").hide();
			$deviceList.append("<li id=\"" + deviceId + "\" style=\"cursor:pointer\">" + devices[key].name + "</li>");
		}
	});

	$("#deviceList").on("click", "li", function() {
		$("#deviceList li").removeClass("selected");
		$selected = $(this);
		$selected.addClass("selected");
		global_device_id = $selected.attr("id");
		console.log("device id set: " + global_device_id);
		$("#deviceId").val($selected.attr("id"));
	});

	$("#refresh").click(function() {
		console.log("here");
		$deviceList.empty();
		const postParams = {userId, userId};
		$.post("/devices", postParams, responseJSON => {
			const responseObject = JSON.parse(responseJSON);
			const devices = responseObject.devices;
			for (let key in devices) {
				const deviceId = devices[key].id;
				const deviceType = devices[key].type;
				console.log(devices[key].name);
				$("#loadingDevices").hide();
				$deviceList.append("<li id=\"" + deviceId + "\" onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\">" + devices[key].name + "</li>");
			}
		});
	});

	$("#formSubmit").click(function() {
		if (global_device_id === null) {
			alert("You must select a device to play from first!");
			return;
		} 

		if ($("#sesh_name").val() !== "" && $("#host_name").val() !== "") {
			const postParams = {userId: userId, sesh_name: $("#sesh_name").val(), host_name: $("#host_name").val(), privacy_setting: $("input[name=privacy_setting]:checked").val(), lat: $("#lat").val(), lon: $("#lon").val(), deviceId: global_device_id};
			$.post("/getParty", postParams, responseJSON => {
				const responseObject = JSON.parse(responseJSON);
				const postParams = {userId: responseObject.userId, partyId: responseObject.partyId, partyName: responseObject.partyName};
				post("/create/party", postParams);
			});
		} else {
			alert("Please fill out the sesh name and host name fields!");
		}

	});

});

function hoverOn(x) {
	x.className = 'hover';
}

function hoverOff(x) {
	x.classList.remove('hover');
}

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
	//MAKE SURE TO DELETE
	//==================================================================


	// global_lat = 41.8240;
	// global_lon = 71.4128;
	// ==================================================================
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
	} else {
		setTimeout(wait, 300);
		console.log("waiting...");
		document.getElementById("formSubmit").disabled = true;
		document.getElementById("formSubmit").value = "Loading...";

	}
}
