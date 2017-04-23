$(document).ready(() => {

	//guest tab content
	const $tabContentRequestGuest = $(".tabContentRequestGuest");
	const $tabContentPlaylistGuest = $(".tabContentPlaylistGuest");
	const $tabContentSearchGuest = $(".tabContentSearchGuest");
	const $tabContentOptionsGuest = $(".tabContentOptionsGuest");
	const $playlistTitle = $("#playlist-title");
	const $requestTitle = $("#request-title");
	const $listWrapper = $('.list-wrapper');


	//guest tabs
	const $requestsGuest = $("#request-guest");
	const $playlistGuest = $("#playlist-guest");
	const $searchGuest = $("#search-guest");
	const $optionsGuest = $("#options-guest");

	$requestsGuest.addClass("active");
	$tabContentPlaylistGuest.hide();
	$tabContentSearchGuest.hide();
	$tabContentOptionsGuest.hide();
	$playlistTitle.hide();


	$playlistGuest.click(function() {
		$playlistGuest.addClass("active");
		$requestsGuest.removeClass("active");
		$searchGuest.removeClass("active");
		$optionsGuest.removeClass("active");
		$tabContentRequestGuest.hide();
		$tabContentOptionsGuest.hide();
		$tabContentSearchGuest.hide();
		$tabContentPlaylistGuest.show();
		$requestTitle.hide();
		$playlistTitle.show();
		$listWrapper.height("50%");
	});

	$requestsGuest.click(function() {
		$playlistGuest.removeClass("active");
		$requestsGuest.addClass("active");
		$searchGuest.removeClass("active");
		$optionsGuest.removeClass("active");
		$tabContentRequestGuest.show();
		$tabContentOptionsGuest.hide();
		$tabContentSearchGuest.hide();
		$tabContentPlaylistGuest.hide();
		$playlistTitle.hide();
		$requestTitle.show();
		$listWrapper.height("50%");
	});

	$searchGuest.click(function() {
		$playlistGuest.removeClass("active");
		$requestsGuest.removeClass("active");
		$searchGuest.addClass("active");
		$optionsGuest.removeClass("active");
		$tabContentRequestGuest.hide();
		$tabContentOptionsGuest.hide();
		$tabContentSearchGuest.show();
		$tabContentPlaylistGuest.hide();
		$playlistTitle.hide();
		$requestTitle.hide();
		$listWrapper.height("56%");
	});

		$optionsGuest.click(function() {
		$playlistGuest.removeClass("active");
		$requestsGuest.removeClass("active");
		$searchGuest.removeClass("active");
		$optionsGuest.addClass("active");
		$tabContentRequestGuest.hide();
		$tabContentOptionsGuest.show();
		$tabContentSearchGuest.hide();
		$tabContentPlaylistGuest.hide();
		$playlistTitle.hide();
		$requestTitle.hide();
		$listWrapper.height("56%");
	});


});