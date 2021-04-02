package com.mapgoblin.domain;

import com.mapgoblin.domain.base.BaseEntity;
import com.mapgoblin.domain.base.MemberRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String userId;

    private String name;

    private String email;

    private String password;

    private int reward;

    private int recode;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @OneToMany(mappedBy = "member")
    private List<MemberSpace> spaces = new ArrayList<>();

    /**
     * Create Member method
     *
     * @param userId
     * @param name
     * @param email
     * @param password
     * @param role
     * @return
     */
    public static Member createMember(String userId, String name, String email, String password, MemberRole role) {
        Member member = new Member();

        member.setUserId(userId);
        member.setName(name);
        member.setEmail(email);
        member.setPassword(password);
        member.setRole(role);

        return member;
    }

    public void addMemberSpace(MemberSpace memberSpace) {
        spaces.add(memberSpace);
        memberSpace.setMember(this);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();

        list.add(new SimpleGrantedAuthority(this.role.toString()));

        return list;
    }

    @Override
    public String getUsername() {
        return this.userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
