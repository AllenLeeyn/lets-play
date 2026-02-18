package com.example.lets_play.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.lets_play.dto.UserCreateRequest;
import com.example.lets_play.dto.UserResponse;
import com.example.lets_play.dto.UserUpdateRequest;
import com.example.lets_play.model.User;
import com.example.lets_play.repository.UserRepository;

@Service
public class UserService {

    private static final String OBJECT_ID_PATTERN = "^[a-fA-F0-9]{24}$";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String defaultAdminEmail;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       @Value("${admin.seed.email}") String defaultAdminEmail) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultAdminEmail = defaultAdminEmail != null ? defaultAdminEmail.trim().toLowerCase() : null;
    }

    /** Admin only. Returns all users (no password in response). */
    public List<UserResponse> listUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /** Admin only. Creates a new user; throws if email already registered. */
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new IllegalStateException("Email already registered");
        }
        User.Role role = parseRoleOrThrow(request.getRole());
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        user = userRepository.save(user);
        return toResponse(user);
    }

    /** Admin or self (enforced by controller @PreAuthorize). Returns user by id; 404 if not found. */
    public UserResponse getUserById(String id) {
        validateObjectId(id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toResponse(user);
    }

    /** Admin or self (enforced by controller @PreAuthorize). Admins may update USER or self only; cannot update another admin. At least one field required. */
    public UserResponse updateUser(String id, UserUpdateRequest request, boolean isSelf) {
        validateObjectId(id);
        if (!request.hasAnyField()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one field must be provided");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (user.getRole() == User.Role.ADMIN && !isSelf) {
            throw new AccessDeniedException("Cannot update another admin");
        }

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            String newEmail = request.getEmail().trim().toLowerCase();
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new IllegalStateException("Email already in use");
            }
            user.setEmail(newEmail);
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            user.setRole(parseRoleOrThrow(request.getRole()));
        }

        user = userRepository.save(user);
        return toResponse(user);
    }

    /** Admin or self (enforced by controller). Admins may delete users only; an admin may delete another admin only if isSelf. Default admin cannot be deleted. */
    public void deleteUser(String id, boolean isSelf) {
        validateObjectId(id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (defaultAdminEmail != null && defaultAdminEmail.equals(user.getEmail())) {
            throw new AccessDeniedException("Cannot delete default admin");
        }
        if (user.getRole() == User.Role.ADMIN && !isSelf) {
            throw new AccessDeniedException("Cannot delete another admin");
        }
        userRepository.deleteById(id);
    }

    private void validateObjectId(String id) {
        if (id == null || !id.matches(OBJECT_ID_PATTERN)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid user ID format");
        }
    }

    private static User.Role parseRoleOrThrow(String role) {
        try {
            return User.Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must be USER or ADMIN");
        }
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}
