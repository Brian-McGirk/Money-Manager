gapi.load('auth2', function(){
      gapi.auth2.init();
});

function onSignIn(googleUser) {
  var profile = googleUser.getBasicProfile();

//  TODO finish implementing google sign in
    $(.g-signin2).css("display","none");
    $("#pic").attr('src',profile.getImageUrl());
    $("#email").text(profile.getEmail());

}

function signOut()
{
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function(){

        alert("You have been signed out!")

    });
}