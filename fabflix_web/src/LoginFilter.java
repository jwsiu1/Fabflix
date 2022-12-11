//import javax.servlet.*;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * Servlet Filter implementation class LoginFilter
// */
//@WebFilter(filterName = "LoginFilter", urlPatterns = "/*")
//public class LoginFilter implements Filter {
//    private final ArrayList<String> allowedURIs = new ArrayList<>();
//    private final ArrayList<String> employeeURIs = new ArrayList<>();
//
//    /**
//     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
//     */
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//        System.out.println("LoginFilter: " + httpRequest.getRequestURI());
//        if (this.isUrlAllowedWithoutLogin(httpRequest.getRequestURI())) {
//            chain.doFilter(request, response);
//            return;
//        }
//        if (httpRequest.getSession().getAttribute("employee_user") == null
//                && httpRequest.getSession().getAttribute("user") == null
//                && !this.isUrlAllowedWithoutEmployeeLogin(httpRequest.getRequestURI())) {
//            httpResponse.sendRedirect("login.html");
//        }
//        else if (httpRequest.getSession().getAttribute("employee_user") == null
//                && this.isUrlAllowedWithoutEmployeeLogin(httpRequest.getRequestURI())){
//            httpResponse.sendRedirect("employee-login");
//        }
//        else {
//            chain.doFilter(request, response);
//        }
//    }
//    private boolean isUrlAllowedWithoutEmployeeLogin(String requestURI) {
//        return employeeURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
//    }
//    private boolean isUrlAllowedWithoutLogin(String requestURI) {
//        return allowedURIs.stream().anyMatch(requestURI.toLowerCase()::endsWith);
//    }
//    public void init(FilterConfig fConfig) {
//        allowedURIs.add("login.html");
//        allowedURIs.add("login.js");
//        allowedURIs.add("api/login");
//        allowedURIs.add("styles/samuel-regan-asante-wMkaMXTJjlQ-unsplash.jpg");
//        allowedURIs.add("styles/login.css");
//        allowedURIs.add("employee-login.html");
//        allowedURIs.add("employee-login.js");
//        allowedURIs.add("employee-login");
//        allowedURIs.add("api/employee-login");
//
//        employeeURIs.add("_dashboard");
//        employeeURIs.add("_dashboard.html");
//        employeeURIs.add("_dashboard.js");
//    }
//    public void destroy() {}
//}