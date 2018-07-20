
function protect() {
	//redirect to index.html if session not found
	if (sessionStorage.userID == null) {
		window.location.replace(domainUrl);
	}
	//get the time now
	var now = new Date().getTime();

	//set time in hours till session expires
	var hours = 0.167;

	//checking for user session timeout
	if(now - sessionStorage.timestamp > hours*60*60*1000) {
		sessionStorage.clear();
	 	window.location.replace(domainUrl);
	}

	

}

function newOTP() {
	sessionStorage.OTP = new Date().getTime();
}


function protectOTP() {
	//get the time now
	var now = new Date().getTime();

	//set time in hours till session expires
	var hours = 0.167;

	//check if user is authenticated 
	if(sessionStorage.OTP == null) {
		window.location.replace(domainUrl + "/authentication/OTP.html");
	}

	//check if otp is expired
	if(now - sessionStorage.OTP > hours*60*60*1000) {
		sessionStorage.removeItem("OTP");
	 	window.location.replace(domainUrl + "/authentication/OTP.html");
	}
}