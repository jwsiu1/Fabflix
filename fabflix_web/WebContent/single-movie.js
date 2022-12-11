const genre_colors = {
    Action: 'hsl(0, 100%, 85%)',
    Adult: 'hsl(15, 100%, 85%)',
    Adventure: 'hsl(31.79258742472043, 100%, 85%)',
    Animation: 'hsl(45.12793316425999, 100%, 85%)',
    Biography: 'hsl(60.85421412875453, 100%, 85%)',
    Comedy: 'hsl(75.62511233760345, 100%, 85%)',
    Crime: 'hsl(90.16094860545147, 100%, 85%)',
    Documentary: 'hsl(105.0151143727365, 100%, 85%)',
    Drama: 'hsl(345.2195919735747, 100%, 85%)',
    Family: 'hsl(135.78992830616977, 100%, 85%)',
    Fantasy: 'hsl(150.13249102224202, 100%, 85%)',
    History: 'hsl(165.22075812008282, 100%, 85%)',
    Horror: 'hsl(180.0941713962292, 100%, 85%)',
    Music: 'hsl(195.60613860217802, 100%, 85%)',
    Musical: 'hsl(210.32314825682371, 100%, 85%)',
    Mystery: 'hsl(225.0589003019197, 100%, 85%)',
    'Reality-TV': 'hsl(240.6279166618656, 100%, 85%)',
    Romance: 'hsl(255.066503245878216, 100%, 85%)',
    'Sci-Fi': 'hsl(270.37572765374355, 100%, 85%)',
    Sport: 'hsl(285.71320913109386, 100%, 85%)',
    Thriller: 'hsl(300.39311279195016, 100%, 85%)',
    War: 'hsl(315.09455415173545, 100%, 85%)',
    Western: 'hsl(330.998289749281, 100%, 85%)'
};

function redirectMoviesPage() {
    $.ajax({
        method: "GET",
        dataType: "json",
        url: "api/moviesPageState",
        success: (data) => {
            console.log(data);
            if (data["uri"] != "") {
                window.location.replace(data["uri"]);
            }
        }
    })
}

/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
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

    console.log("handleResult: populating movie table from resultData");

    let sortedGenres = resultData[0]["movie_genres"].sort(function(a,b) {
        return a.name.localeCompare(b.name);
    });

    jQuery("#movie-card").css("background-color", genre_colors[sortedGenres[0].name]);
    jQuery("#movie-title").text(resultData[0]["movie_title"]);
    jQuery("#card-info").text(resultData[0]["movie_year"] + ", " + sortedGenres[0].name);
    jQuery("#card-rating").append(resultData[0]["movie_rating"] + "/10");
    jQuery("#director").text(resultData[0]["movie_director"]);
    jQuery("#year").text(resultData[0]["movie_year"]);
    jQuery("#rating").prepend(resultData[0]["movie_rating"]);


    let genreElement = jQuery("#genres");

    sortedGenres.forEach((genre)=>{
        let color = genre_colors[genre.name];
        let button = '<a class="genre-link" href="movies.html?genre_id=' + genre.id +
            '&title_name=&movie_title=&star_name=&director_name=&movie_year=&page=1&display=10">'
        button += "<button class='genre-button' style='background-color:" + color +
            "'>" + genre.name + "</button>&nbsp";
        button += "</a>";
        genreElement.append(button)
    });

    let starsElement = jQuery("#movie-stars");
    let sortedStars = resultData[0]["movie_stars"].sort(function(a, b) {
        let a_name = a.name.split(" ");
        let b_name = b.name.split(" ");

        let a_last = a_name[a_name.length-1].toLowerCase() === "jr." ? a_name[a_name.length-2] : a_name[a_name.length-1];
        let b_last = b_name[b_name.length-1].toLowerCase() === "jr." ? b_name[b_name.length-2] : b_name[b_name.length-1];
        console.log(a_last, b_last)
        return a_last.localeCompare(b_last);
    });
    console.log(sortedStars);
    sortedStars.forEach((star, index)=>{
        let starElem = "<a href='single-star.html?id=" + star.id + "'>" +
            star.name + "</a>";
        if (index != 0) {
            starElem = "&nbsp&nbsp|&nbsp&nbsp" + starElem;
        }
        starsElement.append(starElem)
    });
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

function addToCart() {
    console.log("ADD TO CART CALLED")
    $.ajax({
        method: 'POST',
        url: 'api/addToCart?id='+movieId,
        async: true,
        success: function(data) {
            console.log(data);
            $("#add-to-cart-status").text("Added to cart");
        }
    })
}
