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
	let trackMap = new Map();




	spotifyApi.setAccessToken('BQAC7UwpT6LFweyBp9AACS_3EcZRD7oFmmsvXZfjvsEnz_mGKhhi1ydZUZsFIjZd_uD8nbqZLeoKTKimSOxeW-B8StrxAdcXF7FB-UYXkp5FFgxK-DtYqrFAY5SwcDzGTBzJ9xJ_CBhFPWWc_a-qBqYg-PnEWipFoVwqr-mDHF8s5UMaYzDjh-wmwj608QqYPRElzWpb01JjM2FUvoEBZIO3XK6RoBuXpzLymgil98cQf7uaaRppcNnH2tHQN8bSr2IWXGmRGD8_00w7NmSIo1IchmMEKS9xyOA1EcNwKkpCZcR5EdS5zCa3aN4dNkoi8-5BIOIGYm2MeD5km5ahBMvAGA');
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
		itemsBeingDisplayed.push(element[0].outerHTML);
	}

	function clearBrowser(){
		$browser.empty();
		itemsBeingDisplayed = [];
		console.log("CLEAR");
		uriDisplayedSet = new Set();
		allTracks = [];
	}

	function fetchAppendPlaylists(){
		removeLoadButton();
		spotifyApi.getUserPlaylists({limit: playlistLimit, offset: offset, fields: ['uri', 'images', 'name']}, appendPlaylist);
	}

	function fetchAppendPlaylistTracks(){
		removeLoadButton();
		let matches = currentUri.match(playlistUriRegex);
	    let userId = matches[1];
	    let playlistId = matches[2];
	    spotifyApi.getPlaylistTracks(userId, playlistId, {limit: playlistTracksLimit, offset: offset}, appendSavedTracks);
	}

	function fetchAppendAlbumTracks(){
		removeLoadButton();
		let matches = currentUri.match(albumUriRegex);
		let albumId = matches[1];
		spotifyApi.getAlbumTracks(albumId, {limit: albumTracksLimit, offset: offset}, appendTracks);
	}

	function fetchAppendSavedTracks(){
		removeLoadButton();
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

	function appendTrackIfSaved(data, albumTrackIdsUnique, trackMap){
		removeLoadButton();

		for(let i in data){
			let isSaved = data[i];
			if(isSaved){
				let trackId = albumTrackIdsUnique[i];
				let track = trackMap.get(trackId);
				appendToBrowser(newTrackListItem(trackId, track.name))
			}
		}
		addLoadButton();
		bindListElementsOnClick();
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
			$("#loadButton").on('click', loadMoreItems);
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
		addLoadButton();
		bindListElementsOnClick()
	}

	//This is for when you dont have the thumbnails and you add that after you display the list.
	function addThumbnails(err, data){
		let artists = data.artists;
		for(let i in artists){
			let artist = artists[i];
			let uri = artist.uri;
			let $listElement  = $(document.getElementById(uri));
			console.log("itemsBeingDisplayed", itemsBeingDisplayed);

			if(artist.images[0] != undefined){
				let index = itemsBeingDisplayed.indexOf($listElement[0].outerHTML);
				console.log("index", index);
				console.log("listElement ", $listElement[0].outerHTML);
				let image = artist.images[0].url;
				let thumbnail = $listElement.children('img');
				thumbnail.attr('src', image);
				itemsBeingDisplayed[index] = $(document.getElementById(uri))[0];
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

	function bindListElementsOnClick(){
		$("#browse li").off('click', listElementClickHandler);
		$("#browse li").on('click', listElementClickHandler);
	}

	function bindBackButtonOnClick(){
		$("#back").on('click', backButtonClickHandler);
	}

	function bindScrollLoading(){

		$("#browseScroll").on('scroll', function() {
	        if($(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight) {
	            //alert('end reached');
	        }
	    });
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

	function newTrackListItem(id, title){
		return "<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
                    + "id=\"" + id + "\" class=\"listItem\">"
                    + "<div id=\"songtitle\">" + title 
                    //end of song title div
                    + "</div>"
                    + "</li>"; 
	}
});

