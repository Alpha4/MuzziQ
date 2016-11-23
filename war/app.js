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
  console.log("inside init()");
  window.init();
}


app.controller('mainControler',['$scope','$route','$location','$window',function($scope,$route,$location){
  console.log($location.url());
  if($location.url() == ""){
	  $location.path("/main");
  }
  this.$route = $route;
  
  console.log("app controller");
  
  
  $scope.getQuizz = function(genre){
	  console.log("getquizz method");
	  var req = gapi.client.muzziqapi.getQuizz({"Genre" : genre});
	  var resp = req.execute(function(response){
		  console.log(response);
		  $scope.questions = response.questions;
	  });
	  console.log($scope.questions);
  };
  
  
  window.init = function(){
	  console.log("calling window.init()")
	  var rootApi = "https://coral-147014.appspot.com/_ah/api/";
	  gapi.client.load("muzziqapi","v1",function(){
		  console.log("gapi is loaded!");
	  },rootApi);
  };
}]);



app.controller('menuCtrl',['$scope','$location','$route',function($scope,$location,$route){
  this.$route = $route;
  this.player = player;
  this.test = 'aplicatia merge bine!';
  console.log('functioneaza!');
  this.genre = "Rock";
  this.play = function(){
    //$localStorage.player = this.player;
    console.log("play() invoked");
    $location.path("/ongame");
  };
}]);



app.controller('gameCtrl',['$scope','$route',function($scope,$route){
  this.$route = $route;
  console.log("game controller");
  this.player = player;
  console.log(this.player);
  this.ind = 0;
  
  $scope.getQuizz("Rock");
  
}]);

var player={
  name:'',
  score:0
}
