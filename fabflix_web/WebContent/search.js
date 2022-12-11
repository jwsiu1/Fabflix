const genre_colors = {
    Action: 'hsl(0, 100%, 75%)',
    Adult: 'hsl(15, 100%, 75%)',
    Adventure: 'hsl(31.79258742472043, 100%, 75%)',
    Animation: 'hsl(45.12793316425999, 100%, 75%)',
    Biography: 'hsl(60.85421412875453, 100%, 75%)',
    Comedy: 'hsl(75.62511233760345, 100%, 75%)',
    Crime: 'hsl(90.16094860545147, 100%, 75%)',
    Documentary: 'hsl(105.0151143727365, 100%, 75%)',
    Drama: 'hsl(345.2195919735747, 100%, 75%)',
    Family: 'hsl(1  35.78992830616977, 100%, 75%)',
    Fantasy: 'hsl(150.13249102224202, 100%, 75%)',
    History: 'hsl(165.22075812008282, 100%, 75%)',
    Horror: 'hsl(180.0941713962292, 100%, 75%)',
    Music: 'hsl(195.60613860217802, 100%, 75%)',
    Musical: 'hsl(210.32314825682371, 100%, 75%)',
    Mystery: 'hsl(225.0589003019197, 100%, 75%)',
    'Reality-TV': 'hsl(240.6279166618656, 100%, 75%)',
    Romance: 'hsl(255.066503245878216, 100%, 75%)',
    'Sci-Fi': 'hsl(270.37572765374355, 100%, 75%)',
    Sport: 'hsl(285.71320913109386, 100%, 75%)',
    Thriller: 'hsl(300.39311279195016, 100%, 75%)',
    War: 'hsl(315.09455415173545, 100%, 75%)',
    Western: 'hsl(330.998289749281, 100%, 75%)'
};

function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating search info from resultData");
    let container = jQuery("#search_info");
    //if(resultData.length != 0) {
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<div class='col'> <div class='card h-100'>";
        rowHTML += "<div class='card-header'><h4>"
        rowHTML += "<a href='single-movie.html?id=" + resultData[i]["movie_id"] + "'>" + resultData[i]["movie_title"] + "</a>";
        rowHTML += "</h4></div>";
        rowHTML += "<div class='card-body'>";
        rowHTML += "<div class='card-text'><b>Released:</b> " + resultData[i]["movie_year"] + "</div>";
        rowHTML += "<div class='card-text'><b>Director:</b> " + resultData[i]["movie_director"] + "</div>";
        rowHTML += "<div div class='card-text'><b>Rating:</b> "
        let rating = resultData[i]["movie_rating"];
        rowHTML += "<span class='glyphicon glyphicon-star' style='color:orange'></span> " + rating + "/10"
        rowHTML += "</div>";
        rowHTML += "<div class='card-text'><b>Starring: </b>"
        resultData[i]["movie_stars"].forEach((star, index)=>{
            rowHTML += "<a href='single-star.html?id="+star.id+"'>"+ star.name + "</a>";
            if (index != resultData[i]["movie_stars"].length-1) {
                rowHTML += ", "
            }
        });
        rowHTML += "</div>";
        rowHTML += "<div class='card-text'><b>Genres:</b>";
        resultData[i]["movie_genres"].forEach((genre) => {
            let color = genre_colors[genre]
            rowHTML += "&nbsp<button id='genre-button' style='background-color:" + color + "; border-color: " + color + "'>" + genre + "</button>";
        });
        rowHTML += "</div>";
        rowHTML += "</div></div>";
        rowHTML += "</div>";

        // Append the row created to the table body, which will refresh the page
        container.append(rowHTML);
    }

}

let movie_title = getParameterByName('movie_title');
let star_name = getParameterByName('star_name');
let director_name = getParameterByName('director_name');
let movie_year = getParameterByName('movie_year');

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/search?movie_title=" + movie_title + "&star_name=" + star_name + "&director_name=" + director_name + "&movie_year=" + movie_year,
    success: (resultData) => handleResult(resultData)
});