var accountDirectory = "/api/account/";

function getAllAccountByUser(userID, callback) {
	let result = $.ajax({
        type: 'GET',
        url: apiUrl + accountDirectory + "user",
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
    })
}


function getAccount(accountNumber, callback) {
    let result = $.ajax({
        type: 'GET',
        url: apiUrl + accountDirectory + "get",
        data: {
            accountNumber: accountNumber,
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


function updateAccount(accountNumber) {
    var spendingLimit = document.getElementById("spendingLimit").value;
    var withdrawalLimit = document.getElementById("withdrawalLimit").value;

    let result = $.ajax({
        type: 'POST',
        url: apiUrl + accountDirectory + "update",
        data: {
            accountNumber: accountNumber,
            spendingLimit: spendingLimit,
            withdrawalLimit: withdrawalLimit
        },
        success: function(data) {
            window.location.replace(domainUrl + "/account/account-details.html?account=" + accountNumber);
        },
        error: function(data) {
            $('#errorMsg').html(data.responseJSON.message);
            $('#errorMsg').show();
        }
    });
}

function deleteAccount(accountNumber, callback) {
    let result = $.ajax({
        type: 'POST',
        url: apiUrl + accountDirectory + "delete",
        data: {
            accountNumber: accountNumber,
        },
        success: function(data) {
            $('#delete-successModal').modal('show');
        },
        error: function(data) {
            return callback(data.responseJSON.message);
        }
    });
}