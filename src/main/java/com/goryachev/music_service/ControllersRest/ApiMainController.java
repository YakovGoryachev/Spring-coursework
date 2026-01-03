package com.goryachev.music_service.ControllersRest;

import com.goryachev.music_service.Pojo.Track;
import com.goryachev.music_service.Service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/tracks")
@RestController
public class ApiMainController {
    private TrackService trackService;

    @Autowired
    public ApiMainController(TrackService trackService){
        this.trackService = trackService;
    }

    @GetMapping("/{id}/info")
    @ResponseBody
    public Map<String, String> getTrackInfo(@PathVariable int id) {
        Track track = trackService.findById(id);
        if (track == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Map<String, String> info = new HashMap<>();
        info.put("name", track.getName());
        info.put("filepath", track.getFilePath());
        return info;
    }

    @PostMapping("/{id}/play")
    public ResponseEntity<Void> incrementPlayCount(@PathVariable int id) {
        trackService.incrementPlayCount(id);
        return ResponseEntity.ok().build(); // или ResponseEntity.noContent().build();
    }
}
