package com.api.regie.dto;

import com.api.regie.models.Campagnes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampagneRequest {

    private Campagnes campagne;
    
    private List<UUID> panneauxIds;
}
