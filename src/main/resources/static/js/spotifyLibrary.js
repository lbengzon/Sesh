class Page {
	constructor(pageType, uri, scrollTop, itemsBeingDisplayed){
			this.pageType = pageType;
			this.uri = uri;
			this.scrollTop = scrollTop;
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
	let trackRegex = new RegExp('([0-9A-Za-z]{22})');


	let recentlyPlayedUri = [];
	let recentlyPlayedLimit = 50;
	let playlistLimit = 5;
	let albumsLimit = 50;
	let artistsLimit = 50;
	let albumTracksLimit = 50;
	let playlistTracksLimit = 50;
	let artistsAlbumsLimit = 20;
	let savedTracksLimit = 50;

	let scrollAutoloadOffset = 20;

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
	let recentlyPlayedUriCount;
	let uriDisplayedSet = new Set();
	let trackMap = new Map();




	spotifyApi.setAccessToken('BQDPLS3frFaep3HZimI38L5NXwje9Dt51uZMsHXqFbgCAKReXgvixFW4WK2I4Cdcg29nich320fL5xdpuffp0V_8JPazmiFmFCx77MHaZE99ySiTzDU-bqUJ3JYDfrLxK5RA0zzB1vN6V75oTub2TptIgaErLvDuGJGG66yilCAbutHE12pP2obPNeQ7qiM6n4G0JqJUMQNa2ZzpWLzSQyRLGqE9Vz8LWSGdodbGZiOC77lwLJ28CPz8QJWCE56SdNMNppToFSeFnB4Aozc527Mu5ixQSXb5Sb7y2tOGIMFm8L6dmELB4_GGPFjmPwiuzKOGQ6sz5uV4M5Vg3_Rt4h1ayA');
	createHomePage();
	hideOrShowBackButton()
	bindBackButtonOnClick();
	bindScrollLoading();


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
		bindListElementsOnClick();

	}

	function appendToBrowser(item){
		let element = $(item);
		$browser.append(element);
	}

	function clearBrowser(){
		$browser.empty();
		uriDisplayedSet = new Set();
		allTracks = [];
	}

	//fetches more playlists if there are any and appends it to the list
	function fetchAppendPlaylists(){
		spotifyApi.getUserPlaylists({limit: playlistLimit, offset: offset, fields: ['uri', 'images', 'name']}, appendPlaylist);
	}

	//fetches more playlists tracks if there are any and appends it to the list
	function fetchAppendPlaylistTracks(){
		let matches = currentUri.match(playlistUriRegex);
	    let userId = matches[1];
	    let playlistId = matches[2];
	    spotifyApi.getPlaylistTracks(userId, playlistId, {limit: playlistTracksLimit, offset: offset}, appendSavedTracks);
	}

	//fetches more album tracks if there are any and appends it to the list
	function fetchAppendAlbumTracks(){
		let matches = currentUri.match(albumUriRegex);
		let albumId = matches[1];
		spotifyApi.getAlbumTracks(albumId, {limit: albumTracksLimit, offset: offset}, appendTracks);
	}

	//fetches more saved tracks if there are any and appends it to the list
	function fetchAppendSavedTracks(){
		spotifyApi.getMySavedTracks({limit: savedTracksLimit, offset: offset}, appendSavedTracks);
	}

	//ALSO TRY TO FIX THIS FUNCTION!!!! GROSSSSSSSSSSS
	function fetchAppendArtistTracks(){
		let matches = currentUri.match(artistUriRegex);
		let artistId = matches[1];
		spotifyApi.getArtistAlbums(artistId, {limit: artistsAlbumsLimit, offset: offset, album_type: ['album', 'single']})
			.then(function(data) {
				if(data.next === null || data.next === undefined){
					hasNext = false;
				} else{
					hasNext = true;
					offset = data.offset + data.limit;
				}
				return data.items.map(function(a) { return a.id; });
			})
			.then(function(albums) {
				return spotifyApi.getAlbums(albums);
			}).then(function(data) {
				
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
						spotifyApi.containsMySavedTracks(albumTrackIdsUnique).then(function(data) {
							appendTrackIfSaved(data, albumTrackIdsUnique, trackMap);
						})
					}
				}
			});	
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
		loadIfListHasNotOverflown();
	}


	//fetches more saved albums if there are any and appends it to the list
	function fetchAppendAlbums(){
		spotifyApi.getMySavedTracks({limit: albumsLimit, offset: offset}, appendAlbum);
	}

	//fetches more saved artists if there are any and appends it to the list
	function fetchAppendArtists(){
		spotifyApi.getMySavedTracks({limit: artistsLimit, offset: offset}, appendArtist);
	}

	//Will call loadMoreItems in an attempt to populate the list with more items if the items have not filled the browser window yet.
	function loadIfListHasNotOverflown(){
		let browserScrollWindow = $("#browseScroll")[0];

		if(browserScrollWindow.scrollHeight < browserScrollWindow.clientHeight){
			loadMoreItems();
		}
	}

	//Loads more items (depending on the current page type) into the browser if there are any.
	//Also is debounced so that the function cannot be called to often so that we dont get too many requests error 
	//from the spotify api.
	let loadMoreItems = debounce(function(){
		if(hasNext){
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
				case pages.SINGLE_ARTIST:
					fetchAppendArtistTracks();
					break;
				case pages.SINGLE_ALBUM:
					fetchAppendAlbumTracks();
				case pages.SINGLE_PLAYLIST:
					fetchAppendPlaylistTracks();
				case pages.SONGS:
					fetchAppendSavedTracks();
			}
		}
	}, 250);

	//Takes in the data returned from the api request and 
	//adds the items (either tracks/albums/playlists/artists depending on the itemToAppendType) to the browser.
	//Also updates the hasNext, and offset variables for paging. It then rebinds the list elements on click.
	//Add calls loadIfListHasNotOverflown
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
							continue;
						}
						break;
					case itemType.ARTIST:
						p = items[i].track.artists[0];
						if(uriDisplayedSet.has(p.uri)){
							continue;
						}
						break;
					case itemType.TRACK:
						p = items[i];
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
				} else if(itemToAppendType === itemType.ARTIST){
					let matches = uri.match(artistUriRegex);
					let artistId = matches[1];
					artistIdsToFetchThumbnailOf.push(artistId);
				} 
				appendToBrowser(newThumbnailItem(uri, name, image));
				uriDisplayedSet.add(uri);

            }
            if(artistIdsToFetchThumbnailOf.length !== 0){
            	spotifyApi.getArtists(artistIdsToFetchThumbnailOf, {fields: ['uri', 'images', 'name']}, addThumbnails);
            }
		}
		bindListElementsOnClick()
		loadIfListHasNotOverflown();
	}

	//This is for when you dont have the thumbnails and you add that after you display the list.
	function addThumbnails(err, data){
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

	function appendPlaylist(err, data){
		appendItems(err, data, itemType.PLAYLIST);
	}

	function appendAlbum(err, data){
		appendItems(err, data, itemType.ALBUM);
	}

	function appendArtist(err, data){
		appendItems(err, data, itemType.ARTIST);
	}

	function appendTracks(err, data){
		appendItems(err, data, itemType.TRACK);
	}

	function appendSavedTracks(err, data){
    	let items = data.items.map(function(a) { return a.track; });
    	data.items = items;
    	appendTracks(err, data);
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
	        if($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight) {
	            loadMoreItems();
	        }
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
			currentPageType = newPageType;
			currentUri = newUri;

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
		hasNext = true;
		offset = 0;
		let item = data.currentTarget;
		let scrollTop = $("#browseScroll").scrollTop();
		let itemsBeingDisplayed = $browser[0].innerHTML;
		let id = item.id;
		let prevPage = new Page(currentPageType, currentUri, scrollTop, itemsBeingDisplayed);
		prevPageStack.push(prevPage);
		clearBrowser();

		if(albumUriRegex.test(id)){
			currentPageType = pages.SINGLE_ALBUM;
			currentUri = id;
			fetchAppendAlbumTracks();
		}else if(playlistUriRegex.test(id)){
			currentPageType = pages.SINGLE_PLAYLIST;
			currentUri = id;
			fetchAppendPlaylistTracks();
		} else if(artistUriRegex.test(id)){
			currentPageType = pages.SINGLE_ARTIST;
			currentUri = id;
			fetchAppendArtistTracks();
		}else if(id === 'playlists'){
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
		} else if(id === 'songs'){
			currentPageType = pages.SONGS;
			currentUri = null;
			fetchAppendSavedTracks();
		} else if(trackRegex.test(id)){
			console.log("==============YOU JUST CLICKED A TRACK=================");
			
		} else{
			console.log("this id didn't fit what we expected: ", id);
		}
		hideOrShowBackButton();
	}

	//This is the callback for the get recently played tracks 
	//For each of the tracks, it checks if there is a context and if there is it appends it to the
	//recentlyplayeduri list. Then calls addRecentlyPlayedUriToList.
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
	    	}else if(artistUriRegex.test(uri)){
	    		//if the item is an artist
	    		let matches = uri.match(artistUriRegex);
	        	let artistId = matches[1];
	        	//fetch the uri, images, and name of the album and append it to the list.
	        	spotifyApi.getArtist(artistId, {fields: ['uri', 'images', 'name']})
					.then(onRecentlyPlayedContextReturn, function(err) {
						console.error(err);
					});
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

