package com.mapgoblin.service;

import com.mapgoblin.api.dto.space.CreateSpaceRequest;
import com.mapgoblin.api.dto.space.CreateSpaceResponse;
import com.mapgoblin.api.dto.space.SpaceResponse;
import com.mapgoblin.domain.*;
import com.mapgoblin.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final AlarmRepository alarmRepository;
    private final IssueRepository issueRepository;
    private final RequestRepository requestRepository;

    /**
     * Find all spaces
     *
     * @return
     */
    public List<Space> findAll() {
        return spaceRepository.findAll();
    }

    /**
     * Find one by userId, spaceName
     *
     * @param memberId
     * @param spaceName
     * @return
     */
    public SpaceResponse findOne(Long memberId, String spaceName){
        return memberSpaceRepository.findByMemberIdAndSpaceName(memberId, spaceName)
                .stream()
                .findFirst()
                .orElse(null);

    }

    /**
     *
     * @param memberId
     * @param hostId
     * @return
     */
    public SpaceResponse findOne(Long memberId, Long hostId){
        return memberSpaceRepository.findByMemberIdAndHostId(memberId, hostId)
                .stream()
                .findFirst()
                .orElse(null);
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

            Layer layer = Layer.createLayer("Layer1");

            Map map = Map.createMap();

            map.addLayer(layer);

            mapRepository.save(map);

            layerRepository.save(layer);

            Space space = Space.createSpace(request.getName(), request.getThumbnail(), request.getDescription(), map);

            spaceRepository.save(space);

            if(request.getCategories() != null){
                for (String category : request.getCategories()) {

                    SpaceCategory spaceCategory = new SpaceCategory();

                    Category myCategory = categoryRepository.findByName(category).orElse(null);

                    if(myCategory == null){
                        myCategory = Category.createCategory(category);

                        categoryRepository.save(myCategory);
                    }

                    myCategory.addSpaceCategory(spaceCategory);

                    space.addCategory(spaceCategory);

                    spaceCategoryRepository.save(spaceCategory);
                }
            }

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

    @Transactional
    public void modify(Long spaceId, CreateSpaceRequest request){
        Space findSpace = spaceRepository.findById(spaceId).orElse(null);

        findSpace.setName(request.getName());
        findSpace.setThumbnail(request.getThumbnail());
        findSpace.setDescription(request.getDescription());
        findSpace.setOneWord(request.getOneWord());

        for (SpaceCategory category : findSpace.getCategories()) {
            spaceCategoryRepository.delete(category);
        }

        for (String newCategory : request.getCategories()) {
            SpaceCategory spaceCategory = new SpaceCategory();

            Category myCategory = categoryRepository.findByName(newCategory).orElse(null);

            if(myCategory == null){
                myCategory = Category.createCategory(newCategory);

                categoryRepository.save(myCategory);
            }

            myCategory.addSpaceCategory(spaceCategory);

            findSpace.addCategory(spaceCategory);

            spaceCategoryRepository.save(spaceCategory);
        }
    }

    @Transactional
    public void delete(Space space){

        List<Request> requests = requestRepository.findBySpace(space).orElse(null);

        requests.forEach(requestRepository::delete);

        List<Issue> findIssues = issueRepository.findBySpace(space).orElse(null);

        findIssues.forEach(issueRepository::delete);

        List<Alarm> byDstSpace = alarmRepository.findByDstSpace(space).orElse(null);

        byDstSpace.forEach(alarmRepository::delete);

        List<Layer> layers = space.getMap().getLayers();

        for (Layer layer : layers) {
            List<Layer> clonedLayers = layerRepository.findByHost(layer).orElse(null);

            for (Layer clonedLayer : clonedLayers) {
                clonedLayer.setHost(null);
            }
        }

        List<MemberSpace> findMemberSpaces = memberSpaceRepository.findBySpace(space).orElse(null);

        findMemberSpaces.forEach(memberSpaceRepository::delete);

        List<Space> byHost = spaceRepository.findByHost(space).orElse(null);

        if(byHost != null){
            byHost.forEach(cloned -> {
                cloned.setHost(null);
            });
        }

        spaceRepository.delete(space);
    }

    public List<Space> search(String keyword){
        List<Space> containingNameSpaces = spaceRepository.findByNameContaining(keyword).orElse(null);
        List<Space> containingDescriptionSpaces = spaceRepository.findByDescriptionContaining(keyword).orElse(null);

        Comparator<Space> compareByCreatedDate = Comparator.comparing( Space::getCreatedDate ).reversed();

        return Stream.concat(containingNameSpaces.stream(), containingDescriptionSpaces.stream())
                .distinct()
                .sorted(compareByCreatedDate)
                .collect(Collectors.toList());
    }
}
