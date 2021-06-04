package com.mapgoblin.api.controller;

import com.mapgoblin.api.dto.ApiResult;
import com.mapgoblin.api.dto.map.CreateMapDataRequest;
import com.mapgoblin.api.dto.map.LayerDto;
import com.mapgoblin.domain.Layer;
import com.mapgoblin.domain.Map;
import com.mapgoblin.domain.mapdata.MapData;
import com.mapgoblin.domain.mapdata.Point;
import com.mapgoblin.service.LayerService;
import com.mapgoblin.service.MapDataService;
import com.mapgoblin.service.MapService;
import com.mapgoblin.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mapdata")
@RequiredArgsConstructor
public class MapApi {

    private final MapService mapService;
    private final LayerService layerService;
    private final MapDataService mapdataService;
    private final PointService pointService;

    /**
     * Create New MapData
     *
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateMapDataRequest request) {

        Map map = mapService.findByMapId(request.getMapId());
        Layer layer = layerService.findByLayerNameAndMapId(request.getLayerName(), request.getMapId());
        if(request.getLayerName() == null){
            request.setLayerName("Layer1");
        }

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
            layerService.save(map, newLayer);
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
                mapdataService.savePoint(layer, point);
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Delete MapData
     *
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody CreateMapDataRequest request){

        Map map = mapService.findByMapId(request.getMapId());
        Layer layer = layerService.findByLayerNameAndMapId(request.getLayerName(), request.getMapId());

        if (map == null || layer == null){
            // 지우는 요청이 유호하지 않은 맵 또는 레이어의 데이터일때
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(request.getMapDataType().equals("point")){
            Point point = pointService.findByGeometryAndLayerId(request.getGeometry(), layer.getId());
            pointService.delete(point);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get MapData Layer
     *
     * @param
     * @return
     */
    @GetMapping("/{mapId}")
    public ResponseEntity<?> getMapDataListByLayerId(@PathVariable Long mapId) {
        Map map = mapService.findByMapId(mapId);
        List<Layer> layerList = layerService.findByMapId(mapId);
        System.out.println("---------------------------------------------------------");
        System.out.println("MAP " + map.getLayers());
        System.out.println("---------------------------------------------------------");
//        Layer layer = layerService.findByLayerName(request.getLayerName());
//        List<MapData> mapDataList =  layer.getMapDataList();
        System.out.println("/////////////////////////////////////////////////////////");

        List<LayerDto> collect = layerList.stream()
                .map(layer -> new LayerDto(layer))
                .collect(Collectors.toList());

        System.out.println("/////////////////////////////////////////////////////////");
        return ResponseEntity.ok(new ApiResult(collect));
    }
}
