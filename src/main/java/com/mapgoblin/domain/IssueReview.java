package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
public class IssueReview extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "issue_review_id")
    private Long id;

    private String author;

    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "issue_id")
    private Issue issue;

    public static IssueReview create(Issue issue, String author, String content) {
        IssueReview issueReview = new IssueReview();

        issueReview.setIssue(issue);
        issueReview.setAuthor(author);
        issueReview.setContent(content);

        return issueReview;
    }
}
