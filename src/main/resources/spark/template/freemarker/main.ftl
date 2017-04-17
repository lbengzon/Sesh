<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <title>${title}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- In real-world webapps, css is usually minified and
       concatenated. Here, separate normalize from our code, and
       avoid minification for clarity. -->
    <link rel="stylesheet" href="../css/normalize.css">
    <link rel="stylesheet" href="../css/sesh.css">

    <script src="../js/jquery-2.1.1.js"></script>
    <script src="../js/sesh.js"></script>

	<link href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300" rel="stylesheet">

    <!-- inline CSS to make scrollbars prettier -->
    <style media="screen" type="text/css">
        ::-webkit-scrollbar {
            width: 12px;
        }

        ::-webkit-scrollbar-track {
            -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
            -webkit-border-radius: 10px;
            border-radius: 10px;
        }

        ::-webkit-scrollbar-thumb {
            -webkit-border-radius: 10px;
            border-radius: 10px;
            background: rgba(0, 0, 0, 0.8);
            -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.5);
        }

        ::-webkit-scrollbar-thumb:window-inactive {
            background: rgba(0, 0, 0, 0.5);
        }

    </style>
</head>
<body>
<div id="header">
	<h1>sesh</h1>
</div>
${content}
</body>
<!-- See http://html5boilerplate.com/ for a good place to start
     dealing with real world issues like old browsers.  -->
</html>
