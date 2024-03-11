package poseidon.controllers.world;

import poseidon.DAO._Interfaces.IAppearanceDAO;
import poseidon.DAO._Interfaces.IPlaceDAO;
import poseidon.DAO._Interfaces.IUserDAO;
import poseidon.DAO._Interfaces.IWorldDAO;
import poseidon.DTO.Appearance;
import poseidon.DTO.Place;
import poseidon.DTO._Interfaces.IAppearance;
import poseidon.DTO._Interfaces.IPlace;
import poseidon.DTO._Interfaces.IUser;
import poseidon.DTO._Interfaces.IWorld;
import poseidon.Exceptions.QueryException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Controller
@PreAuthorize("authentication.principal.username != null")
public class PlaceController {

    private IPlaceDAO _placeDAO;
    private IUserDAO _userDAO;
    private IWorldDAO _worldDAO;
    private IAppearanceDAO _appearanceDAO;

    //public PlaceController(IPlaceDAO placeDAO, IUserDAO userDAO, IWorldDAO worldDAO){


    public PlaceController(IPlaceDAO placeDAO, IUserDAO userDAO, IWorldDAO worldDAO, IAppearanceDAO appearanceDAO) {
        _placeDAO = placeDAO;
        _userDAO = userDAO;
        _worldDAO = worldDAO;
        _appearanceDAO = appearanceDAO;
    }

    @GetMapping("/new-place")
    public String newPlace(@RequestParam("worldId") Integer worldId,
                           @RequestParam("isOwnWorld") boolean isOwnWorld,
                           Model model) {
        if (worldId == null || worldId < 0) {
            model.addAttribute("error", "Cannot initiate add new place: \nWorld id is invalid!");
            return "main/error";
        }
        IWorld world;
        try {
            world = _worldDAO.getById(worldId);
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot initiate add new place: \nCould not find world with given id in the database.");
            return "main/error";
        }
        if (world == null) {
            model.addAttribute("error", "Cannot initiate add new place: \nCould not find world with given id in the database.");
            return "main/error";
        }

        List<IPlace> places = _placeDAO.getAllByWorld(world, !isOwnWorld);
        model.addAttribute("worldId", worldId);
        model.addAttribute("places", places);
        model.addAttribute("worldDAO", _worldDAO);

        return "world/newplace";
    }

    @GetMapping("/edit-place")
    public String editPlace(@RequestParam("placeId") Integer placeId,
                            Model model) {
        if (placeId == null || placeId < 0) {
            model.addAttribute("error", "Cannot initiate edit place: \nPlace id is invalid!");
            return "main/error";
        }
        IPlace place;
        try {
            place = _placeDAO.getById(placeId);
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot initiate edit place: \nCould not find place with given id in the database.");
            return "main/error";
        }
        if (place == null) {
            model.addAttribute("error", "Cannot initiate edit place: \nCould not find place with given id in the database.");
            return "main/error";
        }

        IWorld world;
        try {
            world = _worldDAO.getById(place.getWorldId());
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot initiate edit place: \nCould not find world with given id in the database.");
            return "main/error";
        }
        if (world == null) {
            model.addAttribute("error", "Cannot initiate edit place: \nCould not find world with given id in the database.");
            return "main/error";
        }

        List<IPlace> places = null;
        if (place.getParent() != null) {
            places = _placeDAO.getAllByWorld(world, false);
            //places.addAll(_placeDAO.getAllByWorld(world,true));
        }
        List<IPlace> children = _placeDAO.getAllByParent(place, false);
        //children.addAll(_placeDAO.getAllByParent(place,false));
        model.addAttribute("appearanceDAO", _appearanceDAO);
        model.addAttribute("places", places);
        model.addAttribute("children", children);
        model.addAttribute("currentPlace", place);
        model.addAttribute("world", world);
        model.addAttribute("worldId", place.getWorldId());
        model.addAttribute("worldDAO", _worldDAO);
        return "world/editplace";
    }

    @PostMapping("/edit-place/process")
    public String processNewPlace(@RequestParam("plname") String placeName,
                                  @RequestParam("pltype") String placeType,
                                  @RequestParam("desc") String description,
                                  @RequestParam("notes") String notes,
                                  @RequestParam(value = "plparent") Integer placeParentId,
                                  @RequestParam("worldId") Integer worldId,
                                  Model model) {
        if (placeName == null || placeName.equals("")) {
            model.addAttribute("error", "Cannot perform save place: \nPlace name must be given to perform update!");
            return "main/error";
        }

        if (placeType == null || placeType.equals("")) {
            model.addAttribute("error", "Cannot perform save place: \nPlace type must be given to perform update!");
            return "main/error";
        }

        if (worldId == null || worldId < 0) {
            model.addAttribute("error", "Cannot perform save place: \nWorld id is invalid!");
            return "main/error";
        }
        IWorld world;
        try {
            world = _worldDAO.getById(worldId);
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot perform save place: \nCould not find world with given id in the database.");
            return "main/error";
        }
        if (world == null) {
            model.addAttribute("error", "Cannot perform save place: \nCould not find world with given id in the database.");
            return "main/error";
        }

        IPlace place = new Place(_placeDAO);
        place.setWorldId(worldId);

        IUser currentUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            currentUser = _userDAO.getBySearchText(currentUserName);
        } else {
            return "redirect:/auth";
        }

        IPlace parent;
        if (_placeDAO.getRootByWorld(world) == null) {
            parent = null;
        } else {
            parent = _placeDAO.getById(placeParentId);
        }

        place.setName(placeName);
        place.setType(placeType);
        place.setParent(parent);
        place.setDescription(description);
        place.setNotes(notes);

        try {
            _placeDAO.save(place);
        } catch (QueryException e) {
            model.addAttribute("new_place_error", "Could not save the changes into database.");
            return "redirect:/new-place?isOwnWorld=true&worldId=" + worldId;
        }
        return "redirect:/place?placeId=" + world.getRootPlace().getId().toString();
    }

    @PostMapping("/edit-place/processing")
    public String processEditPlace(@RequestParam("plname") String placeName,
                                   @RequestParam("pltype") String placeType,
                                   @RequestParam("desc") String description,
                                   @RequestParam("notes") String notes,
                                   @RequestParam(value = "plparent") Integer placeParentId,
                                   @RequestParam("placeId") Integer placeId,
                                   Model model) {
        if (placeName == null || placeName.equals("")) {
            model.addAttribute("error", "Cannot perform save place: \nPlace name must be given to perform update!");
            return "main/error";
        }

        if (placeType == null || placeType.equals("")) {
            model.addAttribute("error", "Cannot perform save place: \nPlace type must be given to perform update!");
            return "main/error";
        }

        if (placeId == null || placeId < 0) {
            model.addAttribute("error", "Cannot perform save place: \nPlace id is invalid!");
            return "main/error";
        }
        IPlace place;
        try {
            place = _placeDAO.getById(placeId);
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot perform save place: \nCould not find place with given id in the database.");
            return "main/error";
        }
        if (place == null) {
            model.addAttribute("error", "Cannot perform save place: \nCould not find place with given id in the database.");
            return "main/error";
        }

        IWorld world;
        try {
            world = _worldDAO.getById(place.getWorldId());
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot perform save place: \nCould not find world with given id in the database.");
            return "main/error";
        }
        if (world == null) {
            model.addAttribute("error", "Cannot perform save place: \nCould not find world with given id in the database.");
            return "main/error";
        }


        //List<IPlace> places = _placeDAO.getAllByParent(place.getParent(), true);
        model.addAttribute("world", world);
        //model.addAttribute("places", places);
        model.addAttribute("worldDAO", _worldDAO);

        IUser currentUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            currentUser = _userDAO.getBySearchText(currentUserName);
        } else {
            return "redirect:/auth";
        }

        IPlace parent = null;
        try {
            parent = _placeDAO.getById(placeId);
        } catch (QueryException e) {
        }
        if (placeName.isEmpty()) {
            placeName = place.getName();
        }
        if (placeType.isEmpty()) {
            placeType = place.getType();
        }
        if (description.isEmpty()) {
            description = place.getDescription();
        }
        if (notes.isEmpty()) {
            notes = place.getNotes();
        }

        place.setName(placeName);
        place.setType(placeType);
        if (world.getRootPlace() != place) {
            place.setParent(parent);
        }
        place.setDescription(description);
        place.setNotes(notes);

        try {
            _placeDAO.save(place);
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot perform save place: \nCould not save the changes into database.");
            return "main/error";
        }
        return "redirect:/place?placeId=" + world.getRootPlace().getId().toString();
    }

    @PostMapping("/edit-place/appearance")
    public String appearance(@RequestParam("placeId") Integer placeId,
                             @RequestParam("coord") String cord,
                             @RequestParam("z-axis") Integer zaxis,
                             @RequestParam("childId") Integer childId,
                             Model model) {
        if (placeId == null || placeId < 0) {
            model.addAttribute("error", "Cannot save appearance: \nLocation id is invalid!");
            return "main/error";
        }
        if (cord == null || cord.equals("")) {
            model.addAttribute("error", "Cannot save appearance: \nCoordinates must be given!");
            return "main/error";
        }
        if (childId == null || childId < 0) {
            model.addAttribute("error", "Cannot save appearance: \nPlace id is invalid!");
            return "main/error";
        }
        if (zaxis == null) {
            model.addAttribute("error", "Cannot save appearance: \nZ-axis value must be given!");
            return "main/error";
        }

        IPlace place;
        try {
            place = _placeDAO.getById(placeId);
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot save appearance: \nCould not find place with given id in the database.");
            return "main/error";
        }
        if (place == null) {
            model.addAttribute("error", "Cannot save appearance: \nCould not find place with given id in the database.");
            return "main/error";
        }
        IPlace childPlace;
        try {
            childPlace = _placeDAO.getById(childId);
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot save appearance: \nCould not find place with given id in the database.");
            return "main/error";
        }
        if (childPlace == null) {
            model.addAttribute("error", "Cannot save appearance: \nCould not find place with given id in the database.");
            return "main/error";
        }

        IAppearance appearance = new Appearance(_placeDAO);
        appearance.setPlace(childPlace);
        appearance.setLocation(place);
        try {
            appearance.setCoordinates(cord);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Cannot save appearance: \n" + e.getMessage());
            return "main/error";
        }
        appearance.setZAxis(zaxis);
        _appearanceDAO.save(appearance);
        return "redirect:/edit-place?placeId=" + placeId.toString() + "&isOwnWorld=true";
    }

    @GetMapping("/place")
    public String place(@RequestParam("placeId") Integer placeId, Model model) {
        IUser currentUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            currentUser = _userDAO.getBySearchText(currentUserName);
        } else {
            return "redirect:/auth";
        }

        if (placeId == null || placeId < 0) {
            model.addAttribute("error", "Cannot show place: \nPlace id is invalid!");
            return "main/error";
        }
        IPlace place;
        try {
            place = _placeDAO.getById(placeId);
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot show place: \nCould not find place with given id in the database.");
            return "main/error";
        }
        if (place == null) {
            model.addAttribute("error", "Cannot show place: \nCould not find place with given id in the database.");
            return "main/error";
        }

        List<IPlace> children = _placeDAO.getAllByParent(place, false);
        model.addAttribute("place", place);
        model.addAttribute("user", currentUser);
        model.addAttribute("children", children);
        model.addAttribute("worldDAO", _worldDAO);
        model.addAttribute("placeDAO", _placeDAO);
        return "world/place";
    }

    @GetMapping("/discovered")
    public String discovered(@RequestParam("id") Integer placeId, Model model) {
        if (placeId == null || placeId < 0) {
            model.addAttribute("error", "Cannot toggle discovered property: \nPlace id is invalid!");
            return "main/error";
        }
        IPlace place;
        try {
            place = _placeDAO.getById(placeId);
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot toggle discovered property: \nCould not find place with given id in the database.");
            return "main/error";
        }
        if (place == null) {
            model.addAttribute("error", "Cannot toggle discovered property: \nCould not find place with given id in the database.");
            return "main/error";
        }

        if (place.isDiscovered()) {
            toggleDiscoverForChild(place);
        } else {
            toggleDiscoverForParent(place);
        }
        place.setDiscovered(!place.isDiscovered());
        _placeDAO.save(place);
        return "redirect:/place?placeId=" + placeId.toString();
    }

    private void toggleDiscoverForChild(IPlace place) {
        List<IPlace> children = _placeDAO.getAllByParent(place, true);

        if (children.isEmpty()) {
            return;
        }

        for (IPlace child : children) {
            toggleDiscoverForChild(child);
            child.setDiscovered(false);
            _placeDAO.save(child);
        }
    }

    private void toggleDiscoverForParent(IPlace place) {
        IPlace parent = place.getParent();

        if (parent == null) {
            return;
        }

        toggleDiscoverForParent(parent);
        parent.setDiscovered(true);
        _placeDAO.save(parent);
    }

    @GetMapping("/description")
    public String isDescriptionShown(@RequestParam("id") Integer placeId, Model model) {
        if (placeId == null || placeId < 0) {
            model.addAttribute("error", "Cannot toggle isDescriptionShown property: \nPlace id is invalid!");
            return "main/error";
        }
        IPlace place;
        try {
            place = _placeDAO.getById(placeId);
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot toggle isDescriptionShown property: \nCould not find place with given id in the database.");
            return "main/error";
        }
        if (place == null) {
            model.addAttribute("error", "Cannot toggle isDescriptionShown property: \nCould not find place with given id in the database.");
            return "main/error";
        }

        place.setDescriptionShown(!place.isDescriptionShown());
        _placeDAO.save(place);
        return "redirect:/place?placeId=" + placeId;
    }

    @GetMapping("/delete-place")
    public String deletePlace(@RequestParam("id") Integer placeId, Model model) {
        if (placeId == null || placeId < 0) {
            model.addAttribute("error", "Cannot delete place: \nPlace id is invalid!");
            return "main/error";
        }
        IPlace place;
        try {
            place = _placeDAO.getById(placeId);
        } catch (QueryException e) {
            model.addAttribute("error", "Cannot delete place: \nCould not find place with given id in the database.");
            return "main/error";
        }
        if (place == null) {
            model.addAttribute("error", "Cannot delete place: \nCould not find place with given id in the database.");
            return "main/error";
        }
        //IAppearance appearance = new Appearance(_placeDAO);
        Integer parentId = place.getParent().getId();
        List<IPlace> children = _placeDAO.getAllByParent(_placeDAO.getById(parentId), false);
        //children.addAll(_placeDAO.getAllByParent(place, false));
        List<IAppearance> appearances = _appearanceDAO.getAllByLocation(_placeDAO.getById(parentId), false);

        if (children != null && !children.isEmpty()) {
            for (IPlace child : children) {
                if (appearances != null && !appearances.isEmpty()) {
                    for (IAppearance appearance : appearances) {
                        if (Objects.equals(appearance.getPlace().getId(), child.getId())) {
                            _appearanceDAO.remove(appearance);
                        }
                    }
                }
                _placeDAO.remove(child);
            }
        }
        try {
            _placeDAO.remove(place);
        } catch (QueryException e) {
            model.addAttribute("edit_place_error", "Could not remove world from database.");
            return "redirect:/edit-place?placeId=" + parentId.toString() + "&isOwnWorld=true";
        }

        return "redirect:/edit-place?placeId=" + parentId.toString() + "&isOwnWorld=true";
    }
}
