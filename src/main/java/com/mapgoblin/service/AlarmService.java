package com.mapgoblin.service;

import com.mapgoblin.api.dto.alarm.AlarmResponse;
import com.mapgoblin.domain.Alarm;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.MemberSpace;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.AlarmType;
import com.mapgoblin.repository.AlarmRepository;
import com.mapgoblin.repository.MemberRepository;
import com.mapgoblin.repository.MemberSpaceRepository;
import com.mapgoblin.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmService {
    private final SpaceRepository spaceRepository;
    private final MemberSpaceRepository memberSpaceRepository;
    private final AlarmRepository alarmRepository;
    private final MemberRepository memberRepository;

    /**
     *
     * @param id
     * @return
     */
    public Space findSpaceById(Long id){
        return spaceRepository.findById(id).orElse(null);
    }

    /**
     *
     * @param memberId
     * @return
     */
    public List<Alarm> findAlarmsByMemberId(String memberId){
        Member member = memberRepository.findByUserId(memberId).orElse(null);
        return alarmRepository.findByDstMemberOrderByCreatedDateDesc(member).orElse(null);
    }

    /**
     *
     * @param alarmId
     * @return
     */
    @Transactional
    public boolean setAlarmRead(Long alarmId){
        Alarm alarm = alarmRepository.findById(alarmId).orElse(null);

        if (alarm == null){
            return false;
        }
        else{
            alarm.setRead(true);
            return true;
        }

    }

    @Transactional
    public boolean setAllAlarmRead(String memberId){
        Member member = memberRepository.findByUserId(memberId).orElse(null);
        List<Alarm> alarms = alarmRepository.findByDstMemberOrderByCreatedDateDesc(member).orElse(null);
        if (alarms == null){
            return false;
        }
        else{
            alarms.forEach(alarm -> alarm.setRead(true));
            return true;
        }
    }

    /**
     *
     * @param space
     * @return
     */
    public List<Member> findMembersBySpace(Space space){
        List<Member> members = new ArrayList<>();
        List<MemberSpace> memberSpaces;
        memberSpaces = memberSpaceRepository.findBySpace(space).orElse(null);

        memberSpaces.forEach(memberSpace -> {
            members.add(memberSpace.getMember());
        });

        return members;
    }

    /**
     *
     * @param id
     * @param alarmType
     * @return
     */
    @Transactional
    public List<AlarmResponse> save(Long id, AlarmType alarmType){

        Space findSpace = spaceRepository.findById(id).orElse(null);
        List<MemberSpace> findMemberSpaces = memberSpaceRepository.findBySpace(findSpace).orElse(null);

        if(findSpace == null || findMemberSpaces == null ){
            return null;
        }

        List<AlarmResponse> alarmList = new ArrayList<>();

        List<Member> memberList = findMemberSpaces.stream()
                .map(MemberSpace::getMember)
                .collect(Collectors.toList());

        memberList.forEach(member -> {

            Alarm alarm = Alarm.createAlarm(findSpace, alarmType);
            member.addAlarm(alarm);
            alarmRepository.save(alarm);

            AlarmResponse alarmResponse = new AlarmResponse(alarm.getId(), alarm.getDstMember().getName(),
                    alarm.getCreatedBy(), alarm.getAlarmType(), findSpace.getName(), findSpace.getThumbnail(), alarm.isRead());

            alarmList.add(alarmResponse);
        });
        return alarmList;
    }
}
