var foreignTransactionDirectory = "/api/transaction/foreign/";

function getExchangeRate(callback) {
	let result = $.ajax({
        type: 'GET',
        url: apiUrl + foreignTransactionDirectory + "list",

        success: function(data) {
        	return callback(data);
        }
	});
}

function addNewForeignTransaction(callback) {
	var fromAccount = document.getElementById("account").value;
	var toAccount = document.getElementById("recipientAccount").value;
	var amount = (document.getElementById("amount").value*1).toFixed(2);
	var currencyCode = $('#currencyCode').text();
	var description = document.getElementById("description").value;

	let result = $.ajax({
	    type: 'POST',
	    url: apiUrl + foreignTransactionDirectory + "add",
	    data: {
	    	fromAccount: fromAccount,
	    	toAccount: toAccount,
	    	amount: amount,
	    	description: description,
	    	currencyCode: currencyCode
	    },
	    success: function(data) {
	    	$('#successModal').modal('show');
	    },
	    error: function(data) {
	    	return callback(data.responseJSON.message);
	    }
	});
	 
}