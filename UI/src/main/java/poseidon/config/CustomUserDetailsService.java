package poseidon.config;

import poseidon.DAO._Interfaces.IUserDAO;
import poseidon.DTO._Interfaces.IUser;
import poseidon.Exceptions.QueryException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
    private IUserDAO _userDAO;

    public CustomUserDetailsService(IUserDAO userDAO) {
        _userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        IUser user;
        try {
            user = _userDAO.getByEmail(username);
        } catch (QueryException e) {
            user = null;
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

}
