var emailDirectory = "/api/email/";

function emailUser() {
	let result = $.ajax({
        type: 'POST',
        url: apiUrl + emailDirectory + "send",
        data: {
            userID: sessionStorage.userID,
        },
        error: function(data) {
        	$('#errorMsg').html(data.message);
            $('#errorMsg').show();
        }
    });
}