$(document).ready(() => {
	var spotifyApi = new SpotifyWebApi();
	spotifyApi.setAccessToken('BQDDrGp_r2cVHIv81vBI2nt6Wr7yF2GZ5cS_kLW-3AFJ7d0ggYPTE8cmpVw1Qj-N4NrRTJ9EZRvZSKP31gFWLLLbP09LyGhSdVZh4eFaYsqkmDp_yfSlc4D1-JXtsOZjd1dhZafLbA3m72L17T8Har1SmEMOaYLE39GM6BSGvUCLtsSqCdhngP6_iIfebg30Iv76sUYjIMAZc8qx-61xZya4yvPPsB-jj8sxGA0RdA5hUXjcbSvrs3IR3Oiw6TsvTqOhPOLwpo07aWbQMvxI_bfHUiPOa_h9xp9sU-82O5MS1OTRuT_MDg');
	spotifyApi.getArtistAlbums('43ZHCT0cAZBISjO8DG9PnE', function(err, data) {
		if (err) console.error(err);
		else console.log('Artist albums', data);
	});
});