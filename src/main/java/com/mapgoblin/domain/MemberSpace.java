package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import com.mapgoblin.domain.base.SourceType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSpace extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_space_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @Enumerated(EnumType.STRING)
    private SourceType source;

    /**
     * Create MemberSpace method
     *
     * @param space
     * @return
     */
    public static MemberSpace createMemberSpace(Space space) {
        MemberSpace memberSpace = new MemberSpace();
        memberSpace.setSpace(space);
        memberSpace.setSource(SourceType.HOST);

        return memberSpace;
    }

    /**
     * Clone MemberSpace method
     *
     * @param space
     * @return
     */
    public static MemberSpace cloneMemberSpace(Space space) {
        MemberSpace memberSpace = new MemberSpace();
        memberSpace.setSpace(space);
        memberSpace.setSource(SourceType.CLONE);

        return memberSpace;
    }
}
