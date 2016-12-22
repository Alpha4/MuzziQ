var app = angular.module('app',['ngRoute']).config(['$routeProvider','$locationProvider',
    function($routeProvider,$locationProvider){
      $routeProvider
        .when('/home',{
          templateUrl: 'templates/home.html',
          controller: 'menuCtrl',
          controllerAs: 'cont'
        })
        .when('/ongame',{
          templateUrl: 'templates/game.html',
          controller: 'gameCtrl',
          controllerAs: 'gcontrol'
        })
        .when('/highscores',{
        	templateUrl: 'templates/highscores.html',
        	controller: 'highscoresCtrl',
        	controllerAs: 'hsctrl'
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
	  console.log("change of ng-view");
	  $location.path("/home");
  }
  this.$route = $route;
  
  //definition des variables pouvant etre appellés dans les nested controllers(menuCtrl,gameCtrl)
  $scope.questions = null;
  $scope.ind = 0;
  //$scope.isSignedIn = false;
  $scope.isCurrentAnswerCorrect = null;
  
  //fonction appelant gapi.client.muzziqapi pour demander le quizz
  $scope.getQuizz = function(){
	  console.log("getquizz method");
	  var req = gapi.client.muzziqapi.getQuizz().execute(function(response){
		  $scope.questions = response.questions;
		  console.log("created scope.questions");
		  $scope.$apply();
	  });
  };
  
  $scope.signOut = function(){
  		console.log("try sign out");
	  $scope.auth2.signOut().then(function(){
		  console.log("User signed out");
		  console.log("isConnected : "+$scope.auth2.isSignedIn.get());
		  state.isSignedIn = $scope.auth2.isSignedIn.get();
		  player.score = 0;
		  $location.path("/home");
		  $scope.$apply();
	  });
  }
  
  $scope.verifyAnswer = function(id,answer){
	  console.log("inside verifyAnswer method");
	  $scope.isCurrentAnswerCorrect = null;
	  var req = gapi.client.muzziqapi.verifyAnswer({"id":id,"answer":answer}).execute(function(response){
		  console.log("is correct ? :"+response.correct);
		  $scope.isCurrentAnswerCorrect = response.correct;//true or false
		  $scope.$apply();
	  })
  }
  
  $scope.addHighScore = function(score){
	  console.log("inside addHighScore method");
	  gapi.client.muzziqapi.addHighScore({"score":score}).execute(function(response){
		  console.log("call to api function to add HighScore");
	  })
  }
  
  $scope.highScores = null;
  
  $scope.getHighScores = function(){
	  console.log("inside getHighScores method ");
	  gapi.client.muzziqapi.getHighScore().execute(function(response){
		  console.log("call to api function to list highscores");
		  console.log(response);
		  $scope.highScores = response.listHs;
		  $scope.$apply();
	  });
  }
  
  $scope.auth2=null;
  
  
  /*
  $scope.handleSuccess = function(googleUser){
	  console.log("logged in as"+ googleUser.getBasicProfile().getName());
	  //$scope.user = googleUser.getBasicProfile().getName();
	  console.log($scope.auth2.isSignedIn.get());
	  player.name = googleUser.getBasicProfile().getName();
	  player.imageUrl = $scope.auth2.currentUser.get().getBasicProfile().getImageUrl();
	  
	  state.isSignedIn = $scope.auth2.isSignedIn.get();
	  //$location.path("/home");
	  $scope.$apply();
  }
  
  $scope.handleFailure = function(){
	  console.log("handleFailure()");
  }
  */
  
  
  //initialisation de gapi
  window.init = function(){
	  console.log("calling window.init()");
	  var rootApi = "https://muzziq-148913.appspot.com/_ah/api/";
	  gapi.client.load("muzziqapi","v1",function(){
		  console.log("gapi is loaded!");
	  },rootApi);
	  gapi.load('auth2',function(){
		  console.log("in auth2 init()");
		  $scope.auth2 = gapi.auth2.init({
			  client_id: '230619663769-99mc5h263pjsejb4ka8lb9v7ssvtd41r.apps.googleusercontent.com',
			  scope: 'profile'
		  });
		  $scope.$apply();
	  });
  };
}]);


//controlleur pour la page d'accueil (homepage) --- /home ---
app.controller('menuCtrl',['$scope','$location','$route',function($scope,$location,$route){
  this.$route = $route;
  this.player = player;
  this.signOut = $scope.signOut;
  this.isSignedIn = false;
  console.log("inside home controller isSignedIn ="+this.isSignedIn);
  this.signIn = function(){
	  console.log("inside signIn function");
	  var signIn = $scope.auth2.signIn({
		  'scope': 'email'
	  }).then(function(){
		  this.isSignedIn = $scope.auth2.isSignedIn.get();
		  this.player.name = $scope.auth2.currentUser.get().getBasicProfile().getName();
		  this.player.imageUrl = $scope.auth2.currentUser.get().getBasicProfile().getImageUrl();
		  console.log("user is signed in");
	  });
	  this.isSignedIn = $scope.auth2.isSignedIn.get();
  }
  
  var me = this;
  $scope.$watch('auth2',function(newVal,oldVal){
	  if(newVal !== oldVal){
		  me.isSignedIn = $scope.auth2.isSignedIn.get();
		  console.log("inside auth2 watch; signedIn ="+me.isSignedIn);
		  if(me.isSignedIn === true){
			  me.player.name = $scope.auth2.currentUser.get().getBasicProfile().getName();
			  me.player.imageUrl = $scope.auth2.currentUser.get().getBasicProfile().getImageUrl();
			  me.text = "Play Game";
		  }else if(me.isSignedIn === false){
			  me.text = "Log in";
		  }
	  }
  });
  
  console.log("in home controller");
  if($scope.auth2 !== null){
	  this.isSignedIn = $scope.auth2.isSignedIn.get();
	  console.log("not in $scope.$watch but signedIn ="+this.isSignedIn)
	  if(this.isSignedIn === true){
		  console.log("signedIn === true");
		  this.player.name = $scope.auth2.currentUser.get().getBasicProfile().getName();
		  console.log(this.player.name);
		  this.player.imageUrl = $scope.auth2.currentUser.get().getBasicProfile().getImageUrl();
		  this.text = "Play Game";
	  }else if(this.isSignedIn === false){
		  console.log("signedIn === false");
		  this.text = "Log in";
	  }
  }
  
  
  this.play = function(){
    console.log("play() invoked");
    if($scope.auth2.isSignedIn.get() === true){
    	this.player.name = $scope.auth2.currentUser.get().getBasicProfile().getName();
  	  	this.player.imageUrl = $scope.auth2.currentUser.get().getBasicProfile().getImageUrl();
    	$location.path("/ongame");
    }else if($scope.auth2.isSignedIn.get() === false){
    	this.signIn();
    }
  };
  
  this.listHighScores = function(){
	  $location.path("/highscores");
  }
  
}]);



//controlleur pour le deroulement du quizz liée à /onquizz
app.controller('gameCtrl',['$location','$scope','$route',function($location,$scope,$route){
  this.$route = $route;
  this.player = player;
  console.log(this.player);
  this.ind = $scope.ind;
  this.signOut = $scope.signOut;
  this.chosenAnswer = null;
  this.hideValidate = false;
  this.isSignedIn = $scope.auth2.isSignedIn.get();
  console.log("signedIn ? :"+this.isSignedIn);
  this.signIn = $scope.signIn;
  
  //fonction qui modifie le modele en passant à la question suivante
  this.onclick = function(){
	  if(this.ind < $scope.questions.length - 1){
		  console.log("inside onclick() method");
		  //$scope.isCurrentAnswerCorrect = null;
		  console.log("correct = "+$scope.isCurrentAnswerCorrect);
		  this.hideValidate = false;
		  console.log("hide: "+this.hideValidate);
		  this.ind++;
		  this.question = $scope.questions[this.ind].content;
		  this.answers= $scope.questions[this.ind].answers;
		  this.questId = $scope.questions[this.ind].id;
		  console.log("id = "+this.questId);
		  console.log(this.question);
		  console.log(this.ind);
	  }else{
		  $scope.addHighScore(this.player.score);
		  this.player.score = 0;
		  $location.path("/home");
	  }
  }
  
  //fonction qui verifie si la reponse est correcte
  //TODO nu uita sa modifici variabilele la delogare
  this.onclickValidate = function(){
	  console.log("inside onclickValidate method");
	  $scope.verifyAnswer(this.questId,this.chosenAnswer);
	 
  }
  
  var me = this;
  $scope.$watch("isCurrentAnswerCorrect",function(newVal,oldVal){
	  console.log("newVal = "+newVal+" ; oldVal = "+oldVal);
	  if((newVal === true || newVal === false) && oldVal===null){
		  console.log($scope.isCurrentAnswerCorrect);
		  if($scope.isCurrentAnswerCorrect === true){
			  me.player.score += 1;
		  }
		  me.hideValidate = true;
		  console.log("hide1 : "+me.hideValidate);
	  }
  });
  
  if(this.player.name === ''){
	  $location.path("/home");
  }
  
  
  //appel à la methode du api qui retourne le quizz
  $scope.getQuizz();
  
  //laisser "me" car si on utilise this dans $scope.$watch(...) ceci ne correspond pas au controlleur 
  
  $scope.$watch("questions",function(newVal,oldVal){
	  if(newVal !== oldVal){
		  console.log("newVal = "+newVal);
		  console.log("oldVal = "+oldVal);
		  console.log("$scope.questions = "+$scope.questions);
		  console.log("ind = ", me.ind);
		  console.log("me version");
		  me.question = $scope.questions[me.ind].content;
		  me.answers = $scope.questions[me.ind].answers;
		  me.questId = $scope.questions[me.ind].id;
	  }
  });
  
}]);


app.controller("highscoresCtrl", ["$location", "$scope", "$route", function($location,$scope,$route){
	this.isSignedIn = $scope.auth2.isSignedIn.get();
	this.signOut = $scope.signOut;
	this.highScores = $scope.highScores;
	
	$scope.getHighScores();
	var me = this;
	$scope.$watch("highScores",function(newVal,oldVal){
		  if(newVal !== oldVal){
			  console.log("newVal = "+newVal);
			  console.log("oldVal = "+oldVal);
			  console.log("$scope.highScores = "+$scope.highScores);
			  me.highScores = $scope.highScores;
		  }
	  });
}]);




var player={
  name:'',
  score:0,
  imageUrl:''
};

var state={
	isSignedIn:false
}

