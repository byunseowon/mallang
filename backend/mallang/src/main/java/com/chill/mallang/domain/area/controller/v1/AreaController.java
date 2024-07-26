package com.chill.mallang.domain.area.controller.v1;

import com.chill.mallang.domain.area.dto.AreaDTO;
import com.chill.mallang.domain.area.dto.AreaTopUserDTO;
import com.chill.mallang.domain.area.dto.ChallengeRecordDTO;
import com.chill.mallang.domain.area.service.AreaService;
import com.chill.mallang.domain.area.service.AreaTopUserService;
//import com.chill.mallang.domain.area.service.ChallengeRecordService;
import com.chill.mallang.domain.user.dto.TryCountDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/areas")
@Tag(name = "Area API",description = "점령지 상세정보 API")
public class AreaController {
    private static final Logger logger = LoggerFactory.getLogger(AreaController.class);

    @Autowired
    private AreaService areaService;

    @Autowired
    private AreaTopUserService areaTopUserService;

//    @Autowired
//    private ChallengeRecordService challengeRecordService;

    // 1. 점령자 대표 유저 정보 조회
    @GetMapping("/{area}/{userTeam}")
    public AreaTopUserDTO getAreaInfo(@PathVariable Long area, @PathVariable Long userTeam) {
        return areaTopUserService.getAreaInfo(area, userTeam);
    }

    // 2. 사용자 잔여 도전 횟수 조회
    @GetMapping("/try-count/{userId}")
    public ResponseEntity<TryCountDTO> getUserTryCountById(@PathVariable Long userId) {
        TryCountDTO tryCount = areaService.getUserTryCountById(userId);
        return new ResponseEntity<>(tryCount, HttpStatus.OK);
    }

    // 3. 도전 기록 조회
//    @GetMapping("/records/{areaId}/{userId}")
//    public ChallengeRecordDTO getGameRecords(@PathVariable Long areaId, @PathVariable Long userId) {
//        return challengeRecordService.getChallengeRecord(areaId, userId);
//    }


    // 4. 그냥 기본 기능 (test용)

    // 점령지 전체 조회
    @GetMapping
    public List<AreaDTO> getAllAreas() {
        logger.info("api test!!");
        return areaService.getAllAreas();
    }

    // 특정 점령지 조회
    @GetMapping("/{id}")
    public AreaDTO getAreaById(@PathVariable Long id) {

        return areaService.getAreaById(id);
    }
}