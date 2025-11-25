        # Advanced Spring Boot Interview Questions — Security (Spring Security & OAuth2)

        ### 1. How does Spring Security integrate with Spring Boot?
        - Auto-configures authentication/authorization filters by default.

        ### 2. Explain the security filter chain.
        - Ordered filters manage authentication and authorization via `SecurityFilterChain`.

        ### 3. How does authentication work?
        - `AuthenticationManager` validates credentials and stores result in `SecurityContextHolder`.

        ### 4. Common security annotations?
        - `@EnableWebSecurity`, `@PreAuthorize`, `@Secured`, and `@PostAuthorize`.

        ### 5. How to customize authentication?
        - Implement `UserDetailsService` and provide custom `PasswordEncoder`.

        ### 6. JWT authentication flow?
        - User logs in → server issues JWT → client sends JWT in headers → server validates signature and roles.

        ### 7. OAuth2 in Spring Boot?
---
### 🔐 OAuth2 (In Brief)
OAuth2 is an authorization framework that allows applications to access user data on another service **without sharing credentials**.  
It issues **access tokens** after user consent, which clients use to call protected APIs securely.

**Example:**  
### 🔄 OAuth2 Authorization Flow (Step-by-Step)

1. **User clicks “Login with Google”**  
   The user chooses to sign in to your application using their Google account.

2. **App redirects to Google’s Authorization Server**  
   Your app sends the user to Google’s OAuth2 consent page with parameters like:
    - `client_id` (your app ID)
    - `redirect_uri` (where Google should send the user back)
    - `scope` (what data you’re asking for, e.g., `profile`, `email`)
    - `response_type=code`

3. **User grants permission**  
   Google authenticates the user and shows a consent screen asking if they want to allow your app access to their profile.

4. **Google redirects back with an Authorization Code**  
   If the user agrees, Google redirects to your app’s callback URL, e.g.: https://yourapp.com/oauth2/callback?code=ABC123
5. **App exchanges the Authorization Code for an Access Token**  
      Your app sends the code (along with its `client_id`, `client_secret`, and `redirect_uri`) to Google’s **token endpoint**.  
      Google verifies the code and responds with:
- `access_token` (used to access resources)
- `refresh_token` (optional, to get new access tokens later)

6. **App uses the Access Token to call Google APIs**  
   Example:
```bash```
    GET https://www.googleapis.com/oauth2/v3/userinfo
    Authorization: Bearer ACCESS_TOKEN
    
    Google verifies the token and returns the user’s profile info. 
7. User is logged in 
   Your app creates a session for the user using the returned data (like email or name) — no password ever exchanged.

**Key Components:** Resource Owner (user), Client (app), Authorization Server, Resource Server.

---
        ### 8. Difference between OAuth2 roles?
- **Client**: app requesting access.
- **Resource Owner**: user.
- **Resource Server**: API provider.

        ### 9. How to secure REST APIs?
        - JWT/OAuth2, HTTPS, and CSRF protection.
- Use rate limiting and input validation.

        ### 10. How to enable method-level security?
        - `@EnableGlobalMethodSecurity(prePostEnabled = true)` + annotations like `@PreAuthorize("hasRole('ADMIN')")`.
