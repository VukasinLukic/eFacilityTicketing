package com.efacility.ticketing.controller;

import com.efacility.ticketing.connection.HttpResponse;
import com.efacility.ticketing.connection.Response;
import com.efacility.ticketing.dto.CommentDTO;
import com.efacility.ticketing.dto.request.AddCommentRequest;
import com.efacility.ticketing.model.User;
import com.efacility.ticketing.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
@CrossOrigin("http://localhost:3000")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/byTicket/{ticketId}")
    public ResponseEntity<Response> getByTicket(@PathVariable Long ticketId,
                                                @AuthenticationPrincipal User currentUser) {
        List<CommentDTO> comments = commentService.getCommentsByTicket(ticketId, currentUser);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Comments fetched", Map.of("comments", comments), HttpStatus.OK)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<Response> add(@Valid @RequestBody AddCommentRequest request,
                                        @AuthenticationPrincipal User currentUser) {
        CommentDTO comment = commentService.addComment(request, currentUser);
        return ResponseEntity.ok(
                HttpResponse.getResponseWithData("Comment added", Map.of("comment", comment), HttpStatus.OK)
        );
    }
}
