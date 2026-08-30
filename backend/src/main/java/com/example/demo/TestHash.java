import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.matches("password", "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HCGaWnC.n2mY0kYQh62Gq"));
        System.out.println(encoder.encode("password"));
    }
}
