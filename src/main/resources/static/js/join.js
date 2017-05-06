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


$(document).ready(() => {
	wait();

	const $partyList = $("#party-list ul");

	$("#userId").val(userId);
	
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			const lat = position.coords.latitude;
			const lon = position.coords.longitude;
			const postParameters = {latitude: lat, longitude: lon};
			$.post("/join2", postParameters, responseJSON => {
				const responseObject = JSON.parse(responseJSON);
				parties = responseObject.parties;
				const partyIds = responseObject.partyIds;
				console.log("party length: " + parties.length);
				$("#loadingParties").hide();
				for (var i = 0; i < parties.length; i++) {
					$partyList.append("<li id=\"" + partyIds[i] + "\" onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\">" + partyIds[i] + ": " + parties[i] + "</li>");
				}
				
				$partyList.on("click", event => {
					$listItems = $("li");
					$selected = $listItems.filter('.hover');
					$("#partyId").val($selected.attr('id'));
					$selected.addClass("selected");
					//$selected.css("background-color", "grey");
					$("#partySubmit").attr("disabled", false);
					//console.log("SELECTED: " , $("#party-list li").filter(".selected").attr("id"));
				});
				//MAKE SURE TO DELETE
				//==================


				lat = 41.8240;
				lon = 71.4128;
				console.log("parties should be received if you are here!");
			});
		}, errorCallBack);
	}
});

