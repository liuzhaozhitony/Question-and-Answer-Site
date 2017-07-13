package com.nowcoder.service;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by LIU ZHAOZHI on 2017-6-12.
 */
@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;

    public List<Comment> getCommentsByEntity(int entityId, int entityType){
        return commentDAO.selectByEntity(entityId,entityType);
    }

    public int addComment(Comment comment){

        return commentDAO.addComment(comment);
    }

    public int getCommentCount(int entityId, int entityType){
        return commentDAO.getCommentCount(entityId,entityType);
    }

    public void deleteComment(int entityId, int entityType){
         commentDAO.updateStatus(entityId,entityType,1);
    }

    public Comment getCommentById(int id){
        return commentDAO.getCommentById(id);
    }
}
