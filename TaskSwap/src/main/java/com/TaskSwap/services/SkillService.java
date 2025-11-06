package com.TaskSwap.services;

import com.TaskSwap.DTOs.SkillRequest;
import com.TaskSwap.DTOs.SkillResponse;

import java.util.List;

public interface SkillService {

    SkillResponse createSkill(SkillRequest request, String username);

    List<SkillResponse> getAllSkills();

    List<SkillResponse> getSkillsByUser(String username);

    void deleteSkill(Long skillId, String username);

    SkillResponse updateSkill(Long skillId, SkillRequest request, String username);

}
