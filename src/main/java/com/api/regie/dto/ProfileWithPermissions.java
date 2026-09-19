package com.api.regie.dto;

import com.api.regie.models.Profils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileWithPermissions {

    private Profils profils;
    private List<PermissionsBody> permissions;

}
