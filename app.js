var app = angular.module('app',['ngRoute','ngStorage']).config(['$routeProvider','$locationProvider',
    function($routeProvider,$locationProvider){
      $routeProvider
        .when('/main',{
          templateUrl: 'menu.html',
          controller: 'menuCtrl',
          controllerAs: 'cont'
        })
        .when('/ongame',{
          templateUrl: 'game.html',
          controller: 'gameCtrl',
          controllerAs: 'gcontrol'
        });
}]);

function init(){
  window.init();
}




/*TODO complete the functions calling our api by replacing name_api, name_function and implementing execute() and
add other necessary functions */

app.controller('questionsControler',['$scope','$window',function($scope,$windwow){

  $scope.listHighScores = function(){
	  gapi.client.name_api.name_function().execute(function(response){
		  console.log(response);
	  });
  };

  $scope.listHighScoresByName = function(){
    gapi.client.name_api.name_function().execute(function(response){
      console.log(response);
    });
  };
  



//TODO replace with our api

  window.init = function(){
    console.log("window.init() called");
    var rootApi = "https://address-of-our-api/_ah/api/";
    gapi.client.load("name_api","v1",function(){
      console.log("gapi is loaded");
    },rootApi);
  }
}]);



app.controller('menuCtrl',['$scope','$localStorage','$route',function($scope,$localStorage,$route){
  this.$route = $route;
  this.player = player;
  this.test = 'aplicatia merge bine!';
  console.log('functioneaza!');
  this.play = function(){
    $localStorage.player = this.player;
    console.log("play() invoked");
  };
}]);

app.controller('gameCtrl',['$scope','$localStorage','$route',function($scope,$localStorage,$route){
  this.$route = $route;
  console.log($localStorage.player);
  this.player = $localStorage.player;
  this.ind = 0;
  this.question = $scope.listHighScores();
}]);

var player={
  name:'',
  score:0
}
