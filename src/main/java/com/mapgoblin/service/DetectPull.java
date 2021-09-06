package com.mapgoblin.service;

import com.mapgoblin.api.dto.request.ChangeInfo;
import com.mapgoblin.api.dto.request.RequestDataDto;
import com.mapgoblin.domain.Layer;
import com.mapgoblin.domain.Space;
import com.mapgoblin.domain.mapdata.MapData;
import com.mapgoblin.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DetectPull implements DetectService {

    private final SpaceRepository spaceRepository;

    @Override
    public boolean support(String type) {
        return type.equals("PULL");
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

            createdLayer = detectNewLayer(hostLayers, clonedLayers);

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
                        List<ChangeInfo> delete = detectData(cloneGeom, hostGeom, clonedLayer.getId());

                        if(!delete.isEmpty()){
                            result.setDelete(delete);
                        }

                        //추가된 데이터
                        List<ChangeInfo> added = detectData(hostGeom, cloneGeom, clonedLayer.getId());

                        if(!added.isEmpty()){
                            result.setAdded(added);
                        }

                        //수정된 데이터
                        List<ChangeInfo> modified = detectModifiedData(hostGeom, cloneGeom, clonedLayer.getId());

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
    private List<ChangeInfo> detectNewLayer(List<Layer> hostLayers, List<Layer> clonedLayers) {
        //새로 생성된 레이어
        List<ChangeInfo> result = new ArrayList<>();

        for (Layer hostLayer : hostLayers) {
            boolean layerCheck = true;

            for (Layer clonedLayer : clonedLayers) {
                if(clonedLayer.getHost().getId().equals(hostLayer.getId())){
                    layerCheck = false;
                    break;
                }
            }

            if(layerCheck){
                ChangeInfo changeInfo = getChangeInfo(hostLayer);

                result.add(changeInfo);
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

                result.add(changeInfo);

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

                result.add(changeInfo);
            }
        });

        return result;
    }

    private ChangeInfo getChangeInfo(String geom, MapData mapData, Long layerId) {
        return ChangeInfo.createdByMapData(mapData, layerId, geom);
    }

    private ChangeInfo getChangeInfo(Layer layer) {
        return ChangeInfo.createdByLayer(layer);
    }
}