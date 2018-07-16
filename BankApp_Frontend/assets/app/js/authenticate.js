var emailDirectory = "/api/email/";

function sendEmail(callback) {
	let result = $.ajax({
        type: 'GET',
        url: apiUrl + userDirectory,
        data: {
            userID: sessionStorage.userID,
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