let pos;
let global_lat = null;
let global_lon = null;
let global_device_id = null;

$(document).ready(() => {
	$("#userId").val(userId);

	const postParameters = {userId: userId};
    $.post("/getactiveparty", postParameters, responseJSON => {
        const responseObject = JSON.parse(responseJSON);
        const redirectPage = responseObject.redirectPage;

        if (redirectPage !== null || redirectPage !== undefined) {
        	const params = {userId: responseObject.userId, partyId: responseObject.partyId, partyName: responseObject.partyName};
        	post(redirectPage, params);
        }
    });


	const $deviceList = $("#deviceList");
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(getLoc, errorCallBack);
	}

	wait();

	

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
		accessType = $("input[name=privacy_setting]:checked").val();
		accessCode = "";
		if(accessType === "PRIVATE"){
			console.log($("#accessCode").val());
			if ($("#accessCode").val().length === 0) {
				alert("Please supply an access code for a private party!");
				return;
			} else {
				accessCode = $("#accessCode").val();
			}
		}
		if (global_device_id === null) {
			alert("You must select a device to play from first!");
			return;
		} 

		if ($("#sesh_name").val() !== "" && $("#host_name").val() !== "") {
			const postParams = {userId: userId, sesh_name: $("#sesh_name").val(), host_name: $("#host_name").val(), accessType: accessType, accessCode: accessCode, lat: $("#lat").val(), lon: $("#lon").val(), deviceId: global_device_id};
			$.post("/getParty", postParams, responseJSON => {
				const responseObject = JSON.parse(responseJSON);
				const postParams = {userId: responseObject.userId, partyId: responseObject.partyId, partyName: responseObject.partyName};
				post("/create/party", postParams);
			});
		} else {
			alert("Please fill out the sesh name and host name fields!");
		}

	});

	$("#private_sesh").click(function() {
		document.getElementById("passwordDiv").style.display = "block";
	});

	$("#public_sesh").click(function() {
		document.getElementById("passwordDiv").style.display = "none";
	});

});

function hoverOn(x) {
	x.className = 'hover';
}

function hoverOff(x) {
	x.classList.remove('hover');
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
	} else {
		setTimeout(wait, 300);
		console.log("waiting...");
		document.getElementById("formSubmit").disabled = true;
		document.getElementById("formSubmit").value = "Loading...";

	}
}
