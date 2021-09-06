package com.mapgoblin.service;

import com.mapgoblin.api.dto.request.ChangeInfo;
import com.mapgoblin.api.dto.request.RequestDataDto;
import com.mapgoblin.domain.Layer;
import com.mapgoblin.domain.RequestData;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.base.RequestStatus;
import com.mapgoblin.domain.mapdata.MapData;
import com.mapgoblin.repository.RequestDataRepository;
import com.mapgoblin.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DetectRequest implements DetectService {

    private final SpaceRepository spaceRepository;
    private final RequestDataRepository requestDataRepository;

    @Override
    public boolean support(String type) {
        return type.equals("REQUEST");
    }

    @Override
    public RequestDataDto compareMapData(Long hostId, Space clonedSpace) {
        RequestDataDto result = new RequestDataDto();

        Space hostSpace = spaceRepository.findById(hostId).orElse(null);

        if(hostSpace != null && clonedSpace != null) {
            List<Layer> hostLayers = hostSpace.getMap().getLayers();
            List<Layer> clonedLayers = clonedSpace.getMap().getLayers();

            //새로 생성된 레이어
            List<ChangeInfo> createdLayer;

            createdLayer = detectNewLayer(clonedLayers);

            if(!createdLayer.isEmpty()){
                result.setLayer(createdLayer);
            }

            for (Layer hostLayer : hostLayers) {
                for (Layer clonedLayer : clonedLayers) {
                    if(clonedLayer.getHost() == hostLayer){
                        //지오메트리 hash
                        HashMap<String, MapData> hostGeom = getGeometryHashMap(hostLayer);
                        HashMap<String, MapData> cloneGeom = getGeometryHashMap(clonedLayer);

                        // 삭제된 데이터
                        List<ChangeInfo> delete = detectData(hostGeom, cloneGeom, hostLayer.getId());

                        if(!delete.isEmpty()){
                            result.setDelete(delete);
                        }

                        //추가된 데이터
                        List<ChangeInfo> added = detectData(cloneGeom, hostGeom, hostLayer.getId());

                        if(!added.isEmpty()){
                            result.setAdded(added);
                        }

                        //수정된 데이터
                        List<ChangeInfo> modified = detectModifiedData(cloneGeom, hostGeom, hostLayer.getId());

                        if(!modified.isEmpty()){
                            result.setModified(modified);
                        }

                        break;
                    }
                }
            }

        }

        return result;
    }

    private HashMap<String, MapData> getGeometryHashMap(Layer layer) {
        List<MapData> mapDataList = layer.getMapDataList();

        HashMap<String, MapData> result = new HashMap<>();

        for (MapData mapData : mapDataList) {
            result.put(mapData.getGeometry(), mapData);
        }

        return result;
    }


    /**
     * 새로 생긴 레이어 감지
     *
     * @param clonedLayers
     * @return
     */
    private List<ChangeInfo> detectNewLayer(List<Layer> clonedLayers) {
        List<ChangeInfo> result = new ArrayList<>();

        for (Layer clonedLayer : clonedLayers) {
            if(clonedLayer.getHost() == null){
                ChangeInfo changeInfo = getChangeInfo(clonedLayer);

                if(changeInfo != null) {
                    result.add(changeInfo);
                }
            }
        }

        return result;
    }

    /**
     * 데이터 추가, 삭제 감지
     *
     * @param target
     * @param comparisonTarget
     * @param layerId
     * @return
     */
    private List<ChangeInfo> detectData(HashMap<String, MapData> target, HashMap<String, MapData> comparisonTarget,
                                        Long layerId) {
        List<ChangeInfo> result = new ArrayList<>();
        List<String> detectGeoms = new ArrayList<>();

        target.forEach((geom, mapData) -> {
            if(!comparisonTarget.containsKey(geom)){
                ChangeInfo changeInfo = getChangeInfo(geom, mapData, layerId);

                if(changeInfo != null) {
                    result.add(changeInfo);
                }

                detectGeoms.add(geom);
            }
        });

        for (String geom : detectGeoms) {
            target.remove(geom);
        }

        return result;
    }

    /**
     * 수정된 데이터 감지
     *
     * @param target
     * @param comparisonTarget
     * @param layerId
     * @return
     */
    private List<ChangeInfo> detectModifiedData(HashMap<String, MapData> target, HashMap<String, MapData> comparisonTarget,
                                                Long layerId) {
        List<ChangeInfo> result = new ArrayList<>();

        target.forEach((geom, mapData) -> {
            MapData hostData = comparisonTarget.get(geom);

            if(!mapData.equals(hostData)){
                ChangeInfo changeInfo = getChangeInfo(geom, mapData, layerId);

                if(changeInfo != null) {
                    result.add(changeInfo);
                }
            }
        });

        return result;
    }

    private ChangeInfo getChangeInfo(String geom, MapData mapData, Long layerId) {
        RequestData findRequestData = requestDataRepository.findByMapDataIdAndLayerIdAndStatus(mapData.getId(), layerId, RequestStatus.WAITING).orElse(null);

        if(findRequestData == null){
            return ChangeInfo.createdByMapData(mapData, layerId, geom);
        }

        return null;
    }

    private ChangeInfo getChangeInfo(Layer layer) {
        RequestData findRequestData = requestDataRepository.findByMapDataIdAndLayerIdAndStatus(null, layer.getId(), RequestStatus.WAITING).orElse(null);

        if(findRequestData == null){
            return ChangeInfo.createdByLayer(layer);
        }

        return null;
    }
}