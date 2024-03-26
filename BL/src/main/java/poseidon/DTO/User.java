package poseidon.DTO;

import poseidon.Constants;
import poseidon.DTO._Interfaces.IUser;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import poseidon.UserRoles;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Data transfer object to represent the user model.
 */
public class User implements IUser {
    //region Properties
    private String _psCode;
    private String _name;
    private String _email;
    private String _passwordHash;
    private Integer _szakId;
    private UserRoles _role;
    private Integer _kezdesEve;
    private Integer _vegzesEve;
    //endregion

    //region Getters
    @Override
    public String getPsCode() {
        return _psCode;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getEmail() {
        return _email;
    }

    @Override
    public Integer getSzakId() {
        return _szakId;
    }

    @Override
    public UserRoles getRole() {
        return _role;
    }

    @Override
    public Integer getKezdesEve() {
        return _kezdesEve;
    }

    @Override
    public Integer getVegzesEve() {
        return _vegzesEve;
    }
    //endregion

    //region Setters
    @Override
    public IUser setPsCode(String id) throws IllegalOperationException {
        if (_psCode != null) throw new IllegalOperationException("Id cannot be changed");
        _psCode = id;
        return this;
    }

    @Override
    public IUser setName(String name) throws ArgumentNullException {
        if (name == null || name.isEmpty()) throw new ArgumentNullException("name");

        _name = name.trim();
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
    public IUser setSzakId(Integer szakId) throws IllegalArgumentException {
        //TODO: check, hogy van-e ilyen?
        //nem, az itt nem scope imo, ez egy modell
        if (szakId == null || szakId < 0) throw new IllegalArgumentException("szakId");

        _szakId = szakId;
        return this;
    }

    @Override
    public IUser setRole(UserRoles role) {
        _role = role;
        return this;
    }

    @Override
    public IUser setKezdesEve(Integer kezdesEve) throws IllegalArgumentException {
        if (kezdesEve == null || kezdesEve < 0) throw new IllegalArgumentException("kezdesEve");

        _kezdesEve = kezdesEve;
        return this;
    }

    @Override
    public IUser setVegzesEve(Integer vegzesEve) throws IllegalArgumentException {
        if (vegzesEve == null || vegzesEve < 0) throw new IllegalArgumentException("vegzesEve");

        _vegzesEve = vegzesEve;
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
        return _psCode;
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
