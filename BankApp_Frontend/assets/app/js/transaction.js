var transactionDirectory = "/api/transaction/";

function addNewTransaction(callback) {
	var fromAccount = document.getElementById("account").value;
	var toAccount = document.getElementById("recipientAccount").value;
	var amount = (document.getElementById("amount").value*1).toFixed(2);
	var description = document.getElementById("description").value;
	var payment = document.getElementById("payment").value;

	if(payment == "Immediate") {
		let result = $.ajax({
	        type: 'POST',
	        url: apiUrl + transactionDirectory + "add",
	        data: {
	        	fromAccount: fromAccount,
	        	toAccount: toAccount,
	        	amount: amount,
	        	description: description
	        },
	        error: function(data) {
	            return callback(data.responseJSON.message);
	        },
	        success: function(data) {
	        	$('#successModal').modal('show');
	        }
    	});
	} else{
		addNewRecurringPayment(fromAccount, toAccount, amount, payment.toUpperCase(), description, function(data) {
			return callback(data);
		})
	}
	
}

function getTransactionsByAccount(accountNumber, callback) {
	let result = $.ajax({
        type: 'GET',
        url: apiUrl + transactionDirectory + "account",
        data: {
        	fromAccount: accountNumber
        },
        error: function(data) {
            $('#errorMsg').html(data.responseJSON.message);
        	$('#errorMsg').show();
        },
        success: function(data) {
        	return callback(data);
        }
	});

}

function getTransactionsByUser(callback) {
	let result = $.ajax({
        type: 'GET',
        url: apiUrl + transactionDirectory + "user",
        data: {
        	userID: sessionStorage.userID
        },
        error: function(data) {
            $('#errorMsg').html(data.responseJSON.message);
        	$('#errorMsg').show();
        },
        success: function(data) {
        	return callback(data);
        }
	});
}

function getTransactionByDate(callback) {
	var account = document.getElementById("account").value;
	var startdate = document.getElementById("startdate").value;
	var enddate = document.getElementById("enddate").value;

	if(startdate == "" || enddate == "") {
		$('#errorMsg').html("Please input valid date.");
    	$('#errorMsg').show();
	}else {
		var startArray = startdate.split("/");
		var startFormat = startArray[2] + "-" + startArray[0] + "-" + startArray[1];
		var endArray = enddate.split("/");
		var endFormat = endArray[2] + "-" + endArray[0] + "-" + endArray[1];

		let result = $.ajax({
		    type: 'GET',
		    url: apiUrl + transactionDirectory + "date",
		    data: {
		    	fromAccount: account,
		    	fromDate: startFormat,
		    	toDate: endFormat
		    },
		    error: function(data) {
		        $('#errorMsg').html(data.responseJSON.message);
		    	$('#errorMsg').show();
		    },
		    success: function(data) {
		    	return callback(data);
		   	}
		});
	}
}

function getTransaction (transactionID, callback) {
	let result = $.ajax({
	    type: 'GET',
	    url: apiUrl + transactionDirectory + "get",
	    data: {
	    	transactionID: transactionID
	    },
	    error: function(data) {
	        $('#errorMsg').html(data.responseJSON.message);
	    	$('#errorMsg').show();
	    },
	    success: function(data) {
	    	return callback(data);
	   	}
	});
}