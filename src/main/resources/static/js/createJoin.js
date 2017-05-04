// var global_lat = null;
// var global_lon = null;

// function getLoc(position) {
// 	const $partyList = $("#party-list");
// 	global_lat = position.coords.latitude, 
// 	global_lon = position.coords.longitude;

// 	const postParameters = {latitude: global_lat, longitude: global_lon};
// 	$.post("/join2", postParameters, responseJSON => {
// 		const responseObject = JSON.parse(responseJSON);
// 		parties = responseObject.parties;
// 		const partyIds = responseObject.partyIds;
// 		console.log("party length: " + parties.length);
// 		for (var i = 0; i < parties.length; i++) {
// 			$partyList.append("<li id=\"" + partyIds[i] + "\" onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\">" + partyIds[i] + ": " + parties[i] + "</li>");
// 		}
		
// 		$partyList.on("click", event => {
// 			$listItems = $("li");
// 			$selected = $listItems.filter('.selected');
// 			$("#partyId").val($selected.attr('id'));
// 			$selected.css("background-color", "grey");

// 		});

// 		console.log("parties should be received if you are here!");
// 	});

// }

// function wait() {
// 	const $joinButton = $("#joinSesh");
// 	if (global_lat !== null && global_lon !== null) {
// 		console.log("lat set: " + global_lat);
// 		console.log("lon set: " + global_lon);
// 		$joinButton.val("Join a sesh");
// 		$joinButton.attr("disabled", false);
// 	} else {
// 		setTimeout(wait, 300);
// 		console.log("waiting...");
// 		$joinButton.attr("disabled", true);
// 		$joinButton.val("Loading...");
// 	}
// }


$(document).ready(() => {
	//const $partyList = $("#party-list");
	const $div = $("#createJoin");
	const $buttons = $(".createJoin");
	const $form = $("#createJoinForm");
	const $createButton = $("#createSesh");
	const $joinButton = $("#joinSesh");

	// if (global_lat === null && global_lon === null) {
	// 	if (navigator.geolocation) {
	// 		navigator.geolocation.getCurrentPosition(getLoc, errorCallBack);
	// 	}
	// }
	

	// $joinButton.click(function() {
	// 	wait();
	// });

	console.log("USER ID: " + userId);

	//mobile device w smaller screen
	if ($(window).width() < 1000) {
		$div.css("display", "block");
		console.log($div.css("display"));
		$div.css("text-align", "center");
		$buttons.css("font-size", "300%");
		$buttons.css("width", "80%");
		$form.css("margin-bottom", "4%");
	//laptop
	} else {
		$div.css("display", "flex");
		$buttons.css("font-size", "450%");
		$buttons.css("width", "");
		$form.css("margin-bottom", "");
	}

	$("#createUserId").val(userId);
	$("#joinUserId").val(userId);

});