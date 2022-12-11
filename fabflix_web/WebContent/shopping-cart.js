function handleMainResult(resultData) {

    console.log(resultData);
    $("#cart-empty-container").hide();

    if (jQuery.isEmptyObject(resultData)) {
        console.log("cart empty")
        $("#checkout-container").hide();
        $("#cart-empty-container").show();
        $("#navbar").css("position", "absolute");
        return;
    }

    $("#navbar").css("position", "relative");

    let container = $("#movie-body");
    container.html("");
    let totalOrderPrice = 0;

    for (const key in resultData) {
        const movieObj = resultData[key];
        const title = movieObj.title;
        const quantity = movieObj.quantity;
        const price = movieObj.price;
        const totalPrice = movieObj.totalPrice;
        totalOrderPrice += totalPrice;

        let rowHTML = "<tr id='"+key+"'>";
        rowHTML += "<td>" + title + "</td>";
        rowHTML += "<td>$" + price + "</td>";
        rowHTML += "<td><input name='quantity' min='1' max='200' type='number' value='" + quantity + "' onblur='changeQuantity(\""+key+"\",this.value)'</td>";
        rowHTML += "<td>$" + totalPrice + "</td>";
        rowHTML += "<td><span class='glyphicon glyphicon-trash' onclick='deleteMovie(\""+key+"\")'></span></td>";
        rowHTML += "</tr>";

        container.append(rowHTML);
    }

    // let orderSummaryRow = "<tr style='border-bottom: white'>";
    // orderSummaryRow += "<th colspan='3' class='text-right'>Total</th>";
    // orderSummaryRow += "<th colspan='2' id='total-price'>$" + totalOrderPrice + "</th>";
    // orderSummaryRow += "</tr>";
    // container.append(orderSummaryRow);

    $("#total-price").html("Total: $"+totalOrderPrice);


}

function changeQuantity(movieId, quantity) {
    console.log(movieId,quantity);
    if (quantity) {
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "POST", // Setting request method
            async: false,
            url: "api/changeQuantity?id=" + movieId + "&quantity=" + quantity, // Setting request url, which is mapped by StarsServlet in Stars.java
            success: (resultData) => {console.log(resultData)} // Setting callback function to handle data returned successfully by the StarsServlet
        });
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/shoppingCart", // Setting request url, which is mapped by StarsServlet in Stars.java
            success: (resultData) => handleMainResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    }




}

function handleDeleteFromCart(resultData) {
    console.log(resultData)
    if (resultData["cart"] === "{}") {
        console.log("cart empty")
        $("#checkout-container").hide();
        $("#cart-empty-container").show();
        $("#navbar").css("position", "absolute");
        return;
    }

    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/shoppingCart", // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleMainResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });


}


function deleteMovie(movieId) {
    console.log(movieId);
    $("#"+movieId).remove();

    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: "api/removeFromCart?id=" + movieId,
        success: (resultData) => handleDeleteFromCart(resultData)
    })
}


jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/shoppingCart", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMainResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});