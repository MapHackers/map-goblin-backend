package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.issue.CreateIssueRequest;
import com.mapgoblin.api.dto.issue.CreateIssueResponse;
import com.mapgoblin.api.dto.issue.CreateIssueReviewResponse;
import com.mapgoblin.api.dto.issue.GetIssueResponse;
import com.mapgoblin.api.dto.space.SpaceResponse;
import com.mapgoblin.domain.Issue;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.AlarmType;
import com.mapgoblin.domain.base.IssueStatus;
import com.mapgoblin.service.AlarmService;
import com.mapgoblin.service.IssueService;
import com.mapgoblin.service.MemberService;
import com.mapgoblin.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class IssueApi {

    private final IssueService issueService;
    private final MemberService memberService;
    private final SpaceService spaceService;
    private final AlarmService alarmService;

    /**
     * Create issue
     *
     * @param request
     * @param userId
     * @param repositoryName
     * @return
     */
    @PostMapping("/{userId}/spaces/{repositoryName}/issues")
    public ResponseEntity<?> create(@RequestBody CreateIssueRequest request,
                                    @PathVariable String userId, @PathVariable String repositoryName){

        Member findMember = memberService.findByUserId(userId);

        SpaceResponse spaceResponse = spaceService.findOne(findMember.getId(), repositoryName);

        if (spaceResponse != null) {
            Space space = spaceService.findById(spaceResponse.getId());

            Issue issue = Issue.create(request.getTitle(), request.getContent(), space);

            CreateIssueResponse result = issueService.save(issue);

            return ResponseEntity.ok(result);
        }else{
            return ApiResult.errorMessage("존재하지 않는 지도입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * get issue list for one repository
     *
     * @param userId
     * @param repositoryName
     * @return
     */
    @GetMapping("/{userId}/spaces/{repositoryName}/issues")
    public ResponseEntity<?> getIssueList(@PathVariable String userId, @PathVariable String repositoryName,
                                          @RequestParam String status,
                                          @PageableDefault(size = 8, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Member findMember = memberService.findByUserId(userId);

        SpaceResponse spaceResponse = spaceService.findOne(findMember.getId(), repositoryName);

        if (spaceResponse != null) {
            Space space = spaceService.findById(spaceResponse.getId());

            Page<GetIssueResponse> result = issueService.findBySpace(space, IssueStatus.valueOf(status), pageable);

            if (result != null){
                return ResponseEntity.ok(result);
            }else{
                return ApiResult.errorMessage("이슈가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
        }else{
            return ApiResult.errorMessage("존재하지 않는 지도입니다.", HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * get one issue information
     *
     * @param userId
     * @param repositoryName
     * @param id
     * @return
     */
    @GetMapping("/{userId}/spaces/{repositoryName}/issues/{id}")
    public ResponseEntity<?> getIssueList(@PathVariable String userId, @PathVariable String repositoryName, @PathVariable Long id) {

        Issue issue = issueService.findIssueById(id);
        Member member = memberService.findByUserId(issue.getCreatedBy());
        GetIssueResponse result = new GetIssueResponse(issue, member);

        List<CreateIssueReviewResponse> temp = issue.getIssueReviewList().stream()
                .map(review -> {
                    Member findInfo = memberService.findByUserId(review.getAuthor());
                    return new CreateIssueReviewResponse(review.getId(), findInfo.getName(), review.getContent(), findInfo.getProfile(), review.getCreatedDate());
                })
                .collect(Collectors.toList());

        result.setIssueReviewList(temp);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{userId}/spaces/{repositoryName}/issues/{id}/check")
    public ResponseEntity<?> checkIssue(@PathVariable String userId, @PathVariable String repositoryName, @PathVariable Long id) {
        if(issueService.setChecked(id)){

            Member findMember = memberService.findByUserId(userId);

            SpaceResponse spaceResponse = spaceService.findOne(findMember.getId(), repositoryName);

            if (spaceResponse != null){
                Issue issue = issueService.findIssueById(id);

                alarmService.createAlarm(issue.getCreatedBy(), spaceResponse.getId(), AlarmType.ISSUE_OK);

                return new ResponseEntity<>(HttpStatus.OK);
            }else {
                return ApiResult.errorMessage("존재하지 않는 이슈입니다.", HttpStatus.BAD_REQUEST);
            }
        } else {
            return ApiResult.errorMessage("존재하지 않는 이슈입니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
