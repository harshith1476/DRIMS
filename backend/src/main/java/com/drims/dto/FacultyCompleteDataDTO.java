package com.drims.dto;

import lombok.Data;
import java.util.List;

@Data
public class FacultyCompleteDataDTO {
    private FacultyProfileDTO profile;
    private List<TargetDTO> targets;
    private List<JournalDTO> journals;
    private List<ConferenceDTO> conferences;
    private List<PatentDTO> patents;
    private List<BookChapterDTO> bookChapters;
}

