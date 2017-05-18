class Page {
	constructor(pageType, uri, index, itemsBeingDisplayed){
			this.pageType = pageType;
			this.uri = uri;
			this.index = index;
			this.itemsBeingDisplayed = itemsBeingDisplayed;
	}
	
	toString(){
		return this.pageType.toString() + " " + this.uri.toString() + " " + this.index.toString();
	}
}


$(document).ready(() => {
	let $browser = $("#browse");
	let spotifyApi = new SpotifyWebApi();
	let playlistUriRegex = new RegExp('spotify:user:([^:]+):playlist:([0-9A-Za-z]{22})');
	let albumUriRegex = new RegExp('spotify:album:([0-9A-Za-z]{22})');
	let artistUriRegex = new RegExp('spotify:artist:([0-9A-Za-z]{22})');

	let recentlyPlayedUri = [];
	let recentlyPlayedLimit = 50;
	let playlistLimit = 5;
	let albumsLimit = 50;
	let artistsLimit = 50;

	let hasNext = true;
	let offset = 0;
	let pages = {
		HOME: 'home',
		PLAYLISTS: 'playlists',
		SONGS: 'songs',
		ARTISTS: 'artists',
		ALBUMS: 'albums',
		SINGLE_PLAYLIST: 'single_playlist',
		SINGLE_ARTIST: 'single_artist',
		SINGLE_ALBUM: 'single_album'
				};

	let itemType = {
		PLAYLIST: 'playlist',
		ALBUM: 'album',
		ARTIST: 'artist',
		TRACK: 'track'
				};
	let currentPageType;
	let currentUri = null;
	let prevPageStack = [];
	let itemsBeingDisplayed = [];
	let recentlyPlayedUriCount;
	let uriDisplayedSet = new Set();


	spotifyApi.setAccessToken('BQDggsudksjptHQ_DaZYYfSTabtk-l1Exj-_ipxgkvmnIIjkVwZipbGKkkAeJvXYn9Lf387ZV1dgzn_cfr8NgpxB2glvyRjXHc6BqsXIKIQAT68qwDBjkha4cQm9BoNesR0g1UcfAhYARdQnhO8gnPOCoiJNYUCpuJrQiowsa1EAAhwH-NA0l-ikOZdqgH_buLsSI97OTcv3iUny8jGPn0qQ9s10U78Hw83f7l9g6JL8N9eB7zTinVeMPW4K9qHz--tIwyDpglDT_YYDmyfVOZUonwUZvG7gtzXZrAd43gKNHyY6bfPSchth1H_kFTtq8TGBccHGvM8qpP0T-8GOzGb5hA');
	
	createHomePage();
	hideOrShowBackButton()
	bindBackButtonOnClick();


	//==============================================FUNCTIONS======================================================

	function createHomePage(){
		currengPage = pages.HOME;
		clearBrowser();
		appendToBrowser(newListItem("playlists", "Playlists"));
		appendToBrowser(newListItem("songs", "Songs"));
		appendToBrowser(newListItem("albums", "Albums"));
		appendToBrowser(newListItem("artists", "Artists"));
		appendToBrowser('<h4 class=\"spotifyBrowserInListLabel\">Recently Played</h4>')
		spotifyApi.getMyRecentlyPlayedTracks({limit: recentlyPlayedLimit}, populateRecentlyPlayedContext);
	}

	function appendToBrowser(item){
		$browser.append(item)
		itemsBeingDisplayed.push(item);
	}

	function clearBrowser(){
		$browser.empty();
		itemsBeingDisplayed = [];
		uriDisplayedSet = new Set();
	}

	function fetchAppendPlaylists(){
		removeLoadButton();
		spotifyApi.getUserPlaylists({limit: playlistLimit, offset: offset, fields: ['uri', 'images', 'name']}, appendPlaylist);
	}

	function fetchAppendAlbums(){
		removeLoadButton();
		spotifyApi.getMySavedTracks({limit: albumsLimit, offset: offset}, appendAlbum);
	}

	function fetchAppendArtists(){
		removeLoadButton();
		spotifyApi.getMySavedTracks({limit: artistsLimit, offset: offset}, appendArtist);
	}

	function addLoadButton(){
		if(hasNext){
			appendToBrowser('<button type=\"button\" id=\"loadButton\">Load More</button>');
			$("#loadButton").click(loadMoreItems);
		}
	}

	function removeLoadButton(){
		$("#loadButton").remove();
	}

	function loadMoreItems(){
		switch(currentPageType){
			case pages.PLAYLISTS:
				fetchAppendPlaylists();
				break;
			case pages.ALBUMS:
				fetchAppendAlbums();
				break;
			case pages.ARTISTS:
				fetchAppendArtists();
				break;
		}
	}


	function appendItems(err, data, itemToAppendType){
		if (err) console.error(err);
		else {
			let artistIdsToFetchThumbnailOf = [];
			let items = data.items;
			if(data.next === null || data.next === undefined){
				hasNext = false;
			} else{
				hasNext = true;
				offset = data.offset + data.limit;
				console.log('hasnext', hasNext);
				console.log('offset', offset);
			}
			for (let i in items) {
				let p = null;
				
				switch(itemToAppendType){
					case itemType.PLAYLIST:
						p = items[i];
						break;
					case itemType.ALBUM:
						p = items[i].track.album;
						if(uriDisplayedSet.has(p.uri)){
							p = null;
						}
						break;
					case itemType.ARTIST:
						p = items[i].track.artists[0];
						if(uriDisplayedSet.has(p.uri)){
							p = null;
						}
						break;
				}
				//you should clean this up. SO GROSS!!!!
				console.log(p);
				if(p !== null){
					let name = p.name;
					let uri = p.uri;
					let image = "nosource";
					if(p.images !== null && p.images != undefined){
						image = p.images[0].url;
					} else{
						let matches = uri.match(artistUriRegex);
	        			let artistId = matches[1];
	        			artistIdsToFetchThumbnailOf.push(artistId);
					}
					appendToBrowser(newThumbnailItem(uri, name, image));
					uriDisplayedSet.add(uri);
				}
            }
            if(artistIdsToFetchThumbnailOf.length !== 0){
            	spotifyApi.getArtists(artistIdsToFetchThumbnailOf, {fields: ['uri', 'images', 'name']}, addThumbnails);
            }
		}
		addLoadButton();
	}

	function addThumbnails(err, data){
		let artists = data.artists;
		for(let i in artists){
			let artist = artists[i];
			let uri = artist.uri;

			if(artist.images[0] != undefined){
				let image = artist.images[0].url;
				let thumbnail = $(document.getElementById(uri)).children('img');
				console.log(image);
				console.log(thumbnail);
				thumbnail.attr('src', image);
			}else{
				console.log(artist);
			}
			
		}
	}

	function appendPlaylist(err, data){
		appendItems(err, data, itemType.PLAYLIST);
	}

	function appendAlbum(err, data){
		appendItems(err, data, itemType.ALBUM);
	}

	function appendArtist(err, data){
		appendItems(err, data, itemType.ARTIST);
	}

	function bindListElementsOnClick(){
		$("#browse li").click(listElementClickHandler);
	}

	function bindBackButtonOnClick(){
		$("#back").click(backButtonClickHandler);
	}

	function backButtonClickHandler(data){
		let newPage = prevPageStack.pop();

		if(newPage !== undefined || newPage !== null){
			clearBrowser();
			let newPageType = newPage.pageType;
			let newUri = newPage.uri;
			let index = newPage.index;
			let itemsToDisplay = newPage.itemsBeingDisplayed;
			currentPageType = newPageType;
			uri = newUri;
			for (let i in itemsToDisplay) {
                let item = itemsToDisplay[i];
                appendToBrowser(item);
            }
            bindListElementsOnClick();
            hideOrShowBackButton();
		}
	}

	function hideOrShowBackButton(){
		let $backButton = $("#back");
		if(prevPageStack.length === 0){
			$backButton.hide();
		}else{
			$backButton.show();
		}
	}

	function listElementClickHandler(data){
		hasNext = true;
		offset = 0;
		let item = data.currentTarget;
		let id = item.id;
		let prevPage = new Page(currentPageType, currentUri, getListOffset(), itemsBeingDisplayed);
		prevPageStack.push(prevPage);
		clearBrowser();

		if(albumUriRegex.test(id)){

		}else if(playlistUriRegex.test(id)){

		} else if(id === 'playlists'){
			currentPageType = pages.PLAYLISTS;
			currentUri = null;
			fetchAppendPlaylists();
		} else if(id === 'albums'){
			currentPageType = pages.ALBUMS;
			currentUri = null;
			fetchAppendAlbums();
		} else if(id === 'artists'){
			currentPageType = pages.ARTISTS;
			currentUri = null;
			fetchAppendArtists();
		}
		hideOrShowBackButton();
	}

	function populateWithAlbumView(albumId){
		
	}

	function getListOffset(){
		return 0;
	}

	function populateRecentlyPlayedContext(err, data){
		if (err) console.error(err);
		else {
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
	}

	function addRecentlyPlayedUriToList(){
		recentlyPlayedUriCount = recentlyPlayedUri.length;
		for(let i in recentlyPlayedUri){
			console.log(recentlyPlayedUri[i]);
			let uri = recentlyPlayedUri[i];
			//If the item is a playlist
			if(playlistUriRegex.test(uri)){
	        	let matches = uri.match(playlistUriRegex);
	        	let userId = matches[1];
	        	let playlistId = matches[2];
	        	//Fetch the name, uri, and images of the playlist and append it to the list.
	        	spotifyApi.getPlaylist(userId, playlistId, {fields: ['uri', 'images', 'name']})
					.then(onRecentlyPlayedContextReturn, function(err) {
						console.error(err);
					});
	    	} else if(albumUriRegex.test(uri)){
	    		//if the item is an album
	    		let matches = uri.match(albumUriRegex);
	        	let albumId = matches[1];
	        	//fetch the uri, images, and name of the album and append it to the list.
	        	spotifyApi.getAlbum(albumId, {fields: ['uri', 'images', 'name']})
					.then(onRecentlyPlayedContextReturn, function(err) {
						console.error(err);
					});
	    	}
		}
	}

	function onRecentlyPlayedContextReturn(data){
		let image = data.images[0].url;
		let name = data.name;
		let uri = data.uri;
		appendToBrowser(newThumbnailItem(uri, name, image));
		recentlyPlayedUriCount--;
		if(recentlyPlayedUriCount == 0){
			bindListElementsOnClick();
		}
	}

	// function newPlaylistItem(uri, title, imageSource){
	// 	return "<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
 //                    + "id=\"" + uri + "\">"
 //                    + "<img class=\"spotifyBrowsingThumbnail\" src=\"" + imageSource + "\"></img>"
 //                    + "<div id=\"songtitle\">" + title 
 //                    //end of song title div
 //                    + "</div>"
 //                    + "</li>"; 
	// }

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
});

