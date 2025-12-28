package com.goryachev.music_service.ControllersRest;

import com.goryachev.music_service.Pojo.Track;
import com.goryachev.music_service.Service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class AudioController {

    TrackService trackService;

    @Autowired
    public AudioController(TrackService trackService){
        this.trackService = trackService;
    }


    @GetMapping("/audio/{id}")
    public ResponseEntity<Resource> serveAudio(@PathVariable int id){
        System.out.println("AUDIO ID ENTRANCE ;ASLKDFJ;ASLDKJF;ASLDFJ;ASDLKFJASD;LFKJASD;LFKJQWEPORIUQWEPRIOU");
        Track track = trackService.findById(id);
        if (track == null){
            return ResponseEntity.notFound().build();
        }
        try {
            Path path = Paths.get(track.getFilePath());
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("audio/mpeg")) // или другой MIME-тип
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
