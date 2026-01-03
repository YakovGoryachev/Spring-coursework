package com.goryachev.music_service.Controllers;

import com.goryachev.music_service.DTO.*;
import com.goryachev.music_service.Pojo.*;
import com.goryachev.music_service.Repository.*;
import com.goryachev.music_service.Service.*;
import com.goryachev.music_service.Utility.AudioUtils;
import com.mpatric.mp3agic.Mp3File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final TrackService trackService;
    private final ArtistService artistService;
    private final GroupService groupService;
    private final AlbumService albumService;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;
    private final GroupRepository groupRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    @Autowired
    public AdminController(TrackService trackService, ArtistService artistService,
                          GroupService groupService, AlbumService albumService,
                          UserService userService, FileStorageService fileStorageService,
                          TrackRepository trackRepository, ArtistRepository artistRepository,
                          GroupRepository groupRepository, AlbumRepository albumRepository,
                          UserRepository userRepository) {
        this.trackService = trackService;
        this.artistService = artistService;
        this.groupService = groupService;
        this.albumService = albumService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
        this.trackRepository = trackRepository;
        this.artistRepository = artistRepository;
        this.groupRepository = groupRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
    }

    @GetMapping({"", "/"})
    public String adminPanel(Model model) {
        return "admin-panel";
    }

    @GetMapping("/tracks")
    public String tracks(@RequestParam(required = false) String filterBy,
                        @RequestParam(required = false) String searchQuery,
                        Model model) {
        List<Track> allTracks = trackRepository.findAll();
        List<TrackDto> tracks = allTracks.stream()
                .map(trackService::mapToDto)
                .collect(Collectors.toList());

        if (searchQuery != null && !searchQuery.trim().isEmpty() && filterBy != null) {
            String query = searchQuery.toLowerCase().trim();
            tracks = tracks.stream()
                    .filter(track -> {
                        switch (filterBy) {
                            case "title":
                                return track.getName() != null && track.getName().toLowerCase().contains(query);
                            case "artist":
                                return track.getArtistName() != null && track.getArtistName().toLowerCase().contains(query);
                            case "album":
                                return track.getAlbumName() != null && track.getAlbumName().toLowerCase().contains(query);
                            case "group":
                                return track.getGroupName() != null && track.getGroupName().toLowerCase().contains(query);
                            case "genre":
                                return track.getGenre() != null && track.getGenre().toLowerCase().contains(query);
                            default:
                                return true;
                        }
                    })
                    .collect(Collectors.toList());
        }

        model.addAttribute("tracks", tracks);
        model.addAttribute("totalTracks", tracks.size());
        model.addAttribute("filterBy", filterBy);
        model.addAttribute("searchQuery", searchQuery);
        return "admin-tracks";
    }

    @PostMapping("/tracks/add")
    public String addTrack(@RequestParam String title,
                          @RequestParam String artist,
                          @RequestParam(required = false) String album,
                          @RequestParam(required = false) String group,
                          @RequestParam String genre,
                          @RequestParam(required = false) Integer duration,
                          @RequestParam MultipartFile audioFile,
                          @RequestParam(required = false) MultipartFile coverFile,
                          RedirectAttributes redirectAttributes) {
        try {
            Artist artistEntity = artistRepository.findFirstByNameIgnoreCase(artist);

            if (artistEntity == null) {
                ArtistDto artistDto = new ArtistDto();
                artistDto.setName(artist);
                artistEntity = artistService.createArtist(artistDto);
            }
            final Artist finalArtistEntity = artistEntity;

            String groupName = group != null && !group.trim().isEmpty() ? group : artist;
            Group groupEntity = groupRepository.findFirstByNameIgnoreCase(groupName);

            if (groupEntity == null) {
                GroupDto groupDto = new GroupDto();
                groupDto.setName(groupName);
                groupEntity = groupService.createGroup(groupDto);
                groupService.addArtistToGroup(groupEntity.getId(), finalArtistEntity.getId());
            } else {
                boolean artistInGroup = false;
                if (finalArtistEntity.getGroups() != null) {
                    final Group finalGroupEntity = groupEntity;
                    artistInGroup = finalArtistEntity.getGroups().stream()
                            .anyMatch(g -> g.getId() == finalGroupEntity.getId());
                }
                if (!artistInGroup) {
                    groupService.addArtistToGroup(groupEntity.getId(), finalArtistEntity.getId());
                }
            }
            final Group finalGroupEntity = groupEntity;

            String albumName = album != null && !album.trim().isEmpty() ? album : "Сингл";

            Album finalAlbum = new Album();
            if (albumName.equals("Сингл")){
                Album al = albumRepository.findByNameAndGroupId(albumName, finalGroupEntity.getId());
                if (al == null){
                    AlbumDto albumDto = new AlbumDto();
                    albumDto.setName(albumName);
                    albumDto.setGroupId(finalGroupEntity.getId());
                    finalAlbum = albumService.createAlbum(albumDto);
                }
                else{
                    finalAlbum = al;
                }
            }
            else {
                //was
                Album albumEntity = albumRepository.findByNameAndGroupId(albumName, finalGroupEntity.getId());
                if (albumEntity == null) {
                    AlbumDto albumDto = new AlbumDto();
                    albumDto.setName(albumName);
                    albumDto.setGroupId(finalGroupEntity.getId());
                    albumEntity = albumService.createAlbum(albumDto);
                    finalAlbum = albumEntity;
                }
                else finalAlbum = albumEntity;
            }

            Integer fileDuration = AudioUtils.getMp3DurationSeconds(audioFile);

//            TrackDto trackDto = new TrackDto();
//            trackDto.setName(title);
//            trackDto.setGenre(genre);
//            if (duration != null)
//                trackDto.setDuration(duration);
//            else
//                trackDto.setDuration(fileDuration);
//            trackDto.setAlbumName(albumEntity.getName()); //problem
//            trackDto.setArtistName(finalArtistEntity.getName());
//            trackDto.setGroupName(finalGroupEntity.getName());
//
//            String audioPath = "./uploads/" + fileStorageService.storeAudio(audioFile);
//            trackDto.setFilePath(audioPath);
//
//            if (coverFile != null && !coverFile.isEmpty()) {
//                String avatarPath = "/uploads/" + fileStorageService.storeAvatar(coverFile, "track");
//                trackDto.setAvatarPath(avatarPath);
//            }
//
//            trackService.createTrack(trackDto);
            ObjectTrackDto tDto = new ObjectTrackDto();
            tDto.setName(title);
            tDto.setGenre(genre);
            if (duration != null)
                tDto.setDuration(duration);
            else
                tDto.setDuration(fileDuration);
            tDto.setAlbum(finalAlbum);
            tDto.setArtist(finalArtistEntity);
            tDto.setGroup(finalGroupEntity);

            String audioPath = "./uploads/" + fileStorageService.storeAudio(audioFile);
            tDto.setFilePath(audioPath);

            if (coverFile != null && !coverFile.isEmpty()) {
                String avatarPath = "/uploads/" + fileStorageService.storeAvatar(coverFile, "track");
                tDto.setAvatarPath(avatarPath);
            }
            trackService.createWithObjectsTrack(tDto);

            redirectAttributes.addFlashAttribute("success", "Трек успешно добавлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при добавлении трека: " + e.getMessage());
        }
        return "redirect:/admin/tracks";
    }

    @GetMapping("/tracks/edit/{id}")
    public String editTrackForm(@PathVariable int id, Model model) {
        Track track = trackService.findById(id);
        TrackDto trackDto = trackService.mapToDto(track);
        model.addAttribute("track", trackDto);
        return "admin-track-edit";
    }

    @PostMapping("/tracks/edit/{id}")
    public String updateTrack(@PathVariable int id,
                             @RequestParam String title,
                             @RequestParam(required = false) String artist,
                             @RequestParam(required = false) String album,
                             @RequestParam(required = false) String group,
                             @RequestParam(required = false) String genre,
                             @RequestParam(required = false) Integer duration,
                             @RequestParam(required = false) MultipartFile audioFile,
                             @RequestParam(required = false) MultipartFile coverFile,
                             RedirectAttributes redirectAttributes) {
        try {
            TrackDto trackDto = trackService.mapToDto(trackService.findById(id));
            trackDto.setName(title);
            if (genre != null) trackDto.setGenre(genre);
            if (duration != null) trackDto.setDuration(duration);
            if (album != null) trackDto.setAlbumName(album);
            if (artist != null) trackDto.setArtistName(artist);
            if (group != null) trackDto.setGroupName(group);

            if (audioFile != null && !audioFile.isEmpty()) {
                String audioPath = "./uploads/" + fileStorageService.storeAudio(audioFile);
                trackDto.setFilePath(audioPath);
            }
            if (coverFile != null && !coverFile.isEmpty()) {
                String avatarPath = "/uploads/" + fileStorageService.storeAvatar(coverFile, "track");
                trackDto.setAvatarPath(avatarPath);
            }

            trackService.updateTrack(trackDto);
            redirectAttributes.addFlashAttribute("success", "Трек успешно обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении трека: " + e.getMessage());
        }
        return "redirect:/admin/tracks";
    }

    @PostMapping("/tracks/delete/{id}")
    public String deleteTrack(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            trackService.deleteTrack(id);
            redirectAttributes.addFlashAttribute("success", "Трек успешно удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении трека: " + e.getMessage());
        }
        return "redirect:/admin/tracks";
    }

    @GetMapping("/artists")
    public String artists(@RequestParam(required = false) String searchQuery, Model model) {
        List<Artist> allArtists = artistRepository.findAll();
        List<ArtistDto> artists = allArtists.stream()
                .map(artistService::mapToDto)
                .collect(Collectors.toList());

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String query = searchQuery.toLowerCase().trim();
            artists = artists.stream()
                    .filter(artist -> artist.getName() != null && artist.getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        model.addAttribute("artists", artists);
        model.addAttribute("searchQuery", searchQuery);
        return "admin-artists";
    }

    @PostMapping("/artists/add")
    public String addArtist(@RequestParam String name,
                           @RequestParam(required = false) String biography,
                           @RequestParam(required = false) MultipartFile avatarFile,
                           RedirectAttributes redirectAttributes) {
        try {
            ArtistDto dto = new ArtistDto();
            dto.setName(name);
            dto.setBiography(biography);
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarPath = fileStorageService.storeAvatar(avatarFile, "artist");
                dto.setAvatarPath(avatarPath);
            }
            artistService.createArtist(dto);
            redirectAttributes.addFlashAttribute("success", "Артист успешно добавлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при добавлении артиста: " + e.getMessage());
        }
        return "redirect:/admin/artists";
    }

    @GetMapping("/artists/edit/{id}")
    public String editArtistForm(@PathVariable int id, Model model) {
        Artist artist = artistService.findById(id);
        ArtistDto artistDto = artistService.mapToDto(artist);
        model.addAttribute("artist", artistDto);
        return "admin-artist-edit";
    }

    @PostMapping("/artists/edit/{id}")
    public String updateArtist(@PathVariable int id,
                              @RequestParam String name,
                              @RequestParam(required = false) String biography,
                              @RequestParam(required = false) MultipartFile avatarFile,
                              RedirectAttributes redirectAttributes) {
        try {
            ArtistDto dto = artistService.mapToDto(artistService.findById(id));
            dto.setName(name);
            dto.setBiography(biography);
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarPath = fileStorageService.storeAvatar(avatarFile, "artist");
                dto.setAvatarPath(avatarPath);
            }
            artistService.updateArtist(id, dto);
            redirectAttributes.addFlashAttribute("success", "Артист успешно обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении артиста: " + e.getMessage());
        }
        return "redirect:/admin/artists";
    }

    @PostMapping("/artists/delete/{id}")
    public String deleteArtist(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            artistService.deleteArtist(id);
            redirectAttributes.addFlashAttribute("success", "Артист успешно удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении артиста: " + e.getMessage());
        }
        return "redirect:/admin/artists";
    }

    @GetMapping("/groups")
    public String groups(@RequestParam(required = false) String searchQuery, Model model) {
        List<Group> allGroups = groupRepository.findAll();
        List<GroupDto> groups = allGroups.stream()
                .map(groupService::mapToDto)
                .collect(Collectors.toList());

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String query = searchQuery.toLowerCase().trim();
            groups = groups.stream()
                    .filter(group -> group.getName() != null && group.getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        model.addAttribute("groups", groups);
        model.addAttribute("searchQuery", searchQuery);
        return "admin-groups";
    }

    @PostMapping("/groups/add")
    public String addGroup(@RequestParam String name,
                          @RequestParam(required = false) MultipartFile avatarFile,
                          RedirectAttributes redirectAttributes) {
        try {
            GroupDto dto = new GroupDto();
            dto.setName(name);
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarPath = fileStorageService.storeAvatar(avatarFile, "group");
                dto.setAvatarPath(avatarPath);
            }
            groupService.createGroup(dto);
            redirectAttributes.addFlashAttribute("success", "Группа успешно добавлена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при добавлении группы: " + e.getMessage());
        }
        return "redirect:/admin/groups";
    }

    @GetMapping("/groups/edit/{id}")
    public String editGroupForm(@PathVariable int id, Model model) {
        Group group = groupService.findById(id);
        GroupDto groupDto = groupService.mapToDto(group);
        model.addAttribute("group", groupDto);
        return "admin-group-edit";
    }

    @PostMapping("/groups/edit/{id}")
    public String updateGroup(@PathVariable int id,
                            @RequestParam String name,
                            @RequestParam(required = false) MultipartFile avatarFile,
                            RedirectAttributes redirectAttributes) {
        try {
            GroupDto dto = groupService.mapToDto(groupService.findById(id));
            dto.setName(name);
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String avatarPath = fileStorageService.storeAvatar(avatarFile, "group");
                dto.setAvatarPath(avatarPath);
            }
            groupService.updateGroup(id, dto);
            redirectAttributes.addFlashAttribute("success", "Группа успешно обновлена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении группы: " + e.getMessage());
        }
        return "redirect:/admin/groups";
    }

    @PostMapping("/groups/delete/{id}")
    public String deleteGroup(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            groupService.deleteGroup(id);
            redirectAttributes.addFlashAttribute("success", "Группа успешно удалена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении группы: " + e.getMessage());
        }
        return "redirect:/admin/groups";
    }

    @GetMapping("/albums")
    public String albums(@RequestParam(required = false) String searchQuery, Model model) {
        List<Album> allAlbums = (searchQuery != null && !searchQuery.trim().isEmpty())
                ? albumRepository.findByNameContainingIgnoreCase(searchQuery.trim())
                : albumRepository.findAll();
        List<AlbumDto> albums = allAlbums.stream()
                .map(albumService::mapToDto)
                .collect(Collectors.toList());

        model.addAttribute("albums", albums);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("groups", groupRepository.findAll().stream()
                .map(groupService::mapToDto)
                .collect(Collectors.toList()));
        return "admin-albums";
    }

    @PostMapping("/albums/add")
    public String addAlbum(@RequestParam String name,
                          @RequestParam int groupId,
                          @RequestParam(required = false) String releaseDate,
                          RedirectAttributes redirectAttributes) {
        try {
            AlbumDto dto = new AlbumDto();
            dto.setName(name);
            dto.setGroupId(groupId);
            if (releaseDate != null && !releaseDate.trim().isEmpty()) {
                dto.setReleaseDate(LocalDate.parse(releaseDate));
            }
            albumService.createAlbum(dto);
            redirectAttributes.addFlashAttribute("success", "Альбом успешно добавлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при добавлении альбома: " + e.getMessage());
        }
        return "redirect:/admin/albums";
    }

    @GetMapping("/albums/edit/{id}")
    public String editAlbumForm(@PathVariable int id, Model model) {
        Album album = albumService.findById(id);
        AlbumDto albumDto = albumService.mapToDto(album);
        model.addAttribute("album", albumDto);
        model.addAttribute("groups", groupRepository.findAll().stream()
                .map(groupService::mapToDto)
                .collect(Collectors.toList()));
        return "admin-album-edit";
    }

    @PostMapping("/albums/edit/{id}")
    public String updateAlbum(@PathVariable int id,
                             @RequestParam String name,
                             @RequestParam int groupId,
                             @RequestParam(required = false) String releaseDate,
                             RedirectAttributes redirectAttributes) {
        try {
            AlbumDto dto = albumService.mapToDto(albumService.findById(id));
            dto.setName(name);
            dto.setGroupId(groupId);
            if (releaseDate != null && !releaseDate.trim().isEmpty()) {
                dto.setReleaseDate(LocalDate.parse(releaseDate));
            }
            albumService.updateAlbum(id, dto);
            redirectAttributes.addFlashAttribute("success", "Альбом успешно обновлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении альбома: " + e.getMessage());
        }
        return "redirect:/admin/albums";
    }

    @PostMapping("/albums/delete/{id}")
    public String deleteAlbum(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            albumService.deleteAlbum(id);
            redirectAttributes.addFlashAttribute("success", "Альбом успешно удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении альбома: " + e.getMessage());
        }
        return "redirect:/admin/albums";
    }

    @GetMapping("/users")
    public String users(@RequestParam(required = false) String searchQuery, Model model) {
        List<User> allUsers = userRepository.findAll();
        List<UserDto> users = allUsers.stream()
                .map(userService::mapToDto)
                .collect(Collectors.toList());

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String query = searchQuery.toLowerCase().trim();
            users = users.stream()
                    .filter(user -> (user.getLogin() != null && user.getLogin().toLowerCase().contains(query)) ||
                                   (user.getEmail() != null && user.getEmail().toLowerCase().contains(query)))
                    .collect(Collectors.toList());
        }

        model.addAttribute("users", users);
        model.addAttribute("searchQuery", searchQuery);
        return "admin-users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Пользователь успешно удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении пользователя: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
