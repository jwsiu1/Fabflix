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

function addToCart(movieId) {
    console.log("ADD TO CART CALLED")
    $.ajax({
        method: 'POST',
        url: 'api/addToCart?id='+movieId,
        async: true,
        success: function(data) {
            console.log(data);
            $("#add-to-cart-status-"+movieId).text("Added to cart");
        }
    })
}

let maxRows = 0;

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(data) {
    console.log("handleResult: populating movie info from resultData");

    let container = jQuery("#container");
    container.html("");

    maxRows = data["total"];
    console.log(maxRows);

    let resultData = data["movies"];
    for (let i = 0; i < resultData.length; i++) {

        let rowHTML = "";
        rowHTML += "<div class='col'> <div class='card h-100'>";
        rowHTML += "<div class='card-header'><h4>";
            rowHTML += "<a href='single-movie.html?id=" + resultData[i]["movie_id"] + "'>" + resultData[i]["movie_title"] + "</a>";
            rowHTML += "</h4>";
        rowHTML += "</div>";
        rowHTML += "<div class='card-body'>";
        rowHTML += "<div class='card-text'><b>Released:</b> " + resultData[i]["movie_year"] + "</div>";
        rowHTML += "<div class='card-text'><b>Director:</b> " + resultData[i]["movie_director"] + "</div>";
        rowHTML += "<div class='card-text'><b>Rating:</b> ";
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
                let color = genre_colors[genre.name]
                rowHTML += '<a class="genre-link" href="movies.html?genre_id=' + genre.id +
                    '&title_name=&movie_title=&star_name=&director_name=&movie_year=&page=1&display=10">'
                rowHTML += "&nbsp<button id='genre-button' style='background-color:" + color + "; border-color: " + color + "'>" + genre.name + "</button>";
                rowHTML += '</a>';
            });
        rowHTML += "</div>";
        rowHTML += "</div>"
        rowHTML += "<div class='card-footer'>";
            rowHTML += "<button class='btn btn-default btn-sm add-to-cart-btn' id='add-to-cart-btn-"+ resultData[i]["movie_id"] +"' onClick='addToCart(\""+resultData[i]["movie_id"]+"\")'>";
                rowHTML += "<span class='glyphicon glyphicon-shopping-cart green'></span>";
                rowHTML += "<b id='add-to-cart-status-"+ resultData[i]["movie_id"] +"'>Add to Cart</b>";
            rowHTML += "</button>";
        rowHTML += "</div>";
        rowHTML += "</div>";
        rowHTML += "</div>";
        // Append the row created to the table body, which will refresh the page
        container.append(rowHTML);
    }

}


/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */
let genre_Id = getParameterByName('genre_id');
let title_name = getParameterByName('title_name');
let movie_title= getParameterByName('movie_title');
let star_name = getParameterByName('star_name');
let director_name = getParameterByName('director_name');
let movie_year = getParameterByName('movie_year');

let page = getParameterByName("page") != null ? getParameterByName("page") : "";
let display = getParameterByName("display") != null ? getParameterByName("display") : "";
if (display == "10" || display == "25" || display == "50" || display == "100") {
    $("#display").val(display);
}

let sortStatus = getParameterByName("sort");

function changeDisplay() {
    const firstSort = $("#firstSort").val();
    const titleSort = $("#titleSort").val();
    const ratingSort = $("#ratingSort").val();
    const updateDisplay = $("#display").val();

    page = 1;
    let params = "genre_id=" + genre_Id + "&title_name=" + title_name + "&movie_title=" + movie_title + "&star_name=" + star_name + "&director_name=" + director_name + "&movie_year=" + movie_year
        + "&sortFirst=" + firstSort + "&titleSort=" + titleSort + "&ratingSort=" + ratingSort
        + "&page=" + page + "&display=" + updateDisplay + "&sort=true";
    setMoviesPageState("movies.html?"+params)

    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/sortedMovies?" + params,// Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}

function goPrevious() {
    if (parseInt(page) == 1) {
        return;
    }
    const updateDisplay = $("#display").val();
    const firstSort = $("#firstSort").val();
    const titleSort = $("#titleSort").val();
    const ratingSort = $("#ratingSort").val();

    let url = "movies.html?genre_id="+ genre_Id + "&title_name=" + title_name + "&movie_title=" + movie_title + "&star_name=" + star_name + "&director_name=" + director_name + "&movie_year=" + movie_year
        + "&sortFirst=" + firstSort + "&titleSort=" + titleSort + "&ratingSort=" + ratingSort
        + "&page=" + (parseInt(page) - 1) + "&display=" + updateDisplay
        + "&sort=true";
    window.location.replace(url);
}

function goNext() {
    const updateDisplay = $("#display").val();
    if (parseInt(page) * parseInt(updateDisplay) > maxRows) {
        return;
    }
    const firstSort = $("#firstSort").val();
    const titleSort = $("#titleSort").val();
    const ratingSort = $("#ratingSort").val();

    let url = "movies.html?genre_id="+ genre_Id + "&title_name=" + title_name + "&movie_title=" + movie_title + "&star_name=" + star_name + "&director_name=" + director_name + "&movie_year=" + movie_year
        + "&sortFirst=" + firstSort + "&titleSort=" + titleSort + "&ratingSort=" + ratingSort
        + "&page=" + (parseInt(page) + 1) + "&display=" + updateDisplay
        + "&sort=true";
    window.location.replace(url);
}

function changeSort() {
    const firstSort = $("#firstSort").val();
    const titleSort = $("#titleSort").val();
    const ratingSort = $("#ratingSort").val();
    const updateDisplay = $("#display").val();

    console.log(firstSort, titleSort, ratingSort);

    let params = "genre_id=" + genre_Id + "&title_name=" + title_name + "&movie_title=" + movie_title + "&star_name=" + star_name + "&director_name=" + director_name + "&movie_year=" + movie_year
    + "&sortFirst=" + firstSort + "&titleSort=" + titleSort + "&ratingSort=" + ratingSort
    + "&page=1" + "&display=" + updateDisplay + "&sort=true";

    setMoviesPageState("movies.html?" + params)

    // Makes the HTTP GET request and registers on success callback function handleResult
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/sortedMovies?" + params,// Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}

function setMoviesPageState(uri) {
    console.log(uri);
    let postData = {"uri":uri}
    jQuery.ajax({
        dataType: "json",
        method: "POST",
        data: postData,
        url: "api/moviesPageState",
        success: (data) => {console.log(data);}
    });
}

// Makes the HTTP GET request and registers on success callback function handleResult
let currentURI = window.location.href.split("/").at(-1);
setMoviesPageState(currentURI)


if (sortStatus != null) {
    let firstSort = getParameterByName("sortFirst");
    let titleSort = getParameterByName("titleSort");
    let ratingSort = getParameterByName("ratingSort");

    $("#firstSort").val(firstSort);
    $("#titleSort").val(titleSort);
    $("#ratingSort").val(ratingSort);

    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/sortedMovies?genre_id=" + genre_Id + "&title_name=" + title_name + "&movie_title=" + movie_title + "&star_name=" + star_name + "&director_name=" + director_name + "&movie_year=" + movie_year
            + "&sortFirst=" + firstSort + "&titleSort=" + titleSort + "&ratingSort=" + ratingSort
            + "&page=" + page + "&display=" + display,// Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
} else {
    jQuery.ajax({
        dataType: "json",  // Setting return data type
        method: "GET",// Setting request method
        url: "api/movies?genre_id=" + genre_Id + "&title_name=" + title_name + "&movie_title=" + movie_title + "&star_name=" + star_name + "&director_name=" + director_name + "&movie_year=" + movie_year
            + "&page=" + page + "&display=" + display, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
    });
}
