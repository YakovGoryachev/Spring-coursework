package com.goryachev.music_service.Repository;

import com.goryachev.music_service.Pojo.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByTrack_Id(int trackId);

    List<Comment> findByUser_Id(int userId);
}
