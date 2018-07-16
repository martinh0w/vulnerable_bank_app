var cardDirectory = "/api/card/";

function getAllCardsByAccount (accountNumber, callback) {

	let result = $.ajax({
        type: 'GET',
        url: apiUrl + cardDirectory + "account",
        data: {
        	accountNumber: accountNumber
        },
        success: function(data) {
        	return callback(data);
        }
    });
}

function getCard (cardNumber, callback) {
	let result = $.ajax({
        type: 'GET',
        url: apiUrl + cardDirectory + "get",
        data: {
        	cardNumber: cardNumber
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

function updateCard (cardNumber, callback) {
	var pin = document.getElementById("pin").value;
	var spendingLimit = document.getElementById("spendingLimit").value;

	let result = $.ajax({
        type: 'POST',
        url: apiUrl + cardDirectory + "update",
        data: {
        	cardNumber: cardNumber,
        	pin: pin,
        	spendingLimit: spendingLimit
        },
        success: function(data) {
        	 window.location.replace(domainUrl + "/card/card-details.html?card=" + cardNumber);
        },
        error: function(data) {
        	$('#errorMsg').html(data.responseJSON.message);
            $('#errorMsg').show();
        }
    });
}

function deleteCard(cardNumber, callback) {
	let result = $.ajax({
        type: 'POST',
        url: apiUrl + cardDirectory + "delete",
        data: {
        	cardNumber: cardNumber
        },
        success: function(data) {
        	 $('#delete-successModal').modal('show');
        },
        error: function(data) {
        	return callback(data.responseJSON.message);
        }
    });
}

function addNewCard(callback) {
	var cardNumber = document.getElementById("cardNumber").value;
	var pin = document.getElementById("pin").value;
	var accountNumber = document.getElementById("accountNumber").value;
	var spendingLimit = document.getElementById("spendingLimit").value;
	var cardType = document.getElementById("cardType").value;

	let result = $.ajax({
        type: 'POST',
        url: apiUrl + cardDirectory + "add",
        data: {
        	cardNumber: cardNumber,
        	accountNumber: accountNumber,
        	PIN: pin,
        	spendingLimit: spendingLimit,
        	cardType: cardType
        },
        error: function(data) {
        	return callback(data.responseJSON.message);
        },
        success: function(data) {
        	$('#cardModal').modal('show');
            $("#success").attr("value", function(i, value) {
                return data.cardNumber;
            });
        }
    });

}