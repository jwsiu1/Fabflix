let cartQuantity = 0;

function handleOrderSummary(data) {
    let orderTotal = 0;
    let quantity = 0;
    let price = 0;

    for (const key in data) {
        console.log(data[key]);
        const movieData = data[key];
        quantity += movieData.quantity;
        orderTotal += movieData.totalPrice;
        price = movieData.price;
    }

    $('#quantity').text(quantity);
    $('#pricePer').text(price);
    $('#total').text(orderTotal);

    cartQuantity = quantity;
}

function handleOrderResponse(data) {
    console.log(data);
    if (data.status == "fail") {
        $("#error-card").css("display","block");
        $("#error-card").text(data["message"]);
    } else {
        $("#error-card").text(data["message"]);
        $("#error-card").css("display","none");
        jQuery.ajax({
            dataType: "JSON",
            method: "POST",
            url: "api/addToSales",
            success: (responseData) => {
                if (responseData.status === "success") {
                    window.location.replace("confirmation-page.html");
                } else {
                    $("#error-card").css("display","block");
                    $("#error-card").text(data["message"]);
                }
            }
        });
    }
}


const paymentForm = $("#payment-form");

function submitOrder(formSubmitEvent) {

    formSubmitEvent.preventDefault();

    console.log(cartQuantity)
    if (cartQuantity == 0) {
        $("#error-card").css("display","block");
        $("#error-card").text("Cart is empty");
        return;
    }
    console.log(paymentForm.serialize());

    jQuery.ajax({
        dataType: "JSON",
        method: "POST",
        data: paymentForm.serialize(),
        url: "api/payment",
        success: (data) => handleOrderResponse(data)
    });
}

paymentForm.submit(submitOrder)

jQuery.ajax({
    dataType: "JSON",
    method: "GET",
    url: "api/shoppingCart",
    success: (data) => handleOrderSummary(data)
});