package com.TaskSwap.services;

import com.TaskSwap.DTOs.SkillRequest;
import com.TaskSwap.DTOs.SkillResponse;
import com.TaskSwap.entities.Skill;
import com.TaskSwap.entities.User;
import com.TaskSwap.repositories.SkillRepository;
import com.TaskSwap.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    @Override
    public SkillResponse createSkill(SkillRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Skill skill = Skill.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .build();

        Skill saved = skillRepository.save(skill);

        return mapToResponse(saved);
    }

    @Override
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll()
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SkillResponse> getSkillsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return skillRepository.findByUser(user)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSkill(Long skillId, String username) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        if (!skill.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this skill");
        }

        skillRepository.delete(skill);
    }

    private SkillResponse mapToResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .title(skill.getTitle())
                .description(skill.getDescription())
                .postedBy(skill.getUser().getUsername())
                .createdAt(skill.getCreatedAt())
                .updatedAt(skill.getUpdatedAt())
                .build();
    }

    @Override
    public SkillResponse updateSkill(Long skillId, SkillRequest request, String username) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        if (!skill.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to update this skill");
        }

        skill.setTitle(request.getTitle());
        skill.setDescription(request.getDescription());

        Skill updatedSkill = skillRepository.save(skill);
        return mapToResponse(updatedSkill);
    }

}
