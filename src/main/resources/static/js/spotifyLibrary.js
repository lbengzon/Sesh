$(document).ready(() => {
	let $browser = $("#browse");
	let spotifyApi = new SpotifyWebApi();
	let playlistUriRegex = new RegExp('spotify:user:([^:]+):playlist:([0-9A-Za-z]{22})');
	let albumUriRegex = new RegExp('spotify:album:([0-9A-Za-z]{22})');
	let recentlyPlayedUri = [];
	let recentlyPlayedLimit = 50;
	let playlistLimit = 5;
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
	let currentPage;


	spotifyApi.setAccessToken('BQDwu3Z4in0ScTou-pEgUONoTejQ3Yond9_DEhSmApd8_qHFwr2l9Qai4mtc1EGybJod-1knNoIqUDxzhTeadaApHzwqhuATnJzO_Vxa4CKYd-ejVufgiT6pEmDcUbUaA9ROWg8idsDUB4HFzYvsov9-U-Z0rW2eU2bpumirZu5s-OsngNKXQ8I89wWNPqO5d1__AKBE0fEo1b-a3YYZzC4xFEpvLgwHJeeixnvUigK3i3GqaWE1KvD0jWsyOvdDVqdJNG9y4buGX0MAoBbamXNf4-qrZYXwlEluAEuN2eeccS2yd115buEwc3J3UiH5HI13KZlsYKMbWPV6kZfx_EJeTg');
	
	createHomePage();



	//==============================================FUNCTIONS======================================================

	function createHomePage(){
		currengPage = pages.HOME;
		$browser.empty();
		$browser.append(newListItem("playlists", "Playlists"));
		$browser.append(newListItem("songs", "Songs"));
		$browser.append(newListItem("albums", "Albums"));
		$browser.append(newListItem("artists", "Artists"));
		$browser.append('<h4 class=\"spotifyBrowserInListLabel\">Recently Played</h4>')
		spotifyApi.getMyRecentlyPlayedTracks({limit: recentlyPlayedLimit}, populateRecentlyPlayedContext);
	}

	function fetchAppendPlaylists(){
		removeLoadButton();
		spotifyApi.getUserPlaylists({limit: playlistLimit, offset: offset, fields: ['uri', 'images', 'name']}, appendPlaylists);
	}

	function addLoadButton(){
		$browser.append('<button type=\"button\" id=\"loadButton\">Load More</button>');
		$("#loadButton").click(loadMoreItems);
	}

	function removeLoadButton(){
		$("#loadButton").remove();
	}

	function loadMoreItems(){
		switch(currentPage){
			case pages.PLAYLISTS:
				fetchAppendPlaylists();
				break;	
		}
	}



	function appendPlaylists(err, data){
		if (err) console.error(err);
		else {
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
				let p = items[i];
				console.log(p);
				let image = p.images[0].url;
				let name = p.name;
				let uri = p.uri;
				$browser.append(newPlaylistItem(uri, name, image));
            }
		}
		if(hasNext){
			addLoadButton();
		}
	}

	function bindListElementsOnClick(){
		$("#browse li").click(listElementClickHandler);
	}

	function listElementClickHandler(data){
		$browser.empty();
		hasNext = true;
		offset = 0;
		let item = data.currentTarget
		let id = item.id
		if(albumUriRegex.test(id)){

		}else if(playlistUriRegex.test(id)){

		} else if(id === 'playlists'){
			console.log('fetching playlists')
			currentPage = pages.PLAYLISTS;
			fetchAppendPlaylists();
		}
	}

	function populateWithAlbumView(albumId){
		
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
		let count = recentlyPlayedUri.length;
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
					.then(function(data) {
						let image = data.images[0].url;
						let name = data.name;
						let uri = data.uri;
						$browser.append(newPlaylistItem(uri, name, image));
						count--;
						if(count == 0){
							bindListElementsOnClick();
						}
					}, function(err) {
						console.error(err);
					});
	    	} else if(albumUriRegex.test(uri)){
	    		//if the item is an album
	    		let matches = uri.match(albumUriRegex);
	        	let albumId = matches[1];
	        	//fetch the uri, images, and name of the album and append it to the list.
	        	spotifyApi.getAlbum(albumId, {fields: ['uri', 'images', 'name']})
					.then(function(data) {
						let image = data.images[0].url;
						let name = data.name;
						let uri = data.uri;
						$browser.append(newAlbumItem(uri, name, image));
						count--;
						if(count == 0){
							bindListElementsOnClick();
						}
					}, function(err) {
						console.error(err);
					});
	    	}
		}
	}



	function newPlaylistItem(uri, title, imageSource){
		return "<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
                    + "id=\"" + uri + "\" class=\"listItemPlaylist\">"
                    + "<img class=\"spotifyBrowsingThumbnail\" src=\"" + imageSource + "\"></img>"
                    + "<div id=\"songtitle\">" + title 
                    //end of song title div
                    + "</div>"
                    + "</li>"; 
	}

	function newAlbumItem(uri, title, imageSource){
		return "<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
                    + "id=\"" + uri + "\" class=\"listItemAlbum\">"
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

