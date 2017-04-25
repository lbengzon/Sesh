function hoverOn(x) {
	x.className = 'selected';
}

function hoverOff(x) {
	x.classList.remove('selected');
}


$(document).ready(() => {

	$("#userId").val(userId);
	const $partyList = $("#party-list ul");


	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			const lat = position.coords.latitude;
			const lon = position.coords.longitude
			const postParameters = {latitude: lat, longitude: lon};
			$.post("/join2", postParameters, responseJSON => {
				const responseObject = JSON.parse(responseJSON);
				parties = responseObject.parties;
				const partyIds = responseObject.partyIds;
				console.log("party length: " + parties.length);
				for (var i = 0; i < parties.length; i++) {
					$partyList.append("<li id=\"" + partyIds[i] + "\" onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\">" + partyIds[i] + ": " + parties[i] + "</li>");
				}
				
				$partyList.on("click", event => {
					$listItems = $("li");
					$selected = $listItems.filter('.selected');
					$("#partyId").val($selected.attr('id'));
					$selected.css("background-color", "grey");

				});

				console.log("parties should be received if you are here!");
			});
		}, errorCallBack);
	}
});

function errorCallBack(error) {
	if (error.code == error.PERMISSION_DENIED) {
		console.log("FAILURE");
		window.location.replace("/error");
	}
}