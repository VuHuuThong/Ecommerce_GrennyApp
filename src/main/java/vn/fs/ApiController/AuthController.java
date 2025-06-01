package vn.fs.ApiController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.fs.dto.LoginDto;
import vn.fs.dto.SignUpDto;
import vn.fs.entities.Role;
import vn.fs.entities.User;
import vn.fs.repository.RoleRepository;
import vn.fs.repository.UserRepository;
import vn.fs.service.SendMailService;


import java.util.Collections;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    SendMailService sendMailService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<User> loginApi(@RequestBody LoginDto loginDto){
        User user = userRepository.findByEmail(loginDto.getNameOrEmail());
        if(user != null){
            if(user.getRoles().contains("ROLE_ADMIN")){
                return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
            }
            Authentication authentication = authenticationManager.authenticate(new
                    UsernamePasswordAuthenticationToken(
                    loginDto.getNameOrEmail(), loginDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        }
        return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpDto> registerUser(@RequestBody SignUpDto signUpDto){

        // add check for username exists in a DB
        if(userRepository.existsByName(signUpDto.getName())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // add check for email exists in DB
        if(userRepository.existsByEmail(signUpDto.getEmail())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        int random_otp = (int) Math.floor(Math.random() * (999999 - 100000 + 1) + 100000);
        //so sanh otp tai vi api ko luu duoc secsion
        signUpDto.setAvatar(String.valueOf(random_otp));
        String body = "<div>\r\n" + "<h3>Mã xác thực OTP của bạn là: <span style=\"color:#119744; font-weight: bold;\">"
                + random_otp + "</span></h3>\r\n" + "</div>";
        sendMailService.queue(signUpDto.getEmail(), "Đăng kí tài khoản", body);


        return ResponseEntity.status(HttpStatus.OK).body(signUpDto);


    }
    @PostMapping("/signupConfim/{otp}")
    public ResponseEntity<SignUpDto> registerUserConfim(@RequestBody SignUpDto signUpDto, @PathVariable int otp){
        //anh xa opt tu randomaotp xuong
        if (otp == Integer.parseInt(signUpDto.getAvatar())) {
            // create user object
            User user = new User();
            user.setName(signUpDto.getName());
            user.setEmail(signUpDto.getEmail());
            user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
            user.setAvatar("user.png");
            user.setRegisterDate(new Date());
            user.setStatus(true);
            Role roles = roleRepository.findByName("ROLE_USER").get();
            user.setRoles(Collections.singleton(roles));
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.OK).body(signUpDto);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}