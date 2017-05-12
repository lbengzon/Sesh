let pos;
let global_lat = null;
let global_lon = null;

$(document).ready(() => {
	let global_device_id = null;
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
		$("#deviceList li").removeClass("deviceSelected");
		$selected = $(this);
		$selected.addClass("deviceSelected");
		global_device_id = $selected.attr("id");
		console.log("device id set: " + global_device_id);
		$("#deviceId").val($selected.attr("id"));
	});

	$("#refresh").click(function() {
		console.log("here");
		$("#deviceList li").remove();
		const postParams = {userId, userId};
		$.post("/devices", postParams, responseJSON => {
			const responseObject = JSON.parse(responseJSON);
			const devices = responseObject.devices;
			for (let key in devices) {
				const deviceId = devices[key].id;
				const deviceType = devices[key].type;
				console.log(devices[key].name);
				$("#loadingDevices").hide();
				$deviceList.append("<li id=\"" + deviceId + "\">" + devices[key].name + "</li>");
			}
		});
	});

	$("#formSubmit").click(function() {
			$("#seshNameError").hide();
			$("#hostNameError").hide();
			$("#deviceError").hide();
			$("#accessCodeError").hide();
		accessType = $("input[name=privacy_setting]:checked").val();
		accessCode = "";
		if(accessType === "PRIVATE"){
			if ($("#accessCode").val().length === 0) {
				$("#accessCodeError").show();
				return;
			} else {
				accessCode = $("#accessCode").val();
			}
		}
		if (global_device_id === null) {
			$("#deviceError").show();
			return;
		}

		if ($("#sesh_name").val() !== "" && $("#host_name").val() !== "") {
			const postParams = {userId: userId, sesh_name: $("#sesh_name").val(), host_name: $("#host_name").val(), accessType: accessType, accessCode: accessCode, lat: $("#lat").val(), lon: $("#lon").val(), deviceId: global_device_id};
			$.post("/getParty", postParams, responseJSON => {
				const responseObject = JSON.parse(responseJSON);
				console.log("responseObject: " , responseObject);
				if (responseObject.message === null || responseObject.message === undefined) {
					console.log("HERE");
					const postParams = {userId: responseObject.userId, partyId: responseObject.partyId, partyName: responseObject.partyName};
					post("/create/party", postParams);
				} else {
					console.log("message: " + responseObject.message);
					const params = {message: responseObject.message};
					post("/", params, "get");
				}
				
			});
		} else if ($("#sesh_name").val() === "") {
			$("#seshNameError").show();
			//alert("Please fill out the sesh name and host name fields!");
		} else if ($("#host_name").val() === "") {
			$("#hostNameError").show();
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
