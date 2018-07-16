var payeeDirectory = "/api/payee/";

function addNewPayee(callback) {
	var fullName = document.getElementById("fullName").value;
	var initials = document.getElementById("initials").value;
	var accountNumber = document.getElementById("accountNumber").value;
	var bank = document.getElementById("bank").value;

	let result = $.ajax({
        type: 'POST',
        url: apiUrl + payeeDirectory + "add",
        data: {
        	userID: sessionStorage.userID,
            fullName: fullName.toUpperCase(),
            initials: initials.toUpperCase(),
            accountNumber: accountNumber,
            bank: bank
        },
        error: function(data) {
            return callback(data.responseJSON.message);
        },
        success: function(data) {
            $('#payeeModal').modal('show');
            $("#success").attr("value", function(i, value) {
                return data.accountNumber;
            });
        }
    });

}

function getAllPayeesByUser(callback) {
    var userID = sessionStorage.userID;

    let result = $.ajax({
        type: 'GET',
        url: apiUrl + payeeDirectory + "user",
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

function getPayee(accountNumber, callback) {
    let result = $.ajax({
        type: 'GET',
        url: apiUrl + payeeDirectory + "get",
        data: {
            userID: sessionStorage.userID,
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

function updatePayee(payeeID, accountNumber, callback) {
    var fullName = document.getElementById("fullName").value.toUpperCase();
    var initials = document.getElementById("initials").value;
    var bank = document.getElementById("bank").value;

    let result = $.ajax({
        type: 'POST',
        url: apiUrl + payeeDirectory + "update",
        data: {
            payeeID: payeeID,
            fullName: fullName,
            initials: initials,
            bank: bank
        },
        success: function(data) {
             window.location.replace(domainUrl + "/payee/payee-details.html?account=" + accountNumber);
        },
        error: function(data) {
            $('#errorMsg').html(data.responseJSON.message);
            $('#errorMsg').show();
        }
    });
}

function deletePayee(payeeID, callback) {
    let result = $.ajax({
        type: 'POST',
        url: apiUrl + payeeDirectory + "delete",
        data: {
            payeeID: payeeID,
        },
        success: function(data) {
            $('#delete-successModal').modal('show');
        },
        error: function(data) {
            return callback(data.responseJSON.message);
        }
    });
}
