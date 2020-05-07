package com.knilim.relationship;

import com.knilim.data.model.Friendship;
import com.knilim.relationship.dao.RelationshipRepository;
import com.knilim.service.RelationshipService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class RelationshipServiceImpl implements RelationshipService{

    private RelationshipRepository relationshipRepository;

    @Autowired
    public void setRelationshipRepository(RelationshipRepository relationshipRepository) {
        this.relationshipRepository = relationshipRepository;
    }

    @Override
    public List<Friendship> getFriendsByUserId(String userId) {
        return relationshipRepository.getFriendsByUserId(userId);
    }
}
