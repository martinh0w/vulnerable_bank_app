var userDirectory = "/api/user/";

function getUser(userID, callback) {
	let result = $.ajax({
        type: 'GET',
        url: apiUrl + userDirectory + "get",
        data: {
            userID: userID,
        },
        success: function(data) {
        	return callback(data);
        },
        error: function(data) {
        	$('#errorMsg').html(data.responseJSON.message);
            $('#errorMsg').show();
        }
    });
}

function addNewUser(callback) {
    var userID = document.getElementById("reg-userid").value;
    var PIN = document.getElementById("reg-pin").value;
    var fullName = document.getElementById("reg-fullName").value.toUpperCase();
    var email = document.getElementById("reg-email").value;
    var mobile = document.getElementById("reg-mobile").value;
    var profilePicture = document.getElementById("profile-picture").files[0];

    //stores all values into a form data 
    //must be used due to file upload
    var formdata = new FormData();
    formdata.append('userID', userID);
    formdata.append('PIN', PIN);
    formdata.append('fullName', fullName);
    formdata.append('email', email);
    formdata.append('mobile', mobile);
    formdata.append('profilePicture', profilePicture);

    let result = $.ajax({
        type: 'POST',
        url: apiUrl + userDirectory + "add",
        data: formdata,
        processData: false,
        contentType: false,
        success: function(data) {
            //show register successful modal upon adding new user
            $('#registerModal').modal('show');
            $('#login-form').show();
            $('#register-form').hide();
        },
        error: function(data) {
            return callback(data.responseJSON.message);
        }
    });
}

function getProfilePicture(callback) {
    let result = $.ajax({
        type: 'GET',
        url: apiUrl + userDirectory + "image",
        data: {
            userID: sessionStorage.userID
        },
        success: function(data) {
            return callback(data);
        },
        error: function(data) {
            $('#errorMsg').html(data.responseJSON.message);
            $('#errorMsg').show();
        }
    });
}

function updateUser() {
    var userID = sessionStorage.userID;
    var PIN = document.getElementById("pin").value;
    var fullName = document.getElementById("fullName").value.toUpperCase();
    var email = document.getElementById("email").value;
    var mobile = document.getElementById("mobile").value;
    var profilePicture = document.getElementById("profile-picture").files[0];

    //stores all values into a form data 
    //must be used due to file upload
    var formdata = new FormData();
    formdata.append('userID', userID);
    formdata.append('PIN', PIN);
    formdata.append('fullName', fullName);
    formdata.append('email', email);
    formdata.append('mobile', mobile);
    if (profilePicture != undefined) {
        formdata.append('profilePicture', profilePicture);
    }

    let result = $.ajax({
        type: 'POST',
        url: apiUrl + userDirectory + "update",
        data: formdata,
        processData: false,
        contentType: false,
        success: function(data) {
            window.location.replace(domainUrl + "/user/profile.html");
        },
        error: function(data) {
            $('#errorMsg').html(data.responseJSON.message);
            $('#errorMsg').show();
        }
    });
}

function authenticateUser(callback) {
    var OTP = document.getElementById("otp").value;
    console.log(OTP);

    let result = $.ajax({
        type: 'POST',
        url: apiUrl + userDirectory + "authenticate",
        data: {
            passCode: OTP,
        },
        success: function(data) {
            return callback(data);
        },
        error: function(data) {
            $('#errorMsg').html(data.responseJSON.message);
            $('#errorMsg').show();
        }
    });
}