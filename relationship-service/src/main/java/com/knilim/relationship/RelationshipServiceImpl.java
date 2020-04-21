package com.knilim.relationship;

import com.knilim.data.model.Friendship;
import com.knilim.service.RelationshipService;

import java.util.ArrayList;

public class RelationshipServiceImpl implements RelationshipService{

    private RelationshipServiceImpl relationshipRepository;

    public void setRelationshipRepository(RelationshipServiceImpl relationshipRepository) {
        this.relationshipRepository = relationshipRepository;
    }

    @Override
    public ArrayList<Friendship> getFriendsByUserId(String userId) {
        return relationshipRepository.getFriendsByUserId(userId);
    }
}
