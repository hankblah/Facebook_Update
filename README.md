# Cralwer + Automatic Facebook Fanpage Posting
Crawled a bookstore in Taiwan and republish the data on facebook for book reselling in Hong Kong.



<h3><a id="user-content-how-does-the-utensor-workflow-work" class="anchor" aria-hidden="true" href="#how-does-the-utensor-workflow-work"><svg class="octicon octicon-link" viewBox="0 0 16 16" version="1.1" width="16" height="16" aria-hidden="true"><path fill-rule="evenodd" d="M7.775 3.275a.75.75 0 001.06 1.06l1.25-1.25a2 2 0 112.83 2.83l-2.5 2.5a2 2 0 01-2.83 0 .75.75 0 00-1.06 1.06 3.5 3.5 0 004.95 0l2.5-2.5a3.5 3.5 0 00-4.95-4.95l-1.25 1.25zm-4.69 9.64a2 2 0 010-2.83l2.5-2.5a2 2 0 012.83 0 .75.75 0 001.06-1.06 3.5 3.5 0 00-4.95 0l-2.5 2.5a3.5 3.5 0 004.95 4.95l1.25-1.25a.75.75 0 00-1.06-1.06l-1.25 1.25a2 2 0 01-2.83 0z"></path></svg></a>How does it work?</h3>

<div><a target="_blank" rel="noopener noreferrer" href="https://github.com/hankblah/Facebook_Update/blob/master/2.png"><img src="https://github.com/hankblah/Facebook_Update/blob/master/2.png" width="1500" align="center/" style="max-width:100%;"></a></div>

<h3>What you will need:</h3>
<h2>Access Tokens</h2>
<p>Access tokens allow your app to access the Graph API. They typically perform two functions:</p>

<ul>
<li>They allow your app to access a User&#039;s information without requiring the User&#039;s password.</li>
<li>They allow us to identify your app, the User who is using your app, and the type of data the User has permitted your app to access.</li>
</ul>

<p>Almost all Graph API endpoints require an access token of some kind, so each time you access an endpoint, your request may require  one. Check endpoint <a href="/docs/graph-api/reference">references</a> for token requirements.</p>
<h3>How Tokens Work</h3>

<p>Access tokens conform to the <a href="https://l.facebook.com/l.php?u=https%3A%2F%2Foauth.net%2F2%2F%3Ffbclid%3DIwAR2nH4Y7xJhGQ15XdIcaZDyFNl7ljinSHBho-AoUYEbAHlwYfLfp-ukzAKM&amp;h=AT2XWLNJI7p6Q3ZeqtRYe4mklDAEV7dV-IXyHcw0HNYxdsMYErVXjSSEdlkQOiABtO8uG4lT57_QUJOZLOEXDBuVcBlATeJok6GQhCVjZ65qvSgEnGvNrFrVZhz9LpXBeArOoGwgJ48g7NtoHV8" target="_blank" rel="noopener nofollow" data-lynx-mode="asynclazy">OAuth 2.0</a> protocol. OAuth 2.0 allows entities such as a User or a Page to authorize tokens. Usually this is done through a web interface. Once authorized, apps can use those tokens to access specific information.</p>

<p>For example, this app is asking a User to give it permission to access the User&#039;s photos, videos, and email address:</p>
<div style="text-align:center;margin-bottom:20px;margin-top:20px;"><img class="img" src="https://scontent.ftpe8-2.fna.fbcdn.net/v/t39.2365-6/26804238_178306542936244_8856597188579426304_n.png?_nc_cat=100&amp;ccb=1-3&amp;_nc_sid=ad8a9d&amp;_nc_ohc=cK-l4jKoKoEAX--nstA&amp;_nc_ht=scontent.ftpe8-2.fna&amp;oh=144dd8703dfe64a4b678d4bbc28525d7&amp;oe=607495CA" width="450" alt="" /></div><p>As you can see, this is a Facebook interface. The User has just used the interface to sign into their account, which has allowed us to authenticate the User. If the User continues, we exchange the old token (an App token) for a new one (a User token). The app can then use the new User token to make Graph API requests, but can only access that specific User&#039;s photos, videos, and email address.</p>

<p>This is an important attribute of access tokens. The app and User IDs are both encoded in the token itself (among other things), and we use those IDs to keep track of which data the User has permitted the app to access. For example, if you inspected the token after the User granted permission, it would reveal this information:</p>
<div style="text-align:center;margin-bottom:20px;margin-top:20px;"><img class="img" src="https://scontent.ftpe8-1.fna.fbcdn.net/v/t39.2365-6/26906860_324549194716404_2236494083545628672_n.png?_nc_cat=105&amp;ccb=1-3&amp;_nc_sid=ad8a9d&amp;_nc_ohc=qWFoPhXkDfcAX8IZ_j_&amp;_nc_ht=scontent.ftpe8-1.fna&amp;oh=ba74fa1dcda75f2f04b1a0892e9d590b&amp;oe=6075B7E7" alt="" /></div><p>Since tokens permit access to a User&#039;s data, and since they can be used by anyone, they are extremely valuable, so <strong>take precautions when using them in your queries</strong>. The easiest way to do this is to use Facebook Login to handle your tokens.</p>


<!--<pre><code>
1561456
</code></pre>
-->
