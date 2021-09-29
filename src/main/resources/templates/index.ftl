<#-- @ftlvariable name="session" type="net.lucypoulton.pastebin.plugins.UserSession" -->
<!DOCTYPE html>
<html lang="en">
<body>
<#if session?has_content>
    <h1>Logged in as ${session.username}</h1>
<#else>
    <h1>Not logged in</h1>
</#if>
</body>
</html>