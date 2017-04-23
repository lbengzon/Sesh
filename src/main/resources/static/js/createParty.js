$(document).ready(() => {

	//dj tab content
	const $tabContentPlaylist = $(".tabContentPlaylist");
	const $tabContentSearch = $(".tabContentSearch");
	const $tabContentOptions = $(".tabContentOptions");
	const $titles = $(".titles");
	const $listWrapper = $('.list-wrapper');

	//dj tabs
	const $listview = $("#playlist-dj");
	const $search = $("#search-dj");
	const $options = $("#options-dj");

	const $sortable = $(".sortable");

	$sortable.sortable({
		connectWith: '.sortable'
	});

	//default initial views
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
});