function hoverOn(x) {
	x.className = 'hover';
}

function hoverOff(x) {
	x.classList.remove('hover');
}

function errorCallBack(error) {
	if (error.code == error.PERMISSION_DENIED) {
		console.log("FAILURE");
		window.location.replace("/error");
	}
}

function wait() {
	if ($("#party-list li").filter(".selected").attr("id") !== undefined) {
		$("#partySubmit").attr("disabled", false);
		$("#partySubmit").val("Join");
	} else {
		setTimeout(wait, 300);
		$("#partySubmit").attr("disabled", true);
		$("#partySubmit").val("Please select a sesh first!");
	}

}
let partyId;
let accessType;
let accessCode;

$(document).ready(() => {

	const postParameters = {userId: userId};
    $.post("/getactiveparty", postParameters, responseJSON => {
        const responseObject = JSON.parse(responseJSON);
        const redirectPage = responseObject.redirectPage;

        if (redirectPage !== null || redirectPage !== undefined) {
        	const params = {userId: responseObject.userId, partyId: responseObject.partyId, partyName: responseObject.partyName};
        	post(redirectPage, params);
        }
    });

	wait();

	const $partyList = $("#party-list ul");


	$("#partySubmit").click(function() {
		let postParameters;
		console.log("START");
		console.log("AT: " + accessType);
		if (accessType === "PRIVATE") {
			console.log("INSIDE PRIVATE");
			accessCode = prompt("Please enter your access token to join this party");
			if (accessCode === null || accessCode === "") {
				//user pressed cancel
				return;
			} else {
				console.log("partyId" + partyId);
				console.log("accessCode" + accessCode);
				postParameters ={userId: userId, partyId: partyId, accessType: accessType, accessCode: accessCode};
			}
		} else {
			console.log("INSIDE PUBLIC");
			postParameters ={userId: userId, partyId: partyId, accessType: accessType, accessCode: ""};
		}
		console.log("postParams: " ,postParameters);
		$.post("/joinParty", postParameters, responseJSON => {
			console.log("INSIDE FIRST POST");
			const responseObject = JSON.parse(responseJSON);
			const success = responseObject.success;
			console.log("Respoonse obj: ", responseObject);
			if(!success) {
				alert("Invalid access code!");
			} else {
				console.log("INSIDE 2ND POST");
				const params = {userId: userId, partyId: partyId};
				post("/join/party", params, "post");
			}
		})
	});
	
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			const lat = position.coords.latitude;
			const lon = position.coords.longitude;
			const postParameters = {latitude: lat, longitude: lon};
			$.post("/join2", postParameters, responseJSON => {
				const responseObject = JSON.parse(responseJSON);
				parties = responseObject.parties;
				console.log("party length: " + parties.length);
				$("#loadingParties").hide();
				for (var i = 0; i < parties.length; i++) {
					$partyList.append("<li id=\"" + parties[i].partyId + "\" onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\">" + parties[i].name+ "</li>");
					$("#" + parties[i].partyId).data('accessType', parties[i].accessType);
				}
				
				$partyList.on("click", event => {
					$listItems = $("li");
					$selected = $listItems.filter('.hover');
					partyId = ($selected.attr('id'));
					console.log("id: " + partyId);
					accessType = ($selected.data('accessType'));
					console.log("SELECTED: ", $selected);
					$selected.addClass("selected");
					//$selected.css("background-color", "grey");
					$("#partySubmit").attr("disabled", false);
					//console.log("SELECTED: " , $("#party-list li").filter(".selected").attr("id"));
				});

				console.log("parties should be received if you are here!");
			});
		}, errorCallBack);
	}

});

