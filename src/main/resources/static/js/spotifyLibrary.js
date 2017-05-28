class Page {
	constructor(pageType, uri, scrollTop, itemsBeingDisplayed, browserHeader, offset, hasNext){
			this.pageType = pageType;
			this.uri = uri;
			this.scrollTop = scrollTop;
			this.itemsBeingDisplayed = itemsBeingDisplayed;
			this.browserHeader = browserHeader;
			this.offset = offset;
			this.hasNext = hasNext;
	}
	
	toString(){
		return this.pageType.toString() + " " + this.uri.toString() + " " + this.index.toString();
	}
}


$(document).ready(() => {
	let $browser = $("#browse");
	let $browserHeader = $("#browserHeader");
	let $searchBar = $("#spotifySearch");
	let spotifyApi = new SpotifyWebApi();
	let playlistUriRegex = new RegExp('spotify:user:([^:]+):playlist:([0-9A-Za-z]{22})');
	let savedAlbumUriRegex = new RegExp('saved:spotify:album:([0-9A-Za-z]{22})');
	let savedArtistUriRegex = new RegExp('saved:spotify:artist:([0-9A-Za-z]{22})');
	let fullAlbumUriRegex = new RegExp('full:spotify:album:([0-9A-Za-z]{22})');
	let fullArtistUriRegex = new RegExp('full:spotify:artist:([0-9A-Za-z]{22})');


	let trackRegex = new RegExp('([0-9A-Za-z]{22})');


	const recentlyPlayedLimit = 50;
	const playlistLimit = 5;
	const albumsLimit = 50;
	const artistsLimit = 50;
	const albumTracksLimit = 50;
	const playlistTracksLimit = 50;
	const artistsAlbumsLimit = 20;
	const fullArtistsAlbumsLimit = 4;
	const savedTracksLimit = 50;
	const searchDebounceWait = 1000;
	const topTracksLimit = 5;
	const seeAllSearchResultsLimit = 50;

	const scrollAutoloadOffset = 20;
	const searchLimit = 4;
	//the button ids for the "see all list elements"
	const seeAll = {
		SEARCH_SONGS: 'search_songs',
		SEARCH_ARTISTS: 'search_artists',
		SEARCH_ALBUMS: 'search_albums',
		SEARCH_PLAYLISTS: 'search_playlists',
		ARTISTS_ALBUMS: 'artists_albums',
		ARTISTS_SINGLES: 'artists_singles'
	};

	let hasNext = true;
	let offset = 0;
	let pages = {
		HOME: 'home',
		SAVED_PLAYLISTS: 'playlists', //users saved playlists
		SAVED_SONGS: 'songs', //users saved songs
		SAVED_ARTISTS: 'artists',//users saved artists
		SAVED_ALBUMS: 'albums',//users saved albums
		SAVED_SINGLE_PLAYLIST: 'single_playlist',
		SAVED_SINGLE_ARTIST: 'single_artist',//displays the songs by the artists that were saved
		SAVED_SINGLE_ALBUM: 'single_album', //displays the songs in the albums that were saved
		FULL_SINGLE_ARTIST: 'full_single_artist',//the page that displays the full artist (e.g. when searched)
		FULL_SINGLE_ALBUM: 'full_single_album', //the page that displays the full album (e.g. when searched)
		SEARCH: 'search', //the search page right after you type in a query
		SEARCH_SONGS: 'search_songs', //after you click see all songs after a search
		SEARCH_ARTISTS: 'search_artists', //after you click see all artists after a search
		SEARCH_ALBUMS: 'search_albums',//after you click see all albums after a search
		SEARCH_PLAYLISTS: 'search_playlists', //after you click see all playlists after a search
		ARTISTS_ALBUMS: 'artists_albums',//the page that displays all of an artist's albums
		ARTISTS_SINGLES: 'artists_singles' //the page that displays all of an artists's singles
				};

	let itemType = {
		PLAYLIST: 'playlist',
		SAVED_ALBUM: 'saved_album',
		SAVED_ARTIST: 'saved_artist',
		TRACK: 'track',
		FULL_ARTIST: 'full_artist',
		FULL_ALBUM: 'full_album'
				};
	let currentPageType;
	let currentUri = null;
	let prevPageStack = [];
	let recentlyPlayedUri = [];
	let recentlyPlayedUriCount;
	let uriDisplayedSet = new Set();
	let trackMap = new Map();
	let currentSearchQuery = '';




	setAccessToken('BQDt8ko8mKC9optIyCp5AbaeSkCw2-IxOEoQLrH-4zW02rUkJ-ZRK_JmsQWB_Yt0Kau-vLrEfJKPFZLWzpzr2VSUb7CgITP-g4FigagRXpHxV3IZG6ALKy5qqcOFGXCDC1DLSXs78vRE_NBq-VM49IK4lcVTKsJk23AhLbT3vLntdskABC2FZwrXOLM9NfXMvriqHy9DCyOjsJ9IAn1XPr_FYaZmebz1nEa96j-Fkh7JPBYEgW8WFccPUjLnikZrfqIwFPsWVOrEAACxG9YSAC0YLnUFSOckx2nhItcPWcvW3wvMfpH2cFPTjOrkefrYJxyDFH8ydISfC0VQcQvFdMElEw');
	//createHomePage();
	createSearchPage();
	setUpBindsAndButtons();

	//==============================================FUNCTIONS======================================================

	function setAccessToken(token){
		spotifyApi.setAccessToken(token);
	}

	function setUpBindsAndButtons(){
		hideOrShowBackButton();
		bindBackButtonOnClick();
		bindScrollLoading();
		bindSearchingOnKeyUp();
	}

	function createSearchPage(){
		currentPageType = pages.SEARCH;
		hasNext = false;
		showSearchBar();
	}

	function createHomePage(){
		hideSearchBar();
		currentPageType = pages.HOME;
		hasNext = false;
		clearBrowser();
		appendToBrowser(newListItem("playlists", "Playlists"));
		appendToBrowser(newListItem("songs", "Songs"));
		appendToBrowser(newListItem("albums", "Albums"));
		appendToBrowser(newListItem("artists", "Artists"));
		appendToBrowser(newHeaderSeparator("Recently Played"));
		spotifyApi.getMyRecentlyPlayedTracks({limit: recentlyPlayedLimit}).then(populateRecentlyPlayedContext, apiErrorHandler);
		bindListElementsOnClick();
	}

	function hideSearchBar(){
		$searchBar.hide();
	}

	function showSearchBar(){
		$searchBar.show();
	}

	function appendToBrowser(item){
		let element = $(item);
		$browser.append(element);
	}

	//Emptys the browser, clears the uri set.
	function clearBrowser(){
		$browser.empty();
		uriDisplayedSet = new Set();
		trackMap = new Map();
	}
	
	//Fetches an artists albums and appends them to the browser
	function fetchAppendArtistsAlbums(){
		let matches = currentUri.match(fullArtistUriRegex);
		let artistId = matches[1];
		spotifyApi.getArtistAlbums(artistId, {album_type: ['album'], limit: albumsLimit, offset: offset, market: 'US'}).then(function(data){
			appendItems(data, itemType.FULL_ALBUM);
		})
	}

	//Fetches an artists singles and appends them to the browser
	function fetchAppendArtistsSingles(){
		let matches = currentUri.match(fullArtistUriRegex);
		let artistId = matches[1];
		spotifyApi.getArtistAlbums(artistId, {album_type: ['single'], limit: albumsLimit, offset: offset, market: 'US'}).then(function(data){
			appendItems(data, itemType.FULL_ALBUM);
		})
	}

	//Fetches the search results from the search string passed in from the api and repopulates the search page appropriately.
	let fetchPopulateSearchResults = debounce(function(searchString){
		clearBrowser();
		currentSearchQuery = searchString;
		spotifyApi.search(searchString, ['track', 'artist', 'album', 'playlist'], {limit: searchLimit}).then(populateSearchResults, apiErrorHandler);
	}, searchDebounceWait);

	//fetches and appends the song search results after the "see all songs" button in the search page is clicked
	function fetchAppendSongSearchResults(){
		spotifyApi.search(currentSearchQuery, ['track'], {limit: seeAllSearchResultsLimit, offset: offset}).then(function(data){
			appendItems(data.tracks, itemType.TRACK);
		})
	}

	//fetches and appends the artist search results after the "see all artists "button in the search page is clicked
	function fetchAppendArtistSearchResults(){
		spotifyApi.search(currentSearchQuery, ['artist'], {limit: seeAllSearchResultsLimit, offset: offset}).then(function(data){
			appendItems(data.artists, itemType.FULL_ARTIST);
		})
	}

	//fetches and appends the album search results after the "see all albums" button in the search page is clicked
	function fetchAppendAlbumSearchResults(){
		spotifyApi.search(currentSearchQuery, ['album'], {limit: seeAllSearchResultsLimit, offset: offset}).then(function(data){
			appendItems(data.albums, itemType.FULL_ALBUM);
		})
	}

	//fetches and appends the playlist search results after the "see all playlists" button in the search page is clicked
	function fetchAppendPlaylistSearchResults(){
		spotifyApi.search(currentSearchQuery, ['playlist'], {limit: seeAllSearchResultsLimit, offset: offset}).then(function(data){
			appendItems(data.playlists, itemType.PLAYLIST);
		})
	}

	//Fetches all the tracks in an album and appends it to the browser.
	function fetchAppendFullAlbumTracks(){
		let matches = currentUri.match(fullAlbumUriRegex);
		let albumId = matches[1];
		spotifyApi.getAlbumTracks(albumId, {limit: albumTracksLimit, offset: offset}).then(function(data){
			appendItems(data, itemType.TRACK);
		});
	}

	//Populates the full artist page (e.g when you search and click on an artist)
	function fetchPopulateFullArtist(){
		let matches = currentUri.match(fullArtistUriRegex);
		let artistId = matches[1];
		spotifyApi.getArtistTopTracks(artistId, "US", {limit: topTracksLimit}).then(function(topTracksData){
			spotifyApi.getArtistAlbums(artistId, {album_type: ['album'], limit: fullArtistsAlbumsLimit, market: 'US'}).then(function(albumsData){
				spotifyApi.getArtistAlbums(artistId, {album_type: ['single'], limit: fullArtistsAlbumsLimit, market: 'US'}).then(function(singlesData){
					if(topTracksData.tracks.length > 5){
						topTracksData.items = topTracksData.tracks.slice(0,5);
					} else{
						topTracksData.items = topTracksData.tracks;
					}

					appendToBrowser(newHeaderSeparator("Top Tracks"))
					appendItems(topTracksData, itemType.TRACK);
					appendToBrowser(newHeaderSeparator("Albums"))
					appendItems(albumsData, itemType.FULL_ALBUM);
					appendToBrowser(newListItem(seeAll.ARTISTS_ALBUMS, "See all albums"));
					appendToBrowser(newHeaderSeparator("Singles"))
					appendItems(singlesData, itemType.FULL_ALBUM);
					appendToBrowser(newListItem(seeAll.ARTISTS_SINGLES, "See all singles"));
					bindListElementsOnClick()

				});
			}, apiErrorHandler);
		}, apiErrorHandler);
	}


	//fetches more saved albums if there are any and appends it to the list
	function fetchAppendSavedAlbums(){
		spotifyApi.getMySavedTracks({limit: albumsLimit, offset: offset}).then(appendAlbum, apiErrorHandler);
	}

	//fetches more saved artists if there are any and appends it to the list
	function fetchAppendSavedArtists(){
		spotifyApi.getMySavedTracks({limit: artistsLimit, offset: offset}).then(appendArtist, apiErrorHandler);
	}

	//fetches more playlists if there are any and appends it to the list
	function fetchAppendPlaylists(){
		spotifyApi.getUserPlaylists({limit: playlistLimit, offset: offset, fields: ['uri', 'images', 'name']}).then(appendPlaylist, apiErrorHandler);
	}

	//fetches more playlists tracks if there are any and appends it to the list
	function fetchAppendPlaylistTracks(){
		let matches = currentUri.match(playlistUriRegex);
	    let userId = matches[1];
	    let playlistId = matches[2];
	    spotifyApi.getPlaylistTracks(userId, playlistId, {limit: playlistTracksLimit, offset: offset}).then(appendSavedTracks, apiErrorHandler);
	}

	//fetches more album tracks if there are any and appends it to the list
	function fetchAppendSavedAlbumTracks(){
		let matches = currentUri.match(savedAlbumUriRegex);
		let albumId = matches[1];
		spotifyApi.getAlbumTracks(albumId, {limit: albumTracksLimit, offset: offset}).then(function(data){
			setOffsetHasNext(data);
			let tracks = data.items;
			let trackIds = [];
			for(let i in tracks){
				let track = tracks[i];
				trackMap.set(track.id, track);
				trackIds.push(track.id);
			}
			if(trackIds.length !== 0){
				appendTracksIfSaved(trackIds, trackMap);
			}
		}, apiErrorHandler);
	}

	//fetches more saved tracks if there are any and appends it to the list
	function fetchAppendSavedTracks(){
		spotifyApi.getMySavedTracks({limit: savedTracksLimit, offset: offset}).then(appendSavedTracks, apiErrorHandler);
	}

	//ALSO TRY TO FIX THIS FUNCTION!!!! GROSSSSSSSSSSS
	function fetchAppendSavedArtistTracks(){
		let matches = currentUri.match(savedArtistUriRegex);
		let artistId = matches[1];
		spotifyApi.getArtistAlbums(artistId, {limit: artistsAlbumsLimit, offset: offset, album_type: ['album', 'single']})
			.then(function(data) {
				setOffsetHasNext(data);
				return data.items.map(function(a) { return a.id; });
			}, apiErrorHandler)
			.then(function(albums) {
				return spotifyApi.getAlbums(albums);
			}, apiErrorHandler).then(function(data) {
				
				let albums = data.albums;
				for(let i in albums){
					let tracks = albums[i].tracks.items;
					let albumTrackIdsUnique = [];
					for(let j in tracks){
						let track = tracks[j];
						let artistIds = track.artists.map(function(a){return a.id;});
						if(!trackMap.has(track.id) && artistIds.indexOf(artistId) > -1){
							albumTrackIdsUnique.push(track.id)
							trackMap.set(track.id, track);
						}
					}
					if(albumTrackIdsUnique.length !== 0){
						appendTracksIfSaved(albumTrackIdsUnique, trackMap);
					}
				}
			}, apiErrorHandler);	
	}

	//Sets the has next and offset instance variables from the passed in data returned from the api request.
	function setOffsetHasNext(data){
		if(data.next === null || data.next === undefined){
			hasNext = false;
		} else{
			hasNext = true;
			offset = data.offset + data.limit;
		}
	}

	//Given the track ids and the track map, will append the tracks that are saved by the user to the browser list.
	function appendTracksIfSaved(trackIdsUnique, trackMap){
		spotifyApi.containsMySavedTracks(trackIdsUnique).then(function(data) {
			appendTrackIfSaved(data, trackIdsUnique, trackMap);
		}, apiErrorHandler)
	}

	//Given the data returned from the containsMySavedTracks api call (array of booleans of whether
	//or not the album track id is saved), and the 
	//album track ids, add the tracks that are saved by the user.
	function appendTrackIfSaved(data, albumTrackIdsUnique, trackMap){
		for(let i in data){
			let isSaved = data[i];
			if(isSaved){
				let trackId = albumTrackIdsUnique[i];
				let track = trackMap.get(trackId);
				appendToBrowser(newTrackListItem(trackId, track.name))
			}
		}
		bindListElementsOnClick();
		loadMoreItemsIfBottomOfList();
	}



	//Loads more items (depending on the current page type) into the browser if there are any.
	//Also is debounced so that the function cannot be called to often so that we dont get too many requests error 
	//from the spotify api.
	let loadMoreItems = debounce(function(){
		if(hasNext){
			switch(currentPageType){
				case pages.SAVED_PLAYLISTS:
					fetchAppendPlaylists();
					break;
				case pages.SAVED_ALBUMS:
					fetchAppendSavedAlbums();
					break;
				case pages.SAVED_ARTISTS:
					fetchAppendSavedArtists();
					break;
				case pages.SAVED_SINGLE_ARTIST:
					fetchAppendSavedArtistTracks();
					break;
				case pages.SAVED_SINGLE_ALBUM:
					fetchAppendSavedAlbumTracks();
					break;
				case pages.SAVED_SINGLE_PLAYLIST:
					fetchAppendPlaylistTracks();
					break;
				case pages.SAVED_SONGS:
					fetchAppendSavedTracks();
					break;
				case pages.SEARCH_SONGS:
					fetchAppendSongSearchResults();
					break;
				case pages.SEARCH_ARTISTS:
					fetchAppendArtistSearchResults();
					break;
				case pages.SEARCH_ALBUMS:
					fetchAppendAlbumSearchResults();
					break;
				case pages.SEARCH_PLAYLISTS:
					fetchAppendPlaylistSearchResults();
					break;
				case pages.ARTISTS_ALBUMS:
					fetchAppendArtistsAlbums();
					break;
				case pages.ARTISTS_SINGLES:
					fetchAppendArtistsSingles();
					break;
			}
		}
	}, 250);

	//Takes in the data returned from the api request and 
	//adds the items (either tracks/albums/playlists/artists depending on the itemToAppendType) to the browser.
	//Also updates the hasNext, and offset variables for paging. It then rebinds the list elements on click.
	//Add calls loadIfListHasNotOverflown
	//The data should have the fields 
	function appendItems(data, itemToAppendType){
		let artistIdsToFetchThumbnailOf = [];
		let items = data.items;
		setOffsetHasNext(data)
		for (let i in items) {
			let p = null;
			let uri = "";
			switch(itemToAppendType){
				case itemType.PLAYLIST:
					p = items[i];
					break;
				case itemType.SAVED_ALBUM:
					p = items[i].track.album;
					if(uriDisplayedSet.has(p.uri)){
						continue;
					}
					uri = "saved:";
					break;
				case itemType.SAVED_ARTIST:
					p = items[i].track.artists[0];
					if(uriDisplayedSet.has(p.uri)){
						continue;
					}
					uri = "saved:";
					break;
				case itemType.FULL_ARTIST:
				case itemType.FULL_ALBUM:
					p = items[i];
					uri = "full:";
					break;
				case itemType.TRACK:
					p = items[i];
					if(p === null){
						continue;
					}
					appendToBrowser(newTrackListItem(p.uri, p.name));
					continue;
					break;
			}
			//you should clean this up. SO GROSS!!!!

			let name = p.name;
			uri = uri + p.uri;
			let image = "nosource";
			if(p.images !== null && p.images != undefined && p.images.length !== 0){
				image = p.images[0].url;
			} else if(itemToAppendType === itemType.SAVED_ARTIST){
				let matches = uri.match(savedArtistUriRegex);
				let artistId = matches[1];

				artistIdsToFetchThumbnailOf.push(artistId);
			} 
			appendToBrowser(newThumbnailItem(uri, name, image));
			uriDisplayedSet.add(p.uri);

        }
        if(artistIdsToFetchThumbnailOf.length !== 0){
        	spotifyApi.getArtists(artistIdsToFetchThumbnailOf, {fields: ['uri', 'images', 'name']}).then(addThumbnails, apiErrorHandler);
        }
		bindListElementsOnClick()
		loadMoreItemsIfBottomOfList();
	}

	//This is for when you dont have the thumbnails and you add that after you display the list.
	function addThumbnails(data){
		let artists = data.artists;
		for(let i in artists){
			let artist = artists[i];
			let uri = "saved:" + artist.uri;
			let $listElement  = $(document.getElementById(uri));
			if(artist.images[0] != undefined){
				let image = artist.images[0].url;
				let thumbnail = $listElement.children('img');
				thumbnail.attr('src', image);
			}
		}
	}

	function appendPlaylist(data){
		appendItems(data, itemType.PLAYLIST);
	}

	function appendAlbum(data){
		appendItems(data, itemType.SAVED_ALBUM);
	}

	function appendArtist(data){
		appendItems(data, itemType.SAVED_ARTIST);
	}

	function appendTracks(data){
		appendItems(data, itemType.TRACK);
	}

	function appendSavedTracks(data){
    	let items = data.items.map(function(a) { return a.track; });
    	data.items = items;
    	appendTracks(data);
	}

	//Is called when the search results are returned from the api. Populates the page with the songs, artists, albums, and playlists search results.
	function populateSearchResults(data){
		appendToBrowser(newHeaderSeparator('Songs'));
		appendItems(data.tracks, itemType.TRACK);
		appendToBrowser(newListItem(seeAll.SEARCH_SONGS, "See all songs"));
		appendToBrowser(newHeaderSeparator('Artists'));
		appendItems(data.artists, itemType.FULL_ARTIST);
		appendToBrowser(newListItem(seeAll.SEARCH_ARTISTS, "See all artists"));
		appendToBrowser(newHeaderSeparator('Albums'));
		appendItems(data.albums, itemType.FULL_ALBUM);
		appendToBrowser(newListItem(seeAll.SEARCH_ALBUMS, "See all albums"));
		appendToBrowser(newHeaderSeparator('Playlists'));
		appendItems(data.playlists, itemType.PLAYLIST);
		appendToBrowser(newListItem(seeAll.SEARCH_PLAYLISTS, "See all playlists"));
		bindListElementsOnClick()
	}

	//first unbinds all the click handlers for the list item then rebinds it.
	function bindListElementsOnClick(){
		$("#browse li").off('click', listElementClickHandler);
		$("#browse li").on('click', listElementClickHandler);
	}

	//Binds the back button click handler.
	function bindBackButtonOnClick(){
		$("#back").on('click', backButtonClickHandler);
	}

	//Attaches a handler to when your scrolling and you have reached the bottom of the list.
	function bindScrollLoading(){
		$("#browseScroll").on('scroll', function() {
	        loadMoreItemsIfBottomOfList();
	    });
	}

	//Checks to see where in the browser list you are and calls load more items if your are at the bottom of the list;
	function loadMoreItemsIfBottomOfList(){
		if($("#browseScroll").scrollTop() + $("#browseScroll").innerHeight() >= $("#browseScroll")[0].scrollHeight) {
	        loadMoreItems();
	    }
	}

	//Binds the the fetchPopulateSearchResults (with debouncing) function to the keyup event of the search bar.
	function bindSearchingOnKeyUp(){
		$searchBar.on('keyup', function(data) {
			let searchQuery = $searchBar.val();
	        fetchPopulateSearchResults(searchQuery);
	    });
	}

	//This is called when you click the back button.
	//Gets the previous page from the stack, and makes the appropriate changes.
	function backButtonClickHandler(data){
		let newPage = prevPageStack.pop();
		if(newPage !== undefined || newPage !== null){
			clearBrowser();
			let newPageType = newPage.pageType;
			let newUri = newPage.uri;
			let scrollTop = newPage.scrollTop;
			let itemsToDisplay = newPage.itemsBeingDisplayed;
			let newBrowserHeader = newPage.browserHeader;
			currentPageType = newPageType;
			currentUri = newUri;
			hasNext = newPage.hasNext;
			offset = newPage.offset;
			setBrowserHeader(newBrowserHeader);
            appendToBrowser(itemsToDisplay);
            bindListElementsOnClick();
            hideOrShowBackButton();
            $("#browseScroll").scrollTop(scrollTop);
            $("#browse li").removeClass("selected");
            hideOrShowSearchBar()
		}
	}

	//Hides or shows the back button depending on whether there is a page in the previous stack.
	function hideOrShowBackButton(){
		let $backButton = $("#back");
		if(prevPageStack.length === 0){
			$backButton.hide();
		}else{
			$backButton.show();
		}
	}

	//Hides or shows the search bar depending on whether current page is the search page.
	function hideOrShowSearchBar(){
		if(currentPageType === pages.SEARCH){
        	showSearchBar();
        } else{
        	hideSearchBar();
        }
	}

	//The handler for when an item on the browser list is clicked.
	//It saves the current page and adds it to the prev page stack.
	//Then depending on the id of the element, performs some action.
	function listElementClickHandler(data){
		//creates the page to be pushed to the prev stack
		let item = data.currentTarget;
		let scrollTop = $("#browseScroll").scrollTop();
		let itemsBeingDisplayed = $browser[0].innerHTML;
		let id = item.id;
		let oldBrowserHeader = $browserHeader.text();
		let prevPage = new Page(currentPageType, currentUri, scrollTop, itemsBeingDisplayed, oldBrowserHeader, offset, hasNext);
		prevPageStack.push(prevPage);

		//reset necessary variables
		clearBrowser();
		hasNext = true;
		offset = 0;

		//Switch depending on the id of the element that was clicked
		if(savedAlbumUriRegex.test(id)){
			currentPageType = pages.SAVED_SINGLE_ALBUM;
			currentUri = id;
			setBrowserHeader($(this).text());
			fetchAppendSavedAlbumTracks();
		}else if(playlistUriRegex.test(id)){
			currentPageType = pages.SAVED_SINGLE_PLAYLIST;
			currentUri = id;
			setBrowserHeader($(this).text());
			fetchAppendPlaylistTracks();
		} else if(savedArtistUriRegex.test(id)){
			currentPageType = pages.SAVED_SINGLE_ARTIST;
			currentUri = id;
			setBrowserHeader($(this).text());
			fetchAppendSavedArtistTracks();
		} else if(fullArtistUriRegex.test(id)){
			currentPageType = pages.FULL_SINGLE_ARTIST;
			currentUri = id;
			setBrowserHeader($(this).text());
			fetchPopulateFullArtist();
		} else if(fullAlbumUriRegex.test(id)){
			currentPageType = pages.FULL_SINGLE_ALBUM;
			currentUri = id;
			setBrowserHeader($(this).text());
			fetchAppendFullAlbumTracks();
		} else if(id === 'playlists'){
			currentPageType = pages.SAVED_PLAYLISTS;
			currentUri = null;
			setBrowserHeader("PLAYLISTS");
			fetchAppendPlaylists();
		} else if(id === 'albums'){
			currentPageType = pages.SAVED_ALBUMS;
			currentUri = null;
			setBrowserHeader("ALBUMS");
			fetchAppendSavedAlbums();
		} else if(id === 'artists'){
			currentPageType = pages.SAVED_ARTISTS;
			currentUri = null;
			setBrowserHeader("ARTISTS");
			fetchAppendSavedArtists();
		} else if(id === 'songs'){
			currentPageType = pages.SAVED_SONGS;
			currentUri = null;
			setBrowserHeader("SONGS");
			fetchAppendSavedTracks();
		} else if(id === seeAll.SEARCH_SONGS){
			currentPageType = pages.SEARCH_SONGS;
			currentUri = null;
			setBrowserHeader("\""+currentSearchQuery+"\" in songs");
			fetchAppendSongSearchResults();
		} else if(id === seeAll.SEARCH_ARTISTS){
			currentPageType = pages.SEARCH_ARTISTS;
			currentUri = null;
			setBrowserHeader("\""+currentSearchQuery+"\" in artists");
			fetchAppendArtistSearchResults();
		} else if(id === seeAll.SEARCH_ALBUMS){
			currentPageType = pages.SEARCH_ALBUMS;
			currentUri = null;
			setBrowserHeader("\""+currentSearchQuery+"\" in albums");
			fetchAppendAlbumSearchResults();
		} else if(id === seeAll.SEARCH_PLAYLISTS){
			currentPageType = pages.SEARCH_PLAYLISTS;
			currentUri = null;
			setBrowserHeader("\""+currentSearchQuery+"\" in playlists");
			fetchAppendPlaylistSearchResults();
		} else if(id === seeAll.ARTISTS_ALBUMS){
			currentPageType = pages.ARTISTS_ALBUMS;
			//dont change current uri cause we needs it to be the same
			setBrowserHeader("Albums");
			fetchAppendArtistsAlbums();
		} else if(id === seeAll.ARTISTS_SINGLES){
			currentPageType = pages.ARTISTS_SINGLES;
			//dont change current uri cause we needs it to be the same
			setBrowserHeader("Singles");
			fetchAppendArtistsSingles();
		} else if(trackRegex.test(id)){
			//this else if statement should always be last!
			console.log("==============YOU JUST CLICKED A TRACK WITH ID=================", id);
			
		} else{
			console.log("this id didn't fit what we expected: ", id);
		}
		hideOrShowBackButton();
		hideOrShowSearchBar();
	}


	//Sets the header of the browser
	function setBrowserHeader(newHeader){
		$browserHeader.text(newHeader);
	}

	//This is the callback for the get recently played tracks 
	//For each of the tracks, it checks if there is a context and if there is it appends it to the
	//recentlyplayeduri list. Then calls addRecentlyPlayedUriToList.
	function populateRecentlyPlayedContext(data){
		let items = data.items;
		for (let i in items) {
            let context = items[i].context;
            if(context != undefined){
            	let uri = context.uri;
            	if(recentlyPlayedUri.indexOf(uri) == -1){
            		recentlyPlayedUri.push(uri);
            	}
            }
        }
        addRecentlyPlayedUriToList();
	}

	//For each of the recentlyplayeduri's 
	//fetch the corresponding full context (playlist/albums/artists) and add it to the browser list.
	function addRecentlyPlayedUriToList(){
		recentlyPlayedUriCount = recentlyPlayedUri.length;
		for(let i in recentlyPlayedUri){
			let uri = recentlyPlayedUri[i];
			//If the item is a playlist
			if(playlistUriRegex.test(uri)){
	        	let matches = uri.match(playlistUriRegex);
	        	let userId = matches[1];
	        	let playlistId = matches[2];
	        	//Fetch the name, uri, and images of the playlist and append it to the list.
	        	spotifyApi.getPlaylist(userId, playlistId, {fields: ['uri', 'images', 'name']})
					.then(onRecentlyPlayedContextReturn, apiErrorHandler);
	    	} else if(savedAlbumUriRegex.test(uri)){
	    		//if the item is an album
	    		let matches = uri.match(savedAlbumUriRegex);
	        	let albumId = matches[1];
	        	//fetch the uri, images, and name of the album and append it to the list.
	        	spotifyApi.getAlbum(albumId, {fields: ['uri', 'images', 'name']})
					.then(onRecentlyPlayedContextReturn, apiErrorHandler);
	    	}else if(savedArtistUriRegex.test(uri)){
	    		//if the item is an artist
	    		let matches = uri.match(savedArtistUriRegex);
	        	let artistId = matches[1];
	        	//fetch the uri, images, and name of the album and append it to the list.
	        	spotifyApi.getArtist(artistId, {fields: ['uri', 'images', 'name']})
					.then(onRecentlyPlayedContextReturn, apiErrorHandler);
	    	}
		}
	}



	//When the full context has returned, add it to the browser list. Remove it from the list of uri's, and if that 
	//leaves the recentlyPlayedUri empty (meaning all the full contexts have returned), then rebind the list elements.
	function onRecentlyPlayedContextReturn(data){
		let image = "nosource";
		if(data.images !== null && data.images != undefined){
			image = data.images[0].url;
		}
		let name = data.name;
		let uri = data.uri;
		if(savedAlbumUriRegex.test(uri) || savedArtistUriRegex.test(uri)){
    		uri = "saved:" + uri;
    	}
		appendToBrowser(newThumbnailItem(uri, name, image));
		recentlyPlayedUriCount--;
		if(recentlyPlayedUriCount == 0){
			bindListElementsOnClick();
		}
	}

	function apiErrorHandler(err){
		if (err) console.error(err);
	}

	function newThumbnailItem(uri, title, imageSource){
		return "<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
                    + "id=\"" + uri + "\">"
                    + "<img class=\"spotifyBrowsingThumbnail\" src=\"" + imageSource + "\"></img>"
                    + "<div id=\"songtitle\">" + title 
                    //end of song title div
                    + "</div>"
                    + "</li>"; 
	}

	function newListItem(id, title){
		return "<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
                    + "id=\"" + id + "\" class=\"listItem\">"
                    + "<div id=\"songtitle\">" + title 
                    //end of song title div
                    + "</div>"
                    + "</li>"; 
	}

	function newTrackListItem(id, title){
		return "<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
                    + "id=\"" + id + "\" class=\"listItem\">"
                    + "<div id=\"songtitle\">" + title 
                    //end of song title div
                    + "</div>"
                    + "</li>"; 
	}

	function newHeaderSeparator(label){
		return "<h4 class=\"spotifyBrowserInListLabel\">"+label+"</h4>";
	}

	function debounce(func, wait, immediate) {
        var timeout;
        return function() {
            var context = this, args = arguments;
            var later = function() {
                timeout = null;
                if (!immediate) func.apply(context, args);
            };
            var callNow = immediate && !timeout;
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
            if (callNow) func.apply(context, args);
        };
    };
});

