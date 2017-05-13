$(document).ready(() => {
	let $browser = $("#browse");
	let spotifyApi = new SpotifyWebApi();
	let playlistUriRegex = new RegExp('spotify:user:([^:]+):playlist:([0-9A-Za-z]{22})');

	spotifyApi.setAccessToken('BQA8fQ0A3fBClOuz_Cign1cFajKNGNOh89vmT7c2cUGFzlz9yEX6Nrl4LouwKgIIVR158FiWUMjjYwwh8epHDvetdjIXfJGwdwfHx2aSqQ8YvNelseOhSdHYwjLBN7B9szoJXoMenc-htcn2hMH46newdei_w3d6mlkNSYizgEwyYI0gjL_CNuOEn3OnQE9oPuM9Q6mKyvk32bAhIAKseM_axiha6HwGTLwzci0L7bcUj_c3X7pfpD6IOGwWKStXg2TfT686X5nHKQWMHwDtdj_f_tdt1D2hu_uRRGvdsAgJGVJwD23vX7kKGiqm1x3ImgrFq1zriDu3M-Nh_hH8mQrEGQ');
	
	createHomePage();

	function createHomePage(){
		$browser.empty();
		$browser.append(newListItem("playlists", "Playlists"));
		$browser.append(newListItem("songs", "Songs"));
		$browser.append(newListItem("albums", "Albums"));
		$browser.append(newListItem("artists", "Artists"));
		$browser.append('<h4 class=\"spotifyBrowserInListLabel\">Recently Played</h4>')
		spotifyApi.getMyRecentlyPlayedTracks({}, populateRecentlyPlayedContext);
	}

	function populateRecentlyPlayedContext(err, data){
		let recentlyPlayedContexts = [];
		if (err) console.error(err);
		else {
			console.log('Recently Played Tracks', data);
			let items = data.items;
			for (let i in items) {
                let context = items[i].context;
                if(context != undefined){
                	if(context.type === "playlist"){
	                	let matches = context.uri.match(playlistUriRegex);
	                	let userId = matches[1];
	                	let playlistId = matches[2];

	                	spotifyApi.getPlaylist(userId, playlistId, {fields: ['uri', 'images', 'name']})
							.then(function(data) {
								let image = data.images[0].url;
								let name = data.name;
								let uri = data.uri;
								$browser.append(newPlaylistItem(uri, name, image));
								console.log('User playlist', data);
							}, function(err) {
								console.error(err);
							});
                	}
                }
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

	function newListItem(id, title){
		return "<li onmouseover=\"hoverOn(this)\" onmouseout=\"hoverOff(this)\" "
                    + "id=\"" + id + "\" class=\"listItem\">"
                    // + "<div class=\"fav\" >"
                    //     + "<button class=\"favButton\" id=\"" + suggestions[i].spotifyId + "\" type=\"button\"> " 
                    //       + "<i id=\"ifav\" class=\"material-icons\">grade</i>"
                    //     + "</button>"
                    //   //end of fav div
                    //   + "</div>"
                    + "<div id=\"songtitle\">" + title 
                    //end of song title div
                    + "</div>"
                    + "</li>"; 
	}
});

