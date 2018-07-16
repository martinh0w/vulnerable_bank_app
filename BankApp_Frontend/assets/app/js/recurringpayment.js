var recurringpaymentDirectory = "/api/recurringpayment/";

function addNewRecurringPayment(fromAccount, toAccount, amount, period, description, callback) {
	let result = $.ajax({
	    type: 'POST',
	    url: apiUrl + recurringpaymentDirectory + "add",
	    data: {
	    	fromAccount: fromAccount,
	    	toAccount: toAccount,
	    	amount: amount,
	    	period: period,
	    	description: description
	    },
	    error: function(data) {
	        return callback(data.responseJSON.message);
	    },
	    success: function(data) {
	    	$('#successModal').modal('show');
	    }
	})
}

function getRecurringPaymentByAccount(fromAccount, callback) {
	let result = $.ajax({
	    type: 'GET',
	    url: apiUrl + recurringpaymentDirectory + "account",
	    data: {
	    	fromAccount: fromAccount,
	    },
	    success: function(data) {
        	return callback(data);
        }
	})
}

function getRecurringPayment(recurringID, callback) {
	let result = $.ajax({
	    type: 'GET',
	    url: apiUrl + recurringpaymentDirectory + "get",
	    data: {
	    	recurringID: recurringID,
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

function updateRecurringPayment(recurringID, callback) {
	var amount = document.getElementById("amount").value;
	var period = document.getElementById("period").value;
	var description = document.getElementById("description").value;

	let result = $.ajax({
	    type: 'POST',
	    url: apiUrl + recurringpaymentDirectory + "update",
	    data: {
	    	recurringID: recurringID,
	    	amount: amount,
	    	period: period,
	    	description: description
	    },
	    success: function(data) {
            window.location.replace(domainUrl + "/recurringpayment/recurring-payment-details.html?recurringID=" + recurringID);
        },
        error: function(data) {
            $('#errorMsg').html(data.responseJSON.message);
            $('#errorMsg').show();
        }
	})
}

function deleteRecurringPayment(recurringID, callback) {
	let result = $.ajax({
	    type: 'POST',
	    url: apiUrl + recurringpaymentDirectory + "delete",
	    data: {
	    	recurringID: recurringID
	    },
	    success: function(data) {
            $('#delete-successModal').modal('show');
        },
        error: function(data) {
            return callback(data.responseJSON.message);
        }
	})
}



