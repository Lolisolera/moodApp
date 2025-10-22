package com.lola.moodapp.controller;

import com.lola.moodapp.dto.MoodRequest;
import com.lola.moodapp.dto.MoodResponse;
import com.lola.moodapp.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moods")
public class MoodController {

    private final MoodService moodService;

    @Autowired
    public MoodController(MoodService moodService) {
        this.moodService = moodService;
    }

    @PostMapping("/analyze")
    public MoodResponse analyzeMood(@RequestBody MoodRequest moodRequest) {
        return moodService.analyzeMood(moodRequest.getMood());
    }

}
