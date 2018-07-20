var monthlybillDirectory = "/api/monthlybill/";

function getBillingOrganization(callback) {
    let result = $.ajax({
        type: 'GET',
        url: apiUrl + monthlybillDirectory + "list",

        success: function(data) {
            return callback(data);
        }
    });
}

function addNewMonthlyBill(callback) {
	var cardNumber = document.getElementById("card").value;
	var pin = document.getElementById("pin").value;
	var billingOrganization = document.getElementById("billingOrganization").value;
	var amount = document.getElementById("amount").value;
	var description = document.getElementById("description").value;

	let result = $.ajax({
        type: 'POST',
        url: apiUrl + monthlybillDirectory + "add",
        data: {
        	cardNumber: cardNumber,
        	PIN: pin,
        	billingOrganization: billingOrganization,
        	amount: amount,
        	description: description
        },
        success: function(data) {
        	$('#successModal').modal('show');
        },
        error: function(data) {
        	return callback(data.responseJSON.message);
        }
	});
}

function getMonthlyBillByCard(cardNumber, callback) {
    let result = $.ajax({
        type: 'GET',
        url: apiUrl + monthlybillDirectory + "card",
        data: {
            cardNumber: cardNumber
        },
        success: function(data) {
            return callback(data);
        }
    });
}

function getMonthlyBill(billID, callback) {
    let result = $.ajax({
        type: 'GET',
        url: apiUrl + monthlybillDirectory + "get",
        data: {
            billID: billID
        },
        success: function(data) {
            return callback(data);
        }
    });
}

function updateMonthlyBill(billID, callback) {
    var billingOrganization = document.getElementById("billingOrganization").value;
    var amount = document.getElementById("amount").value;
    var description = document.getElementById("description").value;

    let result = $.ajax({
        type: 'POST',
        url: apiUrl + monthlybillDirectory + "update",
        data: {
            billID: billID,
            billingOrganization: billingOrganization,
            amount: amount,
            description: description
        },
        success: function(data) {
            window.location.replace(domainUrl + "/monthlybill/monthly-bill-details.html?billID=" + billID);
        },
        error: function(data) {
            $('#errorMsg').html(data.responseJSON.message);
            $('#errorMsg').show();
        }
    });
}

function deleteMonthlyBill(billID, callback) {
    let result = $.ajax({
        type: 'POST',
        url: apiUrl + monthlybillDirectory + "delete",
        data: {
            billID: billID
        },
        success: function(data) {
            $('#delete-successModal').modal('show');
        },
        error: function(data) {
            return callback(data.responseJSON.message);
        }
    });
}
