package com.capstone.goat.controller;

import com.capstone.goat.domain.User;
import com.capstone.goat.dto.response.NotificationResponseDto;
import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notification")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 조회", description = "사용자에게 온 알림을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationResponseDto.class)))),
    })
    @GetMapping
    public ResponseEntity<?> notificationList(@Schema(hidden = true) @AuthenticationPrincipal User user){

        log.info("알림 조회 id : {}",user.getId());

        List<NotificationResponseDto> notificationResponseDtoList = notificationService.getNotificationList(user.getId());

        return new ResponseEntity<>(new ResponseDto(notificationResponseDtoList,"성공"), HttpStatus.OK);
    }

    @Operation(summary = "알림 삭제", description = "사용자에게 온 알림을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "[UNAUTHORIZED] 권한이 없습니다. 본인만 삭제할 수 있습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> notificationRemove(@Schema(hidden = true) @AuthenticationPrincipal User user, @PathVariable Long notificationId){

        log.info("알림 삭제 id : {}", notificationId);

        notificationService.deleteNotification(user.getId(), notificationId);

        return new ResponseEntity<>(new ResponseDto(null,"성공"), HttpStatus.OK);
    }

    // 일반 Notification Endpoint
    /*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
    // SSE Notification Endpoint

    @Operation(summary = "SSE 알림 연결", description = "SSE 알림을 연결하고 지속적으로 알림을 받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연결 성공", content = @Content(schema = @Schema(implementation = SseEmitter.class), examples = {
                    @ExampleObject(name = "[SSE] connect", value = "[SSE] EventStream Created. [userId=1]"),
            })),
    })
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@Schema(hidden = true) @AuthenticationPrincipal User user) {
        return notificationService.connect(user.getId());
    }

    @Operation(summary = "SSE 알림 해제", description = "SSE 알림 연결을 해제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연결 해제 성공", content = @Content(schema = @Schema(implementation = Void.class))),
    })
    @DeleteMapping(value = "/disconnect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void disconnect(@Schema(hidden = true) @AuthenticationPrincipal User user) {
        notificationService.disconnect(user.getId());
    }
}
