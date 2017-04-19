$(document).ready(() => {
	const $tabContentPlaylist = $(".tabContentPlaylist");
	const $tabContentSearch = $(".tabContentSearch");
	const $tabContentOptions = $(".tabContentOptions");

	const $listview = $("#listview");
	const $search = $("#search");
	const $options = $("#options");

	const $sortable = $(".sortable");

	$sortable.sortable({
		connectWith: '.sortable'

	});

	$listview.addClass("active");
	$tabContentSearch.hide();
	$tabContentOptions.hide();

	$search.click(function() {
		$search.addClass("active");
		$listview.removeClass("active");
		$options.removeClass("active");
		$tabContentSearch.show();
		$tabContentPlaylist.hide();
		$tabContentOptions.hide();
	});

	$listview.click(function() {
		$listview.addClass("active");
		$search.removeClass("active");
		$options.removeClass("active");
		$tabContentSearch.hide();
		$tabContentPlaylist.show();
		$tabContentOptions.hide();
	});

	$options.click(function() {
		$options.addClass("active");
		$listview.removeClass("active");
		$search.removeClass("active");
		$tabContentSearch.hide();
		$tabContentPlaylist.hide();
		$tabContentOptions.show();
	});


});