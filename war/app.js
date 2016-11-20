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




app.controller('questionsControler',['$scope','$window',function($scope,$windwow){

  $scope.getQuizz = function(){
	  gapi.client.muzziqapi.getQuizz().execute(function(response){
		  console.log(response);
		  return response;
	  });
  };


  window.init = function(){
    console.log("window.init() called");
    var rootApi = "https://coral-147014.appspot.com/_ah/api/";
    gapi.client.load("muzziqapi","v1",function(){
      console.log("gapi is loaded");
      $scope.getQuizz();
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
  //this.quizz = $scope.getQuizz();
}]);

var player={
  name:'',
  score:0
}
