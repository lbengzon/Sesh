function hoverOn(x) {
	x.className = 'selected';
}

function hoverOff(x) {
	x.classList.remove('selected');
}


$(document).ready(() => {

	$("#userId").val(userId);
	
	const $partyList = $("#party-list ul");
	$partyList.on("click", event => {
		$listItems = $("li");
		$selected = $listItems.filter('.selected');
		$("#partyId").val($selected.attr('id'));
	});

	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			const lat = position.coords.latitude;
			const lon = position.coords.longitude
			const postParameters = {latitude: lat, longitude: lon};
			$.post("/join", postParameters, responseJSON => {
				console.log("parties should be received here");
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