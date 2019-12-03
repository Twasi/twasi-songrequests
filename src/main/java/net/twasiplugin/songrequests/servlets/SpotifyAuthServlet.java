package net.twasiplugin.songrequests.servlets;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SpotifyAuthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("callback.html")));
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = br.readLine()) != null) sb.append(s);
        resp.setStatus(200);
        resp.addHeader("Content-Type", "text/html");
        resp.getWriter().print(sb.toString());
    }
}
