import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean deleteAccount(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                userRepository.delete(user);
                return true;
            }
        }
        return false;
    }

    public boolean editUsername(String currentUsername, String newUsername) {
        Optional<User> existingUserOpt = userRepository.findByUsername(currentUsername);
        if (existingUserOpt.isPresent() && userRepository.findByUsername(newUsername).isEmpty()) {
            User user = existingUserOpt.get();
            user.setUsername(newUsername);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
