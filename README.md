# Introduction

# Backend installation. 

# Frontend installation

We will use yeoman. Assuming that yeoman is installed type the following:

	> yo angular

add the twitter bootstrap and select the defaults. After yeoman finishes installing all the dependencies we need to add a couple of thigs more.
Since Angular 1.2, the routes are now an extra dependency. for that we need to download the `angular-routes.js` and install the ngRoutes dependency into
our module. 

	> bower install angular-route

edit the index.html to include the dependency after angular.js

	index.html:
	<script src="bower_components/angular/angular.js"></script>
  	<script src="bower_components/angular-route/angular-route.js"></script>

and edit app.js to add the dependency in the module

	app.js
	angular.module('app', ['ngRoute'])
	
now type `grunt server` to load the application. if the browser doesn't start open the browser to [http://localhost:9000](http://localhost:9000)

## Configuring html5 mode

To avoid the hashbang (#) in your urls you have to configure the angularjs with html5 mode. Doing so though will work as long as you are in clicking the 
links from the main page but it will not work when the users are copy pasting the links or bookmarking them. for that we need to configure the URL-rewriting

first lets add route. We will create a welcome route, controller and view. 

	> yo angular:route welcome
	
This will create the `controllers/welcome.js` file with the `WelcomeCtrl`, the `views/welcome.html` file and will configure a route on the `/welcome` url.
Also it will add this dependency into index.html. 

lets add a link into index.html to navigate to this new page:

	<h3>Enjoy coding! - Yeoman</h3>

  	<a href="#/welcome">Welcome message</a>

Now cliking on the link it will redirect to the [http://localhost:9000/#/welcome](http://localhost:9000/#/welcome) page. 
We want to change that so that it redirects to the [http://localhost:9000/welcome](http://localhost:9000/welcome) page instead
Edit `app.js` and add the `$locationProvider`. The full `app.js` is now:

	angular.module('app', ['ngRoute'])
		.config(function ($routeProvider, $locationProvider) {
			$routeProvider
				.when('/', {
					templateUrl: 'views/main.html',
					controller: 'MainCtrl'
				})
				.when('/welcome', {
					templateUrl: 'views/welcome.html',
					controller: 'WelcomeCtrl'
				})
				.otherwise({
					redirectTo: '/'
				});

			$locationProvider.html5Mode(true);
		});

We added the locationProvider and enabled the html5Mode. Now if you reload the page and you click on the welcome link, the url is 
now [http://localhost:9000/welcome](http://localhost:9000/welcome). The problem is though that if we type the url directly then we get a 404 error. 

To fix that we need to tell the server to redirect all requests through the index page. That way angular will pick up and load the correct route. 

First we need to install the `connect-modrewrite` plugin. 

	> grunt install connect-modrewrite

Now we need to configure it. Open GruntFile.js on the root of the project and locate the livereload configuration:

	connect: {
	...
		livereload: {
			options: {
				middleware: function (connect) {
					return [
						lrSnippet,
						mountFolder(connect, '.tmp'),
						mountFolder(connect, yeomanConfig.app)
					];
				}
			}
		},

change it to:

	livereload: {
		options: {
			middleware: function (connect) {
				return [
					lrSnippet,
					modRewrite([
						'!\\.html|\\.js|\\.css|\\.swf|\\.jp(e?)g|\\.png|\\.gif$ /index.html'
					]),
					mountFolder(connect, '.tmp'),
					mountFolder(connect, yeomanConfig.app)
				];
			}
		}
	},
	
Now go to the top of the file (outside the module.exports) and register the plugin:

	var modRewrite = require('connect-modrewrite');

## Configure Reverse Proxy. 
Now that we have the navigation configured. there is another problem we have to fix. The application needs to communicate with the backend but the backend is on a different domain. 
For that we need to setup a reverse proxy. First we need to install the `grunt-connect-proxy` plugin

	> npm install grunt-connect-proxy
	
Now we need to configure the plugin. 

On the top of the `GruntFile.js` add the following line (outside module.exports)

	var proxySnippet = require('grunt-connect-proxy/lib/utils').proxyRequest;
	
Then load the task:

	module.exports = function (grunt) {
		require('load-grunt-tasks')(grunt);
		require('time-grunt')(grunt);

		grunt.loadNpmTasks('grunt-connect-proxy');
	
And configure it:

	connect: {
		options: {
			port: 9000,
			// Change this to '0.0.0.0' to access the server from outside.
			hostname: 'localhost'
		},
		proxies: [
			{
				context: '/rest',
				host: 'localhost',
				port: '8080'
			}
		],
		livereload: {
		....
		}

In this example the server is under [http://localhost:8080/rest](http://localhost:8080/rest), so we mapped it to the proxies entry.

Finally you need to register the task. search for `grunt.registerTask('server'...`

	grunt.registerTask('server', function (target) {
		if (target === 'dist') {
			return grunt.task.run(['build', 'open', 'connect:dist:keepalive']);
		}

		grunt.task.run([
			'clean:server',
			'concurrent:server',
			'autoprefixer',
			'configureProxies',
			'connect:livereload',
			'open',
			'watch'
		]);
	});
	
You need to add the `configureProxies` before the `connect:livereload`
