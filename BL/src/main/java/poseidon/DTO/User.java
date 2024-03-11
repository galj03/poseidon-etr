package poseidon.DTO;

import poseidon.Constants;
import poseidon.DTO._Interfaces.IUser;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Data transfer object to represent the user model.
 */
public class User implements IUser {
    //region Properties
    private Integer _id;
    private String _username;
    private String _email;
    private String _passwordHash;
    private String _pfpPath;
    //endregion

    //region Getters
    @Override
    public Integer getId() {
        return _id;
    }

    @Override
    public String getEmail() {
        return _email;
    }

    @Override
    public String getPfpPath() {
        return _pfpPath;
    }
    //endregion

    //region Setters
    @Override
    public IUser setId(int id) throws IllegalOperationException {
        if (_id != null) throw new IllegalOperationException("Id cannot be changed");
        _id = id;
        return this;
    }

    @Override
    public IUser setUsername(String username) throws ArgumentNullException {
        if (username == null || username.isEmpty()) throw new ArgumentNullException("username");

        _username = username.trim();
        return this;
    }

    @Override
    public IUser setEmail(String email) throws IllegalArgumentException {
        if (email == null) throw new ArgumentNullException("email");
        if (!Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matcher(email.trim()).matches())
            throw new IllegalArgumentException("Email is invalid: " + email);

        _email = email.trim();
        return this;
    }

    @Override
    public IUser setPassword(String password) throws ArgumentNullException {
        if (password == null || password.isEmpty()) throw new ArgumentNullException("password");

        _passwordHash = password.trim();
        return this;
    }

    @Override
    public IUser setPfpPath(String path) {
        _pfpPath = path == null || path.isEmpty() ? null : path.trim();
        return this;
    }
    //endregion

    //region UserDetails members

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(Constants.USER_ROLE));
        return authorities;
    }

    /**
     * Returns the password used to authenticate the user.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return _passwordHash;
    }

    @Override
    public String getUsername() {
        return _username;
    }

    /**
     * Indicates whether the user's account has expired. An expired account cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user's account is valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie non-expired),
     * <code>false</code> if no longer valid (ie expired)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
    //endregion
}
