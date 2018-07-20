var userDirectory = "/api/user/";

function login() {    
    var userID = document.getElementById("userid").value;
    var PIN = document.getElementById("pin").value;


    if(userID.length == 0) {
        return {errorMsg: "User ID cannot be blank"};
    }
    if (PIN.length == 0) {
        return {errorMsg: "PIN cannot be blank"};
    }
    
    let result = $.ajax({
        type: 'POST',
        url: apiUrl + userDirectory + "login",
        data: {
            userID: userID,
            PIN: PIN,
        },
        error: function(data) {
            $('#errorMsg').html(data.responseJSON.message);
            $('#errorMsg').show();
        },
        success: function() {
            sessionStorage.userID = userID;

            //used to check if session expires
            sessionStorage.timestamp = new Date().getTime();
            window.location.replace(domainUrl + "/user/homepage.html");
        }
    });
}
       
  
 