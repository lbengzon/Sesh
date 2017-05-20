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
	let albumUriRegex = new RegExp('spotify:album:([0-9A-Za-z]{22})');
	let artistUriRegex = new RegExp('spotify:artist:([0-9A-Za-z]{22})');
	let trackRegex = new RegExp('([0-9A-Za-z]{22})');


	const recentlyPlayedLimit = 50;
	const playlistLimit = 5;
	const albumsLimit = 50;
	const artistsLimit = 50;
	const albumTracksLimit = 50;
	const playlistTracksLimit = 50;
	const artistsAlbumsLimit = 20;
	const savedTracksLimit = 50;

	const scrollAutoloadOffset = 20;

	let hasNext = true;
	let offset = 0;
	let pages = {
		HOME: 'home',
		SAVED_PLAYLISTS: 'playlists',
		SAVED_SONGS: 'songs',
		SAVED_ARTISTS: 'artists',
		SAVED_ALBUMS: 'albums',
		SAVED_SINGLE_PLAYLIST: 'single_playlist',
		SAVED_SINGLE_ARTIST: 'single_artist',//displays the songs by the artists that were saved
		SAVED_SINGLE_ALBUM: 'single_album' //displays the songs in the albums that were saved
				};

	let itemType = {
		PLAYLIST: 'playlist',
		SAVED_ALBUM: 'saved_album',
		SAVED_ARTIST: 'saved_artist',
		TRACK: 'track'
				};
	let currentPageType;
	let currentUri = null;
	let prevPageStack = [];
	let recentlyPlayedUri = [];
	let recentlyPlayedUriCount;
	let uriDisplayedSet = new Set();
	let trackMap = new Map();




	setAccessToken('BQBodCIGiebAjy5_fa2uuDLwAc6GMUSyrvjXeRG1rk1VHW5pK4UCg_q4DHuQ-8e7lCce-yf91jR5NFN6WdRDfeWiNTOCzaTGU7-xRjHdmXR-R9nnlzLXqSV5-WPsnmxzALkdOMVXW_gf4UL91_vRc5VR3bTHYqnFYHz3G0hNm6ICfv4JvCHES1ehh5kOP3L_K4kK6EErTg11dM0Bywxo41bc0Fu2_NmUZHMRAhqwiHRJOAvoNno22NEingub-M9dRDqlSNeKoNQHL_TUx0eqzvADPMDla0wCWru5msghayVSGd8MihIEG0M3tFfwVQuMbnyJhdTfHJOkuy-olLEnpylupg');
	createHomePage();
	//createSearchPage();
	setUpBindsAndButtons();

	function setAccessToken(token){
		spotifyApi.setAccessToken(token);
	}

	function setUpBindsAndButtons(){
		hideOrShowBackButton()
		bindBackButtonOnClick();
		bindScrollLoading();
	}

	//==============================================FUNCTIONS======================================================
	function createSearchPage(){
		showSearchBar();
	}

	function createHomePage(){
		hideSearchBar();
		currengPage = pages.HOME;
		hasNext = false;
		clearBrowser();
		appendToBrowser(newListItem("playlists", "Playlists"));
		appendToBrowser(newListItem("songs", "Songs"));
		appendToBrowser(newListItem("albums", "Albums"));
		appendToBrowser(newListItem("artists", "Artists"));
		appendToBrowser('<h4 class=\"spotifyBrowserInListLabel\">Recently Played</h4>')
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
		let matches = currentUri.match(albumUriRegex);
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
		let matches = currentUri.match(artistUriRegex);
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
			}
		}
	}, 250);

	//Takes in the data returned from the api request and 
	//adds the items (either tracks/albums/playlists/artists depending on the itemToAppendType) to the browser.
	//Also updates the hasNext, and offset variables for paging. It then rebinds the list elements on click.
	//Add calls loadIfListHasNotOverflown
	function appendItems(data, itemToAppendType){
		let artistIdsToFetchThumbnailOf = [];
		let items = data.items;
		setOffsetHasNext(data)
		for (let i in items) {
			let p = null;
			
			switch(itemToAppendType){
				case itemType.PLAYLIST:
					p = items[i];
					break;
				case itemType.SAVED_ALBUM:
					p = items[i].track.album;
					if(uriDisplayedSet.has(p.uri)){
						continue;
					}
					break;
				case itemType.SAVED_ARTIST:
					p = items[i].track.artists[0];
					if(uriDisplayedSet.has(p.uri)){
						continue;
					}
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
			let uri = p.uri;
			let image = "nosource";
			if(p.images !== null && p.images != undefined){
				image = p.images[0].url;
			} else if(itemToAppendType === itemType.SAVED_ARTIST){
				let matches = uri.match(artistUriRegex);
				let artistId = matches[1];
				artistIdsToFetchThumbnailOf.push(artistId);
			} 
			appendToBrowser(newThumbnailItem(uri, name, image));
			uriDisplayedSet.add(uri);

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
			let uri = artist.uri;
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
		if(albumUriRegex.test(id)){
			currentPageType = pages.SAVED_SINGLE_ALBUM;
			currentUri = id;
			setBrowserHeader($(this).text());
			fetchAppendSavedAlbumTracks();
		}else if(playlistUriRegex.test(id)){
			currentPageType = pages.SAVED_SINGLE_PLAYLIST;
			currentUri = id;
			setBrowserHeader($(this).text());
			fetchAppendPlaylistTracks();
		} else if(artistUriRegex.test(id)){
			currentPageType = pages.SAVED_SINGLE_ARTIST;
			currentUri = id;
			setBrowserHeader($(this).text());
			fetchAppendSavedArtistTracks();
		}else if(id === 'playlists'){
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
		} else if(trackRegex.test(id)){
			console.log("==============YOU JUST CLICKED A TRACK=================");
			
		} else{
			console.log("this id didn't fit what we expected: ", id);
		}
		hideOrShowBackButton();
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
	    	} else if(albumUriRegex.test(uri)){
	    		//if the item is an album
	    		let matches = uri.match(albumUriRegex);
	        	let albumId = matches[1];
	        	//fetch the uri, images, and name of the album and append it to the list.
	        	spotifyApi.getAlbum(albumId, {fields: ['uri', 'images', 'name']})
					.then(onRecentlyPlayedContextReturn, apiErrorHandler);
	    	}else if(artistUriRegex.test(uri)){
	    		//if the item is an artist
	    		let matches = uri.match(artistUriRegex);
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

