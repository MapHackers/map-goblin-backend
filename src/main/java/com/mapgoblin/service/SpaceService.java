package com.mapgoblin.service;

import com.mapgoblin.api.dto.space.CreateSpaceRequest;
import com.mapgoblin.api.dto.space.CreateSpaceResponse;
import com.mapgoblin.api.dto.space.SpaceResponse;
import com.mapgoblin.domain.*;
import com.mapgoblin.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpaceService {

    private final MemberRepository memberRepository;
    private final SpaceRepository spaceRepository;
    private final MapRepository mapRepository;
    private final MemberSpaceRepository memberSpaceRepository;
    private final LayerRepository layerRepository;
    private final MapDataRepository mapDataRepository;
    private final ReviewRepository reviewRepository;
    private final CategoryRepository categoryRepository;
    private final SpaceCategoryRepository spaceCategoryRepository;

    /**
     * Find all repositories
     *
     * @return
     */
    public List<Space> findAll() {
        return spaceRepository.findAll();
    }

    /**
     * Find one by userId, repositoryName
     *
     * @param memberId
     * @param repoName
     * @return
     */
    public List<SpaceResponse> findOne(Long memberId, String repoName){

        return memberSpaceRepository.findByMemberIdAndSpaceName(memberId, repoName);

    }

    /**
     *
     * @param spaceId
     * @return
     */
    public Space findById(Long spaceId){
        return spaceRepository.findById(spaceId).orElse(null);
    }

    /**
     *
     * @param memberId
     * @param hostId
     * @return
     */
    public List<SpaceResponse> findByMemberIdAndHostId(Long memberId, Long hostId){
        return memberSpaceRepository.findByMemberIdAndHostId(memberId, hostId);
    }

    /**
     * Create Space
     *
     * @param memberId
     * @param request
     * @return
     */
    @Transactional
    public CreateSpaceResponse create(Long memberId, CreateSpaceRequest request) {

        Member member = memberRepository.findById(memberId).orElse(null);

        if(member == null) {
            return null;
        }

        try{

            Map map = Map.createMap();

            mapRepository.save(map);

            Space space = Space.createSpace(request.getName(), request.getThumbnail(), request.getDescription(), map);

            if(request.getCategories() != null){
                for (String category : request.getCategories()) {

                    SpaceCategory spaceCategory = new SpaceCategory();

                    Category myCategory = Category.createCategory(category);

                    categoryRepository.save(myCategory);

                    myCategory.addSpaceCategory(spaceCategory);

                    space.addCategory(spaceCategory);

                    spaceCategoryRepository.save(spaceCategory);
                }
            }

            spaceRepository.save(space);

            MemberSpace memberSpace = MemberSpace.createMemberSpace(space);

            member.addMemberSpace(memberSpace);

            memberSpaceRepository.save(memberSpace);

            return new CreateSpaceResponse(
                    space.getId(),
                    space.getMap().getId(),
                    space.getName(),
                    space.getThumbnail(),
                    space.getDescription(),
                    space.getLikeCount(),
                    space.getDislikeCount());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public CreateSpaceResponse clone(Long memberId, Space hostSpace) {

        Member member = memberRepository.findById(memberId).orElse(null);

        if(member == null) {
            return null;
        }

        try{
            Space copySpace = (Space) hostSpace.clone();

            copySpace.setHost(hostSpace);

            Map copyMap = copySpace.getMap();

            System.out.println("//////////////////////////");
            System.out.println(copyMap.getLayers().size());
            System.out.println("//////////////////////////");

            mapRepository.save(copyMap);

            copyMap.getLayers().forEach(layer -> {
                layerRepository.save(layer);

                layer.getMapDataList().forEach(mapData -> {
                    mapDataRepository.save(mapData);

                    mapData.getReviews().forEach(review -> {
                        reviewRepository.save(review);
                    });
                });
            });

            copySpace.getCategories().forEach(spaceCategory -> {
                categoryRepository.save(spaceCategory.getCategory());

                spaceCategoryRepository.save(spaceCategory);
            });

            spaceRepository.save(copySpace);

            MemberSpace memberSpace = MemberSpace.cloneMemberSpace(copySpace);

            member.addMemberSpace(memberSpace);

            memberSpaceRepository.save(memberSpace);

            return new CreateSpaceResponse(
                    copySpace.getId(),
                    copySpace.getMap().getId(),
                    copySpace.getName(),
                    copySpace.getThumbnail(),
                    copySpace.getDescription(),
                    copySpace.getLikeCount(),
                    copySpace.getDislikeCount());

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Space findByHost(Space space){
        return spaceRepository.findByHost(space).orElse(null);
    }
}
