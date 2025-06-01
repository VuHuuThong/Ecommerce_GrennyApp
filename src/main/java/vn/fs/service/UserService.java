package vn.fs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.fs.entities.User;
import vn.fs.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository repo;
    public List<User> listAll() {
        return (List<User>) repo.findAll();
    }
}
