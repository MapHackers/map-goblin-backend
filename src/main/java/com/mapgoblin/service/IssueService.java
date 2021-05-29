package com.mapgoblin.service;

import com.mapgoblin.api.dto.issue.CreateIssueResponse;
import com.mapgoblin.api.dto.issue.GetIssueResponse;
import com.mapgoblin.domain.Issue;
import com.mapgoblin.domain.Space;
import com.mapgoblin.repository.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IssueService {

    private final IssueRepository issueRepository;

    @Transactional
    public CreateIssueResponse save(Issue issue) {
        issueRepository.save(issue);

        return new CreateIssueResponse(issue.getTitle(), issue.getContent(),
                issue.getStatus(), issue.getCreatedDate(), issue.getCreatedBy());
    }

    public List<GetIssueResponse> findBySpace(Space space){
        List<GetIssueResponse> result = null;
        List<Issue> issueList = issueRepository.findBySpace(space).orElse(null);

        if (issueList != null){
            result = issueList.stream().map(issue -> {
                return new GetIssueResponse(issue.getTitle(), issue.getContent(),
                        issue.getStatus(), issue.getCreatedDate(), issue.getCreatedBy());
            }).collect(Collectors.toList());
        }

        return result;
    }
}
