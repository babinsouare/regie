package com.api.regie.dto;

import com.api.regie.models.Campagnes;
import com.api.regie.models.Devis;
import com.api.regie.models.Facture;
import com.api.regie.models.Panneaux;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampagneDetailsResponse {
    private Campagnes campagne;
    private List<Panneaux> panneaux;
    private List<Devis> devis;
    private List<Facture> factures;
}
