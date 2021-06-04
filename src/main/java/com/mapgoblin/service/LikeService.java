package com.mapgoblin.service;

import com.mapgoblin.domain.Likes;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.LikeType;
import com.mapgoblin.repository.LikeRepository;
import com.mapgoblin.repository.MemberRepository;
import com.mapgoblin.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;
    private final SpaceRepository spaceRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public int addLike(Long memberId, Long spaceId, LikeType likeType){

        Space space = spaceRepository.findById(spaceId).orElse(null);

        Member member = memberRepository.findById(memberId).orElse(null);

        if (space == null || member == null){
            return 0;
        }

        Likes alreadyLike = isAlreadyLike(member, space);

        if(alreadyLike == null){
            Likes like = Likes.create(likeType);

            member.addLikes(like);
            space.addLikes(like);

            likeRepository.save(like);

            if(likeType == LikeType.LIKE){
                space.setLikeCount(space.getLikeCount() + 1);
            }else{
                space.setDislikeCount(space.getDislikeCount() + 1);
            }

            return 1;

        }else{
            if(alreadyLike.getType() == likeType){
                likeRepository.delete(alreadyLike);

                if(likeType == LikeType.LIKE){
                    space.setLikeCount(space.getLikeCount() - 1);
                }else{
                    space.setDislikeCount(space.getDislikeCount() - 1);
                }

                return 2;
            }else{
                alreadyLike.setType(likeType);

                if(likeType == LikeType.LIKE){
                    space.setLikeCount(space.getLikeCount() + 1);
                    space.setDislikeCount(space.getDislikeCount() - 1);
                }else{
                    space.setLikeCount(space.getLikeCount() - 1);
                    space.setDislikeCount(space.getDislikeCount() + 1);
                }

                return 3;
            }
        }
    }

    public Likes isAlreadyLike(Member member, Space space){
        return likeRepository.findByMemberAndSpace(member, space).orElse(null);
    }

    public List<Likes> findByMemberId(Long memberId){
        return likeRepository.findByMemberId(memberId)
                .orElse(null);
    }
}
