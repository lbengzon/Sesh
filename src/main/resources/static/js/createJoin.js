$(document).ready(() => {
	const $div = $("#createJoin");
	const $buttons = $(".createJoin");
	const $form = $("#createJoinForm");
	const $createButton = $("#createSesh");
	const $joinButton = $("#joinSesh");

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

	// $createButton.on("click", event => {
	// 	const postParameter = {userId: userId};
	// 	$.post("/create", postParameter, response => {
	// 		console.log("id sent to create backend");
	// 	});
	// });

	// $joinButton.on("click", event => {
	// 	const postParameter = {userId: userId};
	// 	$.post("/join", postParameter, response => {
	// 		console.log("id sent to join backend");
	// 	});
	// });

});