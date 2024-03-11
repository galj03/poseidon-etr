package poseidon.controllers.world;

import poseidon.DAO._Interfaces.IPlaceDAO;
import poseidon.DAO._Interfaces.IUserDAO;
import poseidon.DAO._Interfaces.IWorldDAO;
import poseidon.DTO.World;
import poseidon.DTO._Interfaces.IUser;
import poseidon.DTO._Interfaces.IWorld;
import poseidon.Exceptions.QueryException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;

@Controller
@PreAuthorize("authentication.principal.username != null")
public class WorldController {

    private final IWorldDAO _worldDAO;
    private final IUserDAO _userDAO;
    private final IPlaceDAO _placeDAO;

    public WorldController(IWorldDAO worldDAO, IUserDAO userDAO, IPlaceDAO placeDAO) {
        _worldDAO = worldDAO;
        _userDAO = userDAO;
        _placeDAO = placeDAO;
    }

    @GetMapping("/new-world")
    public String newWorld(Model model) {
        return "world/new";
    }

    @PostMapping("/new-world/process")
    public String processNewWorld(
            @RequestParam("worldName") String worldName,
            @RequestParam(value = "description", defaultValue = "") String description,
            Model model) {
        if (worldName == null || worldName.equals("")) {
            model.addAttribute("error", "World name must be given to create world!");
            return "main/error";
        }

        IUser currentUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            currentUser = _userDAO.getBySearchText(currentUserName);
        } else {
            return "redirect:/auth";
        }

        try {
            Iterable<IWorld> worlds = _worldDAO.getAllByOwner(currentUser);
            for (IWorld w : worlds) {
                if (Objects.equals(w.getName(), worldName)) {
                    model.addAttribute("error", "World name already in use!");
                    return "main/error";
                }
            }
        } catch (QueryException e) {
        }

        IWorld world = new World(_userDAO, _placeDAO)
                .setName(worldName)
                .setDescription(description)
                .setOwner(currentUser);

        try {
            _worldDAO.save(world);
        } catch (QueryException e) {
            model.addAttribute("error", "Could not save new world into database.");
            return "main/error";
        }

        return "redirect:/";
    }

    @GetMapping("/edit-world")
    public String editWorld(@RequestParam("id") Integer worldId, Model model) {
        if (worldId == null || worldId < 0) {
            model.addAttribute("error", "World id is invalid!");
            return "main/error";
        }

        IWorld world;
        try {
            world = _worldDAO.getById(worldId);
        } catch (QueryException e) {
            model.addAttribute("error", "Could not find world with given id in the database.");
            return "main/error";
        }
        if (world == null) {
            model.addAttribute("error", "Could not find world with given id in the database.");
            return "main/error";
        }

        model.addAttribute("world", world);
        return "world/edit";
    }

    @PostMapping("/edit-world/process")
    public String processEditWorld(
            @RequestParam("worldId") Integer worldId,
            @RequestParam("worldName") String worldName,
            @RequestParam(value = "description", defaultValue = "") String description,
            Model model) {
        IUser currentUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            currentUser = _userDAO.getBySearchText(currentUserName);
        } else {
            return "redirect:/auth";
        }

        if (worldId == null || worldId < 0) {
            model.addAttribute("error", "World id is invalid!");
            return "main/error";
        }

        if (worldName == null || worldName.equals("")) {
            model.addAttribute("error", "World name must be given to perform update!");
            return "main/error";
        }

        IWorld world;

        try {
            world = _worldDAO.getById(worldId);
        } catch (QueryException e) {
            model.addAttribute("error", "Could not find world with given id in the database.");
            return "main/error";
        }
        if (world == null) {
            model.addAttribute("error", "Could not find world with given id in the database.");
            return "main/error";
        }
        model.addAttribute("world", world);

        try {
            Iterable<IWorld> worlds = _worldDAO.getAllByOwner(currentUser);
            for (IWorld w : worlds) {
                if (Objects.equals(w.getName(), worldName) && !Objects.equals(w.getId(), worldId)) {
                    model.addAttribute("error", "World name already in use!");
                    return "main/error";
                }
            }
        } catch (QueryException e) {
        }

        world = new World(_userDAO, _placeDAO)
                .setId(worldId)
                .setName(worldName)
                .setDescription(description)
                .setOwner(currentUser);

        try {
            _worldDAO.save(world);
        } catch (QueryException e) {
            model.addAttribute("error", "Could not save edited world into database.");
            return "main/error";
        }

        return "redirect:/";
    }

    @PostMapping("/invite")
    public String invite(
            @RequestParam("id") Integer worldId,
            @RequestParam("username") String username,
            Model model) {
        IUser currentUser = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            currentUser = _userDAO.getBySearchText(currentUserName);
        }
        IUser existingUserByUsername;

        if (worldId == null || worldId < 0) {
            model.addAttribute("error", "World id is invalid!");
            return "main/error";
        }

        if (username == null || username.equals("")) {
            model.addAttribute("error", "Username must be given for invitation!");
            return "main/error";
        }

        IWorld world;

        try {
            world = _worldDAO.getById(worldId);
        } catch (QueryException e) {
            model.addAttribute("error", "Could not find world with given id in the database.");
            return "main/error";
        }
        if (world == null) {
            model.addAttribute("error", "Could not find world with given id in the database.");
            return "main/error";
        }
        model.addAttribute("world", world);

        try {
            existingUserByUsername = _userDAO.getBySearchText(username);
        } catch (QueryException e) {
            existingUserByUsername = null;
        }

        if (existingUserByUsername == null || Objects.equals(currentUser.getUsername(), username)) {
            model.addAttribute("error", "Username is invalid!");
            return "main/error";
        }

        IWorld currentWorld = _worldDAO.getById(worldId);
        List<IUser> invitedUsers = _userDAO.getByInvitedWorld(currentWorld);
        for (IUser u :
                invitedUsers) {
            if (Objects.equals(u.getUsername(), username)) {
                model.addAttribute("error", "The given user is already invited!");
                return "main/error";
            }
        }
        List<IUser> joined = _userDAO.getByWorldJoined(currentWorld);
        for (IUser u :
                joined) {
            if (Objects.equals(u.getUsername(), username)) {
                model.addAttribute("error", "The given user is already in the world!");
                return "main/error";
            }
        }

        _worldDAO.inviteUser(currentWorld, existingUserByUsername);
        return "redirect:/edit-world?id=" + worldId.toString();
    }

    @PostMapping("/remove-from-world")
    public String removeFromWorld(
            @RequestParam("worldId") Integer worldId,
            @RequestParam("userId") Integer userId,
            Model model) {
        if (worldId == null || worldId < 0) {
            model.addAttribute("error", "Cannot remove from world: World id is invalid!");
            return "main/error";
        }
        if (userId == null || userId < 0) {
            model.addAttribute("error", "Cannot remove from world: User id is invalid!");
            return "main/error";
        }

        IWorld world;

        try {
            world = _worldDAO.getById(worldId);
        } catch (QueryException e) {
            model.addAttribute("error", "Could not find world with given id in the database.");
            return "main/error";
        }
        if (world == null) {
            model.addAttribute("error", "Could not find world with given id in the database.");
            return "main/error";
        }
        model.addAttribute("world", world);

        IUser user;

        try {
            user = _userDAO.getById(userId);
        } catch (QueryException e) {
            model.addAttribute("error", "Could not find user with given id in the database.");
            return "main/error";
        }
        if (user == null) {
            model.addAttribute("error", "Could not find user with given id in the database.");
            return "main/error";
        }

        boolean isFound = false;
        List<IUser> users = world.getJoinedUsers();
        for (IUser u :
                users) {
            if (Objects.equals(u.getId(), user.getId())) {
                isFound = true;
                break;
            }
        }

        users = world.getInvitedUsers();
        for (IUser u :
                users) {
            if (!isFound && Objects.equals(u.getId(), user.getId())) {
                isFound = true;
                break;
            }
        }

        if (!isFound) {
            model.addAttribute("error", "The given user has not been invited to, or has not joined this world!");
            return "main/error";
        }

        _worldDAO.removeUser(world, user);

        return "redirect:/edit-world?id=" + worldId.toString();
    }

    @GetMapping("/delete-world")
    public String deleteWorld(@RequestParam("id") Integer worldId, Model model) {
        if (worldId == null || worldId < 0) {
            model.addAttribute("error", "Cannot remove world: World id is invalid!");
            return "main/error";
        }

        IWorld world;

        try {
            world = _worldDAO.getById(worldId);
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot remove world: Could not find world with given id in the database.");
            return "main/error";
        }
        if (world == null) {
            model.addAttribute("error", "Cannot remove world: Could not find world with given id in the database.");
            return "main/error";
        }

        try {
            _worldDAO.remove(world);
        } catch (QueryException e) {
            model.addAttribute("error", "Could not remove world from database.");
            return "main/error";
        }

        return "redirect:/";
    }
}
