package com.goryachev.music_service.Service;

import com.goryachev.music_service.DTO.CommentDto;
import com.goryachev.music_service.Pojo.Comment;
import com.goryachev.music_service.Pojo.Track;
import com.goryachev.music_service.Pojo.User;
import com.goryachev.music_service.Repository.CommentRepository;
import com.goryachev.music_service.Repository.TrackRepository;
import com.goryachev.music_service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository,
                         UserRepository userRepository,
                         TrackRepository trackRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.trackRepository = trackRepository;
    }

    public Comment findById(int id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Комментарий не найден"));
    }

    public List<Comment> findByTrackId(int trackId) {
        return commentRepository.findByTrack_Id(trackId);
    }

    public List<Comment> findByUserId(int userId) {
        return commentRepository.findByUser_Id(userId);
    }

    public Comment createComment(CommentDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Track track = trackRepository.findById(dto.getTrackId())
                .orElseThrow(() -> new RuntimeException("Трек не найден"));

        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setUser(user);
        comment.setTrack(track);
        comment.setCommentDate(LocalDate.now());

        return commentRepository.save(comment);
    }

    public Comment updateComment(int id, CommentDto dto) {
        Comment comment = findById(id);

        if (dto.getText() != null) {
            comment.setText(dto.getText());
        }

        return commentRepository.save(comment);
    }

    public void deleteComment(int id) {
        Comment comment = findById(id);
        commentRepository.delete(comment);
    }

    public CommentDto mapToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        if (comment.getUser() != null) {
            dto.setUserId(comment.getUser().getId());
            dto.setUserLogin(comment.getUser().getLogin());
        }
        if (comment.getTrack() != null) {
            dto.setTrackId(comment.getTrack().getId());
            dto.setTrackName(comment.getTrack().getName());
        }
        dto.setCommentDate(comment.getCommentDate());
        return dto;
    }
}
