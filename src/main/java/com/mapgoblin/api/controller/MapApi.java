package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.map.CreateMapDataRequest;
import com.mapgoblin.domain.Layer;
import com.mapgoblin.domain.Map;
import com.mapgoblin.domain.mapdata.MapData;
import com.mapgoblin.domain.mapdata.Point;
import com.mapgoblin.service.LayerService;
import com.mapgoblin.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mapdata")
@RequiredArgsConstructor
public class MapApi {

    private final MapService mapService;
    private final LayerService layerService;

    /**
     * Create New MapData
     *
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateMapDataRequest request) {

        Map map = mapService.findByMapId(request.getMapId());
        Layer layer = layerService.findByLayerName(request.getLayerName());

        if (map == null){
            // 요청해온 map id 가 유효하지 않을경우 오류
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (layer == null){
            // 만약 요청해온 레이어가 현재 디비에 없는 경우 새로 레이어를 만들어 준다.
            // 만든 뒤에 이 레이어에 맵 데이터를 추가
            System.out.println("/////////////////////////////////////////");
            System.out.println("Layer NULLLLLL");
            System.out.println("/////////////////////////////////////////");
            Layer newLayer = Layer.createLayer(request.getLayerName());
            layerService.save(newLayer);
            map.addLayer(newLayer);
            mapService.save(map);
            layer = newLayer;
        }
        switch (request.getMapDataType()){
            case "point":
                Point point = Point.createPoint(
                        layer,
                        request.getTitle(),
                        request.getDescription(),
                        request.getRating(),
                        request.getGeometry(),
                        request.getThumbnail()
                );
                layer.addMapData(point);
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get MapData Layer
     *
     * @param request
     * @return
     */
    @GetMapping("/layer")
    public ResponseEntity<?> getMapDataListByLayerId(@RequestBody CreateMapDataRequest request) {
        Map map = mapService.findByMapId(request.getMapId());
        Layer layer = layerService.findByMapId(request.getMapId());
        System.out.println("---------------------------------------------------------");
        System.out.println("MAP " + map.getLayers());
        System.out.println("---------------------------------------------------------");
        List<Layer> layerList = map.getLayers();
//        Layer layer = layerService.findByLayerName(request.getLayerName());
//        List<MapData> mapDataList =  layer.getMapDataList();
        System.out.println("/////////////////////////////////////////////////////////");
        System.out.println(layer);
        System.out.println("/////////////////////////////////////////////////////////");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
