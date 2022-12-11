/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMainResult(resultData) {
    console.log("handleMainResult: populating genre and title table from resultData");
    let genreTableBodyElement = jQuery("#genre_table_body");
    let titleTableBodyElement = jQuery("#title_table_body");

    for (let i = 0; i < resultData.length; i++) {
        if(resultData[i]["genre_name"] != undefined) {
            let rowHTML = "";
            rowHTML += "<tr>";
            rowHTML +=
                "<th>" +
                '<a href="movies.html?genre_id=' + resultData[i]['genre_id'] +
                '&title_name=&genre_id=&title_name=&movie_title=&star_name=&director_name=&movie_year=&page=1&display=10">'
                + resultData[i]['genre_name'] +
                '</a>' +
                "</th>";
            rowHTML += "</tr>";
            genreTableBodyElement.append(rowHTML);
        }
        else if(resultData[i]['title_name'].match(/[a-zA-Z0-9_-]/)) {
            let rowHTML = "";
            rowHTML += "<tr>";
            rowHTML +=
                "<th>" +
                '<a href="movies.html?genre_id=&title_name=' + resultData[i]['title_name'] + '&genre-id=&title_name=&movie_title=&star_name=&director_name=&movie_year=&page=1&display=10">'
                + resultData[i]['title_name'] +
                '</a>' +
                "</th>";
            rowHTML += "</tr>";
            titleTableBodyElement.append(rowHTML);
        }
    }

    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML +=
        "<th>" +
        '<a href="movies.html?genre_id=&title_name=*&genre-id=&title_name=&movie_title=&star_name=&director_name=&movie_year=&page=1&display=10">'
        + '*' +
        '</a>' +
        "</th>";
    rowHTML += "</tr>";
    titleTableBodyElement.append(rowHTML);
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/main",
    success: (resultData) => handleMainResult(resultData)
});