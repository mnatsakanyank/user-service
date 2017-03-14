package com.soundcloud.user.event.follower;

import com.google.common.collect.*;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static com.google.common.collect.Multimaps.synchronizedMultimap;

@Component
public class FollowersStore {

    private final Multimap<Long, Long> followers = synchronizedMultimap(HashMultimap.create());

    public Collection<Long> getUserFollowers(Long userId){
        return followers.get(userId);
    }

    public void registerFollower(Long userId, Long followerId){
        followers.put(userId,followerId);
    }

    public void unfollow(Long userId, Long followerId){
        followers.remove(userId,followerId);
    }

    public void cleanAll(){
        followers.clear();
    }
}
