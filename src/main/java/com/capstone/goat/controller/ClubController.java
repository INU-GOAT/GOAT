package com.capstone.goat.controller;

import com.capstone.goat.domain.User;
import com.capstone.goat.dto.request.ClubSaveDto;
import com.capstone.goat.dto.request.ClubUpdateDte;
import com.capstone.goat.dto.response.ApplicantListResponseDto;
import com.capstone.goat.dto.response.ClubResponseDto;
import com.capstone.goat.dto.response.ResponseDto;
import com.capstone.goat.service.ClubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clubs")
@CrossOrigin(origins = "*")
public class ClubController {
    private final ClubService clubService;

    @Operation(summary = "클럽 생성", description = "url 헤더에 토큰을, 바디에 {name, sport}을 json형식으로 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "클럽 생성 성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400",description = "이미 가입된 클럽이 있습니다. / 이미 존재하는 클럽이름입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PostMapping("")
    public ResponseEntity<ResponseDto<Long>> createClub(@Valid @RequestBody ClubSaveDto clubSaveDto, @Schema(hidden = true)@AuthenticationPrincipal User user){
        log.info("클럽 생성 호출 id={}",user.getId());
        return new ResponseEntity<>(new ResponseDto<>(clubService.createClub(user,clubSaveDto),"클럽 생성 성공"), HttpStatus.CREATED);
    }

    @Operation(summary = "클럽 정보 변경", description = "url 헤더에 토큰을,파라미터에 클럽 id를,  바디에 {name, sport}을 json형식으로 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "클럽 정보 변경 성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "존재하지 않는 클럽입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401",description = "클럽장만이 권한이 있습니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PutMapping("/{clubId}")
    public ResponseEntity<ResponseDto<Long>> updateClub(@PathVariable Long clubId,@Valid @RequestBody ClubUpdateDte clubUpdateDte, @Schema(hidden = true)@AuthenticationPrincipal User user){
        log.info("클럽 정보 변경 호출 id={}",clubId);
        return new ResponseEntity<>(new ResponseDto<>(clubService.updateClub(user.getId(),clubId,clubUpdateDte),"클럽 정보 변경 성공"), HttpStatus.OK);
    }

    @Operation(summary = "클럽 삭제", description = "url 헤더에 토큰을,파라미터에 클럽 id를 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "클럽 삭제 성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "존재하지 않는 클럽입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401",description = "클럽장만이 권한이 있습니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @DeleteMapping("/{clubId}")
    public ResponseEntity<ResponseDto<Long>> deleteClub(@PathVariable Long clubId, @Schema(hidden = true)@AuthenticationPrincipal User user){
        log.info("클럽 삭제 호출 id={}",clubId);
        clubService.deleteClub(user.getId(),clubId);
        return new ResponseEntity<>(new ResponseDto<>(1L,"클럽 삭제 성공"), HttpStatus.OK);
    }

    @Operation(summary = "클럽에서 추방", description = "url 헤더에 토큰을,파라미터에 강퇴할 멤버의 memberId를 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "클럽에서 추방 성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "존재하지 않는 클럽입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401",description = "클럽장만이 권한이 있습니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PutMapping("/{clubId}/kick")
    public ResponseEntity<ResponseDto<Long>> kickInClub(@PathVariable Long clubId,@RequestParam Long memberId, @Schema(hidden = true)@AuthenticationPrincipal User user){
        log.info("클럽에서 추방 호출 id={}",clubId);
        return new ResponseEntity<>(new ResponseDto<>(clubService.kickClub(user.getId(),memberId,clubId),"클럽 삭제 성공"), HttpStatus.OK);
    }

    @Operation(summary = "클럽 정보 가져오기", description = "파라미터에 클럽 id를 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "클럽 정보 가져오기 성공",content = @Content(schema = @Schema(implementation = ClubResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "존재하지 않는 클럽입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @GetMapping("/{clubId}")
    public ResponseEntity<ResponseDto<ClubResponseDto>> getClub(@PathVariable Long clubId){
        log.info("클럽 정보 가져오기 호출 id={}",clubId);
        return new ResponseEntity<>(new ResponseDto<>(clubService.getClub(clubId),"클럽 정보 가져오기 성공"), HttpStatus.OK);
    }

    @Operation(summary = "클럽 가입 신청", description = "url 헤더에 토큰을, 파라미터에 클럽 id를 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "클럽 가입 신청 성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "존재하지 않는 클럽입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400",description = "이미 가입신청한 클럽이나, 가입된 클럽이있습니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))

    })
    @PostMapping("/applicant/{clubId}")
    public ResponseEntity<ResponseDto<Long>> applyClub(@PathVariable Long clubId,  @Schema(hidden = true)@AuthenticationPrincipal User user){
        log.info("클럽 가입 신청 신청자 id={}, 클럽 id = {}",user.getId(),clubId);
        return new ResponseEntity<>(new ResponseDto<>(clubService.applyClub(user.getId(),clubId),"클럽 가입 신청 성공"), HttpStatus.OK);
    }

    @Operation(summary = "클럽 가입 신청 수락/거절", description = "url 헤더에 토큰을, 파라미터변수에 클럽 id를 , 파라미터로 applicantId, accept 를 boolean 형태로 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "클럽 가입 신청 신청 수락/거절 성공",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400",description = "클럽에 가입 신청을 하지 않은 유저입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "존재하지 않는 클럽입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401",description = "클럽장만이 권한이 있습니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @PutMapping ("/applicant/{clubId}")
    public ResponseEntity<ResponseDto<Long>> acceptClubApply(@PathVariable Long clubId, @RequestParam Long applicantId, @RequestParam Boolean accept, @Schema(hidden = true)@AuthenticationPrincipal User user){
        log.info("클럽 가입 신청 수락/거절 신청자 id={}, 클럽 id = {}",user.getId(),clubId);
        return new ResponseEntity<>(new ResponseDto<>(clubService.acceptClub(user.getId(),applicantId,clubId,accept),"클럽 가입 신청 수락/거절 성공"), HttpStatus.OK);
    }

    @Operation(summary = "클럽 가입신청자 목록 가져오기", description = "파라미터에 클럽 id를 보내주세요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "클럽 가입신청자 목록 가져오기 성공",content = @Content(schema = @Schema(implementation = ApplicantListResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "존재하지 않는 클럽입니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @GetMapping("/applicant/{clubId}")
    public ResponseEntity<ResponseDto<List<ApplicantListResponseDto>>> getApplicant(@PathVariable Long clubId, @Schema(hidden = true)@AuthenticationPrincipal User user){
        log.info("클럽 가입 신청신청자 목록 가져오기 id = {}",clubId);
        return new ResponseEntity<>(new ResponseDto<>(clubService.getApplicantList(clubId),"클럽 가입 신청자 목록 가져오기 성공"), HttpStatus.OK);
    }




}
