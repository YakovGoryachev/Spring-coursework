package com.goryachev.music_service.Service;

import com.goryachev.music_service.DTO.TrackDto;
import com.goryachev.music_service.Repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackService {

    private TrackRepository trackRepository;

    public TrackService(){

    }

    @Autowired
    public TrackService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    public void addTrack(TrackDto td){

    }
}
