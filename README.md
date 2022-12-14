# Fabflix
- XMLParser : Parser to add large amounts of movies and stars into database from XML files.
- fabflix_android : Android development for Fabflix that runs on the same backend API as the web version. Can be ran on Android Studio with emulator.
- fabflix_web : Web development for Fabflix that runs on an EC2 instance on AWS. Follow instructions below to access website.
- log_processor : Log processor that calculates average runtimes of JDBC connections and servlet times.
- sql_files : SQL files to create and populate moviedb database.

INSTRUCTIONS FOR DEPLOYMENT:
1. Visit https://34.211.51.144:8443/fabflix/.
2. If there is a "Your connection is not private" warning, click on Advanced -> Proceed to 34.211.51.144 (unsafe). The site is self-authenticated which will throw the      warning when using HTTPS.
3. You will be directed to the login page.
4. Use "a@email.com" as username and "a2" as password. Complete reCAPTCHA verification and continue to login.
5. You will be directed to the Fabflix main page.
