package com.mapgoblin.service;

import com.mapgoblin.api.dto.alarm.AlarmResponse;
import com.mapgoblin.domain.Alarm;
import com.mapgoblin.domain.Member;
import com.mapgoblin.domain.MemberSpace;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.AlarmType;
import com.mapgoblin.repository.AlarmRepository;
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

    public Space findSpaceById(Long id){
        return spaceRepository.findById(id).orElse(null);
    }

    public List<Alarm> findAll(){ return alarmRepository.findAll(); }
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

//        List<Member> memberList = memberSpaces.stream()
//                .map(memberSpace -> memberSpace.getMember())
//                .collect(Collectors.toList());

        return members;
    }

    /**
     *
     * @param alarm
     */
    @Transactional
    public void saveTest(Alarm alarm){
        alarmRepository.save(alarm);
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
                    alarm.getCreatedBy(), alarm.getAlarmType(), findSpace.getName(), alarm.isRead());

            alarmList.add(alarmResponse);
        });
        return alarmList;
    }
}
