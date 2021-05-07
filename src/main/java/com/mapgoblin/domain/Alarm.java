package com.mapgoblin.domain;

import com.mapgoblin.domain.base.AlarmType;
import com.mapgoblin.domain.base.BaseEntity;
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
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "alarm_id")
    private Long id;

    private String srcMember;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member dstMember;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "space_id")
    private Space dstSpace;

    private boolean isRead;

    /**
     *
     * @return
     */
    public static Alarm createAlarm(){
        Alarm alarm = new Alarm();
        alarm.setSrcMember(alarm.getCreatedBy());
        alarm.setRead(false);

        return alarm;
    }
}
