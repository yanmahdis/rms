package com.mitrais.rms.controller;

import com.mitrais.rms.dao.UserDao;
import com.mitrais.rms.dao.impl.UserDaoImpl;
import com.mitrais.rms.model.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/users/*")
public class UserServlet extends AbstractController {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcher;
        String path = getTemplatePath(req.getServletPath() + "/form");
        String id = req.getParameter("id");
        User user;
        switch (req.getPathInfo()) {
            case "/list":
                this.listUser(req, resp);
                break;
            case "/add":
                requestDispatcher = req.getRequestDispatcher(path);
                requestDispatcher.forward(req, resp);
                break;
            case "/edit":
                user = getUser(Long.parseLong(id));
                req.setAttribute("user", user);
                requestDispatcher = req.getRequestDispatcher(path);
                requestDispatcher.forward(req, resp);
                break;
            case "/delete":
                user = getUser(Long.parseLong(id));
                this.deleteUser(user);
                resp.sendRedirect(req.getContextPath() + "/users/list");
                break;
            default:
                this.listUser(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String id = req.getParameter("id");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        Map<String, String> messages = isValid(username, password);
        try {
            if (messages.size() == 0) {
                Long userId = id != null && id != ""? Long.parseLong(id) : null;
                User user = new User(userId, username, password);
                boolean isSuccess = this.saveUser(user);
                if (isSuccess) {
                    resp.sendRedirect(req.getContextPath() + "/users/list");
                } else {
                    messages.put("error", "There is an error while saving the data, please try again later");
                    req.setAttribute("messages", messages);
                    resp.sendRedirect(req.getContextPath() + "/users/list");
                }
            } else {
                String path = getTemplatePath(req.getServletPath() + "/form");
                req.setAttribute("messages", messages);
                RequestDispatcher requestDispatcher = req.getRequestDispatcher(path);
                requestDispatcher.forward(req, resp);
            }
        } catch (ServletException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Internal Server Error, please contact your administrator.");
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Internal Server Error, please contact your administrator.");
        }
    }

    private User getUser(long id) {
        UserDao userDao = UserDaoImpl.getInstance();
        Optional<User> user = userDao.find(id);
        if(user.isPresent()) {
            return user.get();
        }

        throw new RuntimeException("Id not found");
    }

    private void listUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String path = getTemplatePath(req.getServletPath() + "/list");
        UserDao userDao = UserDaoImpl.getInstance();
        List<User> users = userDao.findAll();
        req.setAttribute("users", users);
        RequestDispatcher requestDispatcher = req.getRequestDispatcher(path);
        requestDispatcher.forward(req, resp);
    }

    private boolean saveUser(User user) {
        UserDao userDao = UserDaoImpl.getInstance();
        boolean isSuccess;
        if(user.getId() == null) {
            isSuccess = userDao.save(user);
        } else {
            isSuccess = userDao.update(user);
        }

        return isSuccess;
    }

    private void deleteUser(User user) {
        UserDao userDao = UserDaoImpl.getInstance();
        userDao.delete(user);

    }
    private Map<String, String> isValid(String username, String password) {
        Map<String, String> messages = new HashMap<>();

        if( username == null || username.isEmpty()) {
            messages.put("username", "username is required");
        }

        if( password == null || password.isEmpty()) {
            messages.put("password", "password is required");
        }

        return messages;
    }
}
