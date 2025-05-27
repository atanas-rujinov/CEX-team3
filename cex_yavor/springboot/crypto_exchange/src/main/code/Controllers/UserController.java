import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/deleteAccount")
    public ResponseEntity<String> deleteAccount(@RequestBody DeleteAccountRequest request) {
        boolean success = userService.deleteAccount(request.getUsername(), request.getPassword());
        if (success) {
            return ResponseEntity.ok("Account deleted.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }
    }

    @PostMapping("/editUsername")
    public ResponseEntity<String> editUsername(@RequestBody EditUsernameRequest request) {
        boolean success = userService.editUsername(request.getCurrentUsername(), request.getNewUsername());
        if (success) {
            return ResponseEntity.ok("Username updated.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username change failed.");
        }
    }

    @PostMapping("/banAccount")
    public ResponseEntity<String> banAccount(@RequestBody BanAccountRequest request) {
        boolean success = userService.banAccount(request.getUsername());
        if (success) {
            return ResponseEntity.ok("Account banned.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

}
