jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/_dashboard",
    success: (resultData) => handleResult(resultData)
});

function handleResult(resultData) {
    console.log("handleResult: getting table metadata");

    let tableMetadata = jQuery("#table_metadata_body");

    for(let i = 0; i<resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<table>"
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" + resultData[i]['table_name'] + ": </th>";
        for(let j=0; j<resultData[i]['attributes'].length; j++) {
            rowHTML += "<tr><td>" + resultData[i]['attributes'][j]['Field'] + " ("
                + resultData[i]['attributes'][j]['Type'] + ")</td></tr>";
        }
        rowHTML += "</tr>";
        rowHTML += "</table>"
        tableMetadata.append(rowHTML);
    }

}

const addStarForm = $("#add_star_form");

function submitAddStarForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    console.log(addStarForm.serialize());
    jQuery.ajax({
        dataType: "JSON",
        method: "POST",
        data: addStarForm.serialize(),
        url: "api/addToStars",
        success: (responseData) => {
            if (responseData.status === "success") {
                window.location.replace("add-confirmation-page.html");
            } else {
                $("#error-card").css("display","block");
                $("#error-card").text("Star was not added, please try again");
            }
        }
    });
}

addStarForm.submit(submitAddStarForm);

const addMovieForm = $("#add_movie_form");

function submitAddMovieForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    console.log(addMovieForm.serialize());
    jQuery.ajax({
        dataType: "JSON",
        method: "POST",
        data: addMovieForm.serialize(),
        url: "api/addToMovies",
        success: (responseData) => {
            if (responseData.status === "success") {
                window.location.replace("add-movie-confirmation-page.html");
            } else {
                console.log("ERROR");
                $("#error-card").css("display","block");
                $("#error-card").text(responseData["message"] + "\nDuplicate movie");
            }
        }
    });
}

addMovieForm.submit(submitAddMovieForm);