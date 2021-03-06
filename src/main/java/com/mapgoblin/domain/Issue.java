package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import com.mapgoblin.domain.base.IssueStatus;
import com.mapgoblin.domain.base.IssueTag;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
public class Issue extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "issue_id")
    private Long id;

    private String title;

    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    @Enumerated(EnumType.STRING)
    private IssueTag tag;

    @OneToMany(mappedBy = "issue", orphanRemoval = true)
    private List<IssueReview> issueReviewList = new ArrayList<>();

    public static Issue create(String title, String content, Space space) {
        Issue issue = new Issue();

        issue.setTitle(title);
        issue.setContent(content);
        issue.setSpace(space);
        issue.setStatus(IssueStatus.WAITING);
        issue.setTag(IssueTag.ISSUE);

        return issue;
    }

    public void addIssueReview(IssueReview issueReview) {
        this.issueReviewList.add(issueReview);
        issueReview.setIssue(this);
    }
}
