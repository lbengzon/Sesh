const $div = $("#createJoin");
const $buttons = $(".createJoin");
const $form = $("#createJoinForm");


$(window).resize(function() {
	var responsive_viewport = $(window).width();
	const $div = $("#createJoin");
	const $buttons = $(".createJoin");
	const $form = $("#createJoinForm");

	if (responsive_viewport < 420) {
		$div.css("display", "block");
		$div.css("text-align", "center");
		$buttons.css("font-size", "300%");
		$buttons.css("width", "80%");
		$form.css("margin-bottom", "4%");
	} else {
		$div.css("display", "flex");
		$buttons.css("font-size", "450%");
		$buttons.css("width", "");
		$form.css("margin-bottom", "");
	}
});


$(document).ready(() => {

	if ($(window).width() < 420) {

	} else {

	}

	//dj tab content
	const $tabContentPlaylist = $(".tabContentPlaylist");
	const $tabContentSearch = $(".tabContentSearch");
	const $tabContentOptions = $(".tabContentOptions");
	const $titles = $(".titles");
	const $listWrapper = $('.list-wrapper');

	//guest tab content
	const $tabContentRequestGuest = $(".tabContentRequestGuest");
	const $tabContentPlaylistGuest = $(".tabContentPlaylistGuest");
	const $tabContentSearchGuest = $(".tabContentSearchGuest");
	const $tabContentOptionsGuest = $(".tabContentOptionsGuest");
	const $playlistTitle = $("#playlist-title");
	const $requestTitle = $("#request-title");

	//dj tabs
	const $listview = $("#playlist-dj");
	const $search = $("#search-dj");
	const $options = $("#options-dj");

	//guest tabs
	const $requestsGuest = $("#request-guest");
	const $playlistGuest = $("#playlist-guest");
	const $searchGuest = $("#search-guest");
	const $optionsGuest = $("#options-guest");

	const $sortable = $(".sortable");

	$sortable.sortable({
		connectWith: '.sortable'
	});

	//default initial views
	$listview.addClass("active");
	$tabContentSearch.hide();
	$tabContentOptions.hide();

	$requestsGuest.addClass("active");
	$tabContentPlaylistGuest.hide();
	$tabContentSearchGuest.hide();
	$tabContentOptionsGuest.hide();
	$playlistTitle.hide();


	$search.click(function() {
		$search.addClass("active");
		$listview.removeClass("active");
		$options.removeClass("active");
		$tabContentSearch.show();
		$tabContentPlaylist.hide();
		$tabContentOptions.hide();
		$titles.hide();
		$listWrapper.height("56%");
	});

	$listview.click(function() {
		$listview.addClass("active");
		$search.removeClass("active");
		$options.removeClass("active");
		$tabContentSearch.hide();
		$tabContentPlaylist.show();
		$tabContentOptions.hide();
		$titles.show();
		$listWrapper.height("50%");
	});

	$options.click(function() {
		$options.addClass("active");
		$listview.removeClass("active");
		$search.removeClass("active");
		$tabContentSearch.hide();
		$tabContentPlaylist.hide();
		$tabContentOptions.show();
		$titles.hide();
		$listWrapper.height("56%");
	});

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