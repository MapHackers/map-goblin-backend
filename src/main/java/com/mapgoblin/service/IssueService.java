package com.mapgoblin.service;

import com.mapgoblin.api.dto.issue.CreateIssueResponse;
import com.mapgoblin.api.dto.issue.CreateIssueReviewResponse;
import com.mapgoblin.api.dto.issue.GetIssueResponse;
import com.mapgoblin.domain.Issue;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.IssueStatus;
import com.mapgoblin.domain.base.IssueTag;
import com.mapgoblin.repository.IssueRepository;
import com.mapgoblin.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IssueService {

    private final IssueRepository issueRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CreateIssueResponse save(Issue issue) {
        issueRepository.save(issue);

        return new CreateIssueResponse(issue.getId(), issue.getTitle(), issue.getContent(),
                issue.getStatus(), issue.getCreatedDate(), issue.getCreatedBy());
    }

    public Page<GetIssueResponse> findBySpace(Space space, IssueStatus status, Pageable pageable){
        Page<Issue> issueList = issueRepository.findBySpaceAndStatus(space, status, pageable);

        Page<GetIssueResponse> result = issueList.map(issue -> {
            GetIssueResponse getIssueResponse = new GetIssueResponse(issue);

            List<CreateIssueReviewResponse> temp = issue.getIssueReviewList().stream()
                    .map(review -> {
                        Member findInfo = memberRepository.findByUserId(review.getAuthor()).orElse(null);
                        return new CreateIssueReviewResponse(review.getId(), findInfo.getName(), review.getContent(), findInfo.getProfile(), review.getCreatedDate());
                    }).collect(Collectors.toList());

            getIssueResponse.setIssueReviewList(temp);

            return getIssueResponse;
        });

        return result;
    }

    public Issue findIssueById(Long id){
        return issueRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean setChecked(Long id){
        Issue issue = issueRepository.findById(id).orElse(null);
        if (issue == null) return false;
        else{
            issue.setStatus(IssueStatus.CHECKED);
            issue.setTag(IssueTag.OK);
            return true;
        }
    }
}
