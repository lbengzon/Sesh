<#assign content>

<div id="window">

<script>
userId = "${userId}";

$(document).ready(function() {
    const params = {userId: userId};
    post("/createjoin", params, "get");
});

</script>

</div>

</#assign>
<#include "main.ftl">