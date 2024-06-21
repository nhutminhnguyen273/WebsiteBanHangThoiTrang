package com.nhom15.fashion.service;

import com.nhom15.fashion.models.User;
import com.nhom15.fashion.models.UserRole;
import com.nhom15.fashion.repositories.IRoleRepository;
import com.nhom15.fashion.repositories.IUserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class UserService implements UserDetailsService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private EmailService emailService;

    // Lưu người dùng mới vào database sau khi mã hóa password
    public void save(@NotNull User user){
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }

    // Gán vai trò mặc định cho người dùng dựa trên người dùng
    public void setDefaultRole(String username){
        userRepository.findByUsername(username).ifPresentOrElse(
                user -> {
                    user.getRoles().add(roleRepository.findRoleById(UserRole.CUSTOMER.value));
                    userRepository.save(user);
                },
                () -> {throw new UsernameNotFoundException("Không tìm thấy người dùng.");}
        );
    }

    public void setEmployerRole(String username){
        userRepository.findByUsername(username).ifPresentOrElse(
                user -> {
                    user.getRoles().add(roleRepository.findRoleById(UserRole.EMPLOYER.value));
                    userRepository.save(user);
                },
                () -> {throw new UsernameNotFoundException("Không tìm thấy người dùng.");}
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    }

    // Tìm kiếm người dùng dựa trên tên đăng nhập
    public Optional<User> findByUsername(String username) throws UsernameNotFoundException{
        return userRepository.findByUsername(username);
    }

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public List<User> getAllEmployer(){
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("EMPLOYER")))
                .collect(Collectors.toList());
    }

    public void addEmployer(User user){
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        setEmployerRole(user.getUsername());
    }

    public void updateUser(User user){
        var existingUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng."));
        existingUser.setUsername(user.getUsername());
        existingUser.setFullName(user.getFullName());
        existingUser.setDateOfBirth(user.getDateOfBirth());
        existingUser.setGender(user.isGender());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone(user.getPhone());
        userRepository.save(existingUser);
    }

    public void deleteUser(Long id){
        if(!userRepository.existsById(id)){
            throw new IllegalStateException("Không tìm thấy người dùng.");
        }
        userRepository.deleteById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User processOAuthPostLogin(OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        Optional<User> existUser = findByEmail(email);

        User user;
        if (existUser.isEmpty()) {
            user = new User();
            user.setEmail(email);
            user.setUsername(email);
            user.setPassword("");
            user.setFullName(oauth2User.getAttribute("name"));
            userRepository.save(user);
            setDefaultRole(user.getUsername());
        } else {
            user = existUser.get();
        }
        return user;
    }

    public void createPasswordResetTokenForUser(User user) {
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        userRepository.save(user);

        String resetUrl = "http://localhost:8080/reset-password?token=" + token;
        emailService.sendSimpleEmail(user.getEmail(), "Reset Password", "Click the link to reset your password: " + resetUrl);
    }

    public Optional<User> getUserByPasswordResetToken(String token) {
        return userRepository.findByResetPasswordToken(token);
    }

    public void resetPassword(User user, String newPassword) {

        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }
}