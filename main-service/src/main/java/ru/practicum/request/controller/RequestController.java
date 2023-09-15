package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    public ResponseEntity<RequestDto> createRequest(@PathVariable("userId") Long userId,
                                                    @RequestParam(required = false) Long eventId) {
        log.info("Add request userId: " + userId + " eventID: " + eventId);
        return new ResponseEntity<>(requestService.createRequest(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<RequestDto> updateRequest(@PathVariable("userId") Long userId,
                                                    @PathVariable("requestId") Long requestId) {
        log.info("Cancel request requestId: " + requestId + " userId: " + userId);
        return new ResponseEntity<>(requestService.updateRequestStatus(userId, requestId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<RequestDto>> getRequestsByUser(@PathVariable("userId") Long userId) {
        log.info("Get request by current user with strangers events");
        return new ResponseEntity<>(requestService.getRequestsByUser(userId), HttpStatus.OK);
    }
}
