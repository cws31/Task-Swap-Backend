package com.TaskSwap.controllers;

import com.TaskSwap.DTOs.SkillRequest;
import com.TaskSwap.DTOs.SkillResponse;
import com.TaskSwap.services.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<SkillResponse> createSkill(
            @Valid @RequestBody SkillRequest request,
            Authentication authentication) {

        String username = authentication.getName(); // get logged-in user's username
        return ResponseEntity.ok(skillService.createSkill(request, username));
    }

    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @GetMapping("/my")
    public ResponseEntity<List<SkillResponse>> getMySkills(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(skillService.getSkillsByUser(username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillResponse> updateSkill(
            @PathVariable Long id,
            @Valid @RequestBody SkillRequest request,
            Authentication authentication) {

        String username = authentication.getName(); // logged-in user
        SkillResponse updated = skillService.updateSkill(id, request, username);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSkill(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();
        skillService.deleteSkill(id, username);
        return ResponseEntity.ok("Skill deleted successfully");
    }

}
