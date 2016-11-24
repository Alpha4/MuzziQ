var app = angular.module('app',['ngRoute']).config(['$routeProvider','$locationProvider',
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


//controlleur parent qui possede les methodes pour appeler l'api et des variables de $scope qui se propagent aux autres controlleurs fils
app.controller('mainControler',['$scope','$route','$location','$window',function($scope,$route,$location){
  
  //à l'acces au site(ex : muzziq.appspot.com ), angular va appeler la premiere vue( celle qui a menuCtrl comme controlleur)
  console.log($location.url());
  if($location.url() == ""){
	  $location.path("/main");
  }
  this.$route = $route;
  
  //definition des variables pouvant etre appellés dans les nested controllers(menuCtrl,gameCtrl)
  $scope.questions = null;
  $scope.ind = 0;
  
  //fonction appelant gapi.client.muzziqapi pour demander le quizz
  $scope.getQuizz = function(genre){
	  console.log("getquizz method");
	  var req = gapi.client.muzziqapi.getQuizz({"Genre" : genre}).execute(function(response){
		  $scope.questions = response.questions;
		  console.log("created scope.questions");
		  $scope.$apply();
	  });
  };
  
  //initialisation de gapi
  window.init = function(){
	  console.log("calling window.init()")
	  var rootApi = "https://coral-147014.appspot.com/_ah/api/";
	  gapi.client.load("muzziqapi","v1",function(){
		  console.log("gapi is loaded!");
	  },rootApi);
  };
}]);


//controlleur pour la page d'acqueil (homepage)
app.controller('menuCtrl',['$scope','$location','$route',function($scope,$location,$route){
  this.$route = $route;
  this.player = player;
  this.genres = genres;
  console.log(this.genres)
  this.play = function(){
    console.log("play() invoked");
    $location.path("/ongame");
  };
}]);



//controlleur pour le deroulement du quizz liée à /onquizz
app.controller('gameCtrl',['$scope','$route','$timeout',function($scope,$route,$timeout){
  this.$route = $route;
  this.player = player;
  console.log(this.player);
  this.ind = $scope.ind;
  
  
  //fonction qui modifie le modele en passant à la question suivante
  //TODO a finir 
  this.onclick = function(){
	  if(this.ind < $scope.questions.length - 1){
		  console.log("inside onclick() method");
		  this.ind++;
		  this.question = $scope.questions[this.ind].content;
		  this.answers= $scope.questions[this.ind].answers;
		  console.log(this.question);
		  console.log(this.ind);
	  }
  }
  
  //appel à la methode du api qui retourne le quizz
  $scope.getQuizz(this.player.quizzGenre);
  
  //verification que le quizz est bien retourné avant de l'aficher
  //laisser "me" car si on utilise this dans $scope.$watch(...) ceci ne correspond pas au controlleur 
  var me = this;
  $scope.$watch("questions",function(newVal,oldVal){
	  if(newVal !== oldVal){
		  console.log("newVal = "+newVal);
		  console.log("oldVal = "+oldVal);
		  console.log("$scope.questions = "+$scope.questions);
		  console.log("ind = ", me.ind);
		  console.log("me version");
		  me.question = $scope.questions[me.ind].content;
		  me.answers = $scope.questions[me.ind].answers;
	  }
  });
  
}]);

var player={
  name:'',
  score:0,
  quizzGenre:''
};

//TODO ajouter les autres genres 
var genres = ["Rock","Instrumental"];
